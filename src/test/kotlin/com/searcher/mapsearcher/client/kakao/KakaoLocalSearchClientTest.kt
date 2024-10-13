package com.searcher.mapsearcher.client.kakao

import com.searcher.mapsearcher.config.CircuitBreakerConfig
import com.searcher.mapsearcher.config.WebClientConfig
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@SpringBootTest(
    classes = [
        KakaoLocalSearchClient::class,
        WebClientConfig::class,
        CircuitBreakerConfig::class,
    ]
)
class KakaoLocalSearchClientTest @Autowired constructor(
    private val kakaoLocalSearchClient: KakaoLocalSearchClient,
) {

    @Test
    fun `카카오 로컬 검색 API 호출 테스트`() = runBlocking {
        // given
        val query = "하나은행"
        val size = 10

        // when
        val response = kakaoLocalSearchClient.searchByKeyword(query, size)

        // then
        assert(response.documents.isNotEmpty())
    }

    @Test
    fun `실제 API 호출 테스트 - 존재하지 않는 장소 검색`() = runBlocking {
        // Given
        val query = "절대로존재하지않는장소이름123456789"

        // When
        val result = kakaoLocalSearchClient.searchByKeyword(query)

        assertNotNull(result)
        assertTrue(result.documents.isEmpty())
    }
}