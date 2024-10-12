package com.searcher.mapsearcher.client.naver

import com.searcher.mapsearcher.client.redis.ReactiveRedisClient
import com.searcher.mapsearcher.config.*
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(
    classes = [
        NaverLocalSearchClient::class,
        RedisConfig::class,
        ReactiveRedisClient::class,
        WebClientConfig::class,
        CircuitBreakerConfig::class,
        CoroutineConfig::class,
    ]
)
//@Disabled("실제 API 호출 통합 테스트이므로, 필요시 만 활성화")
class NaverLocalSearchClientIntegrationTest @Autowired constructor(
    private val naverLocalSearchClient: NaverLocalSearchClient
) {

    private val log = KotlinLogging.logger { }

    @Test
    fun `실제 API 호출 테스트 - 검색 결과가 있어야 한다`() = runBlocking {
        // Given
        val query = "하나은행"

        // When
        val result = naverLocalSearchClient.searchByKeyword(query, 10)

        // Then
        log.info { Jackson.snakeCaseObjectMapper.writeValueAsString(result) }
        kotlin.test.assertNotNull(result)
        kotlin.test.assertTrue(result.items.isNotEmpty())
        kotlin.test.assertTrue(result.items.any { it.roadAddress?.contains("강남역") ?: true })
    }

    @Test
    fun `실제 API 호출 테스트 - 존재하지 않는 장소 검색`() = runBlocking {
        // Given
        val query = "절대로존재하지않는장소이름123456789"

        // When
        val result = naverLocalSearchClient.searchByKeyword(query)

        // Then
        log.info { result }
        kotlin.test.assertNotNull(result)
        kotlin.test.assertTrue(result.items.isEmpty())
    }
}