package com.searcher.mapsearcher.service.search_strategy

import com.fasterxml.jackson.core.type.TypeReference
import com.searcher.mapsearcher.service.CacheService
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import mu.KotlinLogging
import org.springframework.stereotype.Service
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours

@Service
class SearchStrategyService(
    private val searchStrategies: List<SearchStrategy>,
    private val cacheService: CacheService
) {
    suspend fun executeSearchStrategies(keyword: String): List<List<SearchResult>> = coroutineScope {
        return@coroutineScope searchStrategies
            .map { async { cacheOrExecute(keyword, it) } } // 각 지도 API 병렬 조회
            .awaitAll()
    }

    // Cache Miss 시, API 호출
    private suspend fun cacheOrExecute(keyword: String, searcher: SearchStrategy): List<SearchResult> {
        val serviceName = searcher.javaClass.simpleName
        val cacheKey = "$serviceName$keyword"
        val type = object : TypeReference<List<SearchResult>>() {}
        return cacheService.getCache(
            cacheKey= "$ONE_HOUR_CACHE$cacheKey",
            type = type,
            fallback = { search(keyword, searcher) }
        )
    }

    // API 호출 실패 시, DR 캐시 체크
    private suspend fun search(keyword: String, searcher: SearchStrategy): List<SearchResult> {
        val serviceName = searcher.javaClass.simpleName
        val cacheKey = "$serviceName::$keyword"
        val type = object : TypeReference<List<SearchResult>>() {}
        val fallback = { throw NoSuchElementException("DR 캐시에서 지도 데이터를 찾을 수 없음 (cacheKey: $cacheKey)") }

        return runCatching {
            searcher.search(keyword).also { asyncUpdateCache (cacheKey, it)}
        }.getOrElse { // DR cache 조회
            cacheService.getCache("$CACHE_FOR_DR$cacheKey", type, fallback)
        }
    }

    // API 호출 후, 캐시 비동기 업데이트
    private suspend fun <T> asyncUpdateCache(cacheKey: String, value: T) {
        cacheService.asyncUpdateCache("$ONE_HOUR_CACHE$cacheKey", value, 1.hours)
        cacheService.asyncUpdateCache("$CACHE_FOR_DR$cacheKey", value, 1.days)
    }


    companion object {
        private const val ONE_HOUR_CACHE = "MAP_SEARCHER::"
        private const val CACHE_FOR_DR = "DR::MAP_SEARCHER::"
        private val log = KotlinLogging.logger { }
    }
}
