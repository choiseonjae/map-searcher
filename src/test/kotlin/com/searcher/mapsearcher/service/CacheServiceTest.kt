package com.searcher.mapsearcher.service

import com.fasterxml.jackson.core.type.TypeReference
import com.searcher.mapsearcher.client.redis.ReactiveRedisClient
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import kotlin.time.Duration.Companion.minutes

@ExperimentalCoroutinesApi
class CacheServiceTest {

    private val reactiveRedisClient = mockk<ReactiveRedisClient>()
    private val supervisorIoScope = CoroutineScope(SupervisorJob())
    private val cacheService = CacheService(reactiveRedisClient, supervisorIoScope)

    @Test
    fun `캐시가 있을 때 getCache는 캐시된 값을 반환해야 한다`() = runTest {
        val cacheKey = "te stKey"
        val cachedValue = "cachedValue"
        val type = object : TypeReference<String>() {}

        coEvery { reactiveRedisClient.getCache<String>(any(), any(), any()) } returns cachedValue

        val result = cacheService.getCache(cacheKey, type) { "fallbackValue" }

        assertEquals(cachedValue, result)
    }

    @Test
    fun `캐시가 없을 때 getCache는 대체 함수를 호출해야 한다`() = runTest {
        val cacheKey = "testKey"
        val fallbackValue = "fallbackValue"
        val type = object : TypeReference<String>() {}

        coEvery { reactiveRedisClient.getCache<String>(any(), any(), any()) } returns null

        val result = cacheService.getCache(cacheKey, type) { fallbackValue }

        assertEquals(fallbackValue, result)
    }

    @Test
    fun `asyncUpdateCache는 캐시를 비동기적으로 업데이트해야 한다`() = runTest {
        val cacheKey = "testKey"
        val value = "testValue"
        val duration = 5.minutes

        coEvery { reactiveRedisClient.updateCache(cacheKey, value, duration) } returns true

        cacheService.asyncUpdateCache(cacheKey, value, duration)

        advanceUntilIdle()

        coVerify(exactly = 1) { reactiveRedisClient.updateCache(cacheKey, value, duration) }
    }
}