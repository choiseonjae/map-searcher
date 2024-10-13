package com.searcher.mapsearcher.client.naver

import com.searcher.mapsearcher.config.CircuitBreakerConfig
import com.searcher.mapsearcher.config.WebClientConfig
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(
    classes = [
        NaverLocalSearchClient::class,
        WebClientConfig::class,
        CircuitBreakerConfig::class,
    ]
)
class NaverLocalSearchClientTest @Autowired constructor(
    private val naverLocalSearchClient: NaverLocalSearchClient
) {

    @Test
    fun `실제 API 호출 테스트 - 검색 결과가 있어야 한다`() = runBlocking {
        // Given
        val query = "하나은행"

        // When
        val result = naverLocalSearchClient.searchByKeyword(query, 10)

        // Then
        assert(result?.items?.isNotEmpty() == true)
    }

    @Test
    fun `실제 API 호출 테스트 - 존재하지 않는 장소 검색`() = runBlocking {
        // Given
        val query = "절대로존재하지않는장소이름123456789"

        // When
        val result = naverLocalSearchClient.searchByKeyword(query)

        // Then
        assert(result?.items?.isEmpty() == true)
    }
}