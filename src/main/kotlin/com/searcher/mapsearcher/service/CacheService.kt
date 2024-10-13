package com.searcher.mapsearcher.service

import com.fasterxml.jackson.core.type.TypeReference
import com.searcher.mapsearcher.client.redis.ReactiveRedisClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.springframework.stereotype.Service
import kotlin.time.Duration

@Service
class CacheService(
    private val reactiveRedisClient: ReactiveRedisClient,
    private val supervisorIoScope: CoroutineScope
) {
    suspend fun <T : Any> getCache(
        cacheKey: String,
        type: TypeReference<T>,
        fallback: suspend () -> T,
    ): T {
        return reactiveRedisClient.getCache(cacheKey, type)
            ?: fallback.invoke()
    }

    suspend fun <T> asyncUpdateCache(cacheKey: String, value: T, duration: Duration) {
        supervisorIoScope.launch {
            reactiveRedisClient.updateCache(cacheKey, value, duration)
        }
    }
}
