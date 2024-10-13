package com.searcher.mapsearcher.service

import com.searcher.mapsearcher.client.redis.ReactiveRedisClient
import com.searcher.mapsearcher.repository.KeywordHistory
import com.searcher.mapsearcher.repository.KeywordHistoryRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.springframework.transaction.reactive.TransactionalOperator
import kotlin.test.assertEquals

class KeywordHistoryServiceTest {

    private val keywordHistoryRepository: KeywordHistoryRepository = mockk()
    private val reactiveRedisClient: ReactiveRedisClient = mockk()
    private val transactionalOperator: TransactionalOperator = mockk()
    private val keywordHistoryService = KeywordHistoryService(
        keywordHistoryRepository,
        transactionalOperator,
        reactiveRedisClient,
        TestScope()
    )

    @Test
    fun `findTopNBySearchCount는 상위 N개의 결과를 반환해야 한다`() = runTest {
        // given
        val n = 5
        val expectedKeywords = flowOf(
            KeywordHistory(1, "keyword1", 10),
            KeywordHistory(2, "keyword2", 9),
            KeywordHistory(3, "keyword3", 8)
        )
        coEvery { keywordHistoryRepository.findTopNBySearchCount(n) } returns expectedKeywords

        // when
        val result = keywordHistoryService.findTopNBySearchCount(n)

        // then
        assertEquals(expectedKeywords.toList(), result)
        coVerify { keywordHistoryRepository.findTopNBySearchCount(n) }
    }

}
