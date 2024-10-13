package com.searcher.mapsearcher.client.redis

import com.fasterxml.jackson.core.type.TypeReference
import com.searcher.mapsearcher.config.Jackson
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.slf4j.MDCContext
import kotlinx.coroutines.withContext
import mu.KotlinLogging
import org.redisson.api.RedissonReactiveClient
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit
import kotlin.time.toJavaDuration

@Component
class ReactiveRedisClient(
    private val reactiveRedissonClient: RedissonReactiveClient
) {

    suspend fun <T> updateCache(
        key: String,
        value: T,
        ttl: Duration,
        serialize: (T) -> String = { Jackson.snakeCaseObjectMapper.writeValueAsString(it) }
    ): Boolean = runCatching {
        val bucket = reactiveRedissonClient.getBucket<String>(key)
        bucket.set(serialize(value), ttl.toJavaDuration()).awaitSingleOrNull()
        true
    }.onFailure {
        log.error(it) { "캐시 적재 실패 (key: $key)" }
    }.getOrDefault(false)

    suspend fun <T : Any> getCache(
        key: String,
        type: TypeReference<T>,
        deserialize: (String) -> T = { Jackson.snakeCaseObjectMapper.readValue(it, type) }
    ): T? = runCatching {
        val bucket = reactiveRedissonClient.getBucket<String>(key)
        val value = bucket.get().awaitFirstOrNull()
        value?.let { deserialize(it) }
    }.onFailure {
        log.error(it) { "캐시 조회 실패 (key: $key)" }
    }.getOrNull()

    suspend fun lock(
        key: String,
        waitTime: Duration = 10.seconds, // 락을 시도하는 대기 시간
        leaseTime: Duration = 1.seconds, // 락이 유지되는 시간
        action: suspend () -> Unit
    ): Unit = withContext(MDCContext() + singleThreadContext) {
        runCatching {
            val lock = reactiveRedissonClient.getLock(key)
            val isLock = lock.tryLock(
                waitTime.toLong(DurationUnit.MILLISECONDS),
                leaseTime.toLong(DurationUnit.MILLISECONDS),
                TimeUnit.MILLISECONDS
            ).awaitSingle()

            if (isLock) {
                runCatching { action() }
                    .onFailure { log.error(it) { "lock 도중 처리 실패 (key: $key)" } }
                lock.forceUnlock().awaitSingleOrNull()
            }
        }.onFailure {
            log.error(it) { "Lock 실패 (key: $key)" }
        }
    }

    companion object {
        private val log = KotlinLogging.logger { }
        @OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)
        val singleThreadContext = newSingleThreadContext("LockThread")
    }
}