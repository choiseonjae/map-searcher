package com.searcher.mapsearcher.service

import com.searcher.mapsearcher.client.redis.ReactiveRedisClient
import com.searcher.mapsearcher.repository.KeywordHistory
import com.searcher.mapsearcher.repository.KeywordHistoryRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import org.springframework.stereotype.Service
import org.springframework.transaction.reactive.TransactionalOperator
import org.springframework.transaction.reactive.executeAndAwait

@Service
class KeywordHistoryService(
    private val keywordHistoryRepository: KeywordHistoryRepository,
    private val transactionalOperator: TransactionalOperator,
    private val reactiveRedisClient: ReactiveRedisClient,
    private val supervisorIoScope: CoroutineScope,
) {

    suspend fun asyncIncreaseSearchCount(keyword: String) {
        supervisorIoScope.launch { // 병렬
            reactiveRedisClient.lock("$VIEW_COUNT_LOCK::$keyword") { // 동시성 제어
                incrementSearchCount(keyword) // 검색 count + 1
            }
        }
    }

    private suspend fun incrementSearchCount(keyword: String) {
        transactionalOperator.executeAndAwait {
            with(keywordHistoryRepository) {
                when {
                    existsByKeyword(keyword) -> incrementSearchCount(keyword)
                    else -> save(KeywordHistory(keyword = keyword, searchCount = 1))
                }
            }
        }
    }

    suspend fun findTopNBySearchCount(n: Int): List<KeywordHistory> {
        return keywordHistoryRepository.findTopNBySearchCount(n).toList()
    }

    companion object {
        private const val VIEW_COUNT_LOCK = "MAP_SEARCHER::VIEW_COUNT_LOCK::"
    }
}
