package com.searcher.mapsearcher.client.kakao

import com.searcher.mapsearcher.client.redis.ReactiveRedisClient
import io.github.resilience4j.circuitbreaker.CircuitBreaker
import io.mockk.MockKAnnotations
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.springframework.web.reactive.function.client.WebClient

class KakaoLocalSearchClientTest {

    private val webClient: WebClient = mockk()
    private val reactiveRedisClient: ReactiveRedisClient = mockk()
    private val circuitBreaker: CircuitBreaker = mockk()

    private lateinit var kakaoLocalSearchClient: KakaoLocalSearchClient

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        kakaoLocalSearchClient = KakaoLocalSearchClient(
            apiKey = "test-api-key",
            webClient = webClient,
            circuitBreaker = circuitBreaker,
        )
    }

//    @Test
//    fun `API 호출 성공시 응답을 정상적으로 반환해야 한다`() = runBlocking {
//        // Given
//        val query = "테스트 쿼리"
//        val expectedResponse = KakaoSearchResponse(emptyList(), KakaoSearchResponse.Meta(0, true, 0))
//        mockWebClientResponse(expectedResponse)
//
//        // When
//        val result = kakaoLocalSearchClient.searchByKeyword(query)
//
//        // Then
//        assertEquals(expectedResponse, result)
//        coVerify { reactiveRedisClient.updateCacheIfAbsent(query, expectedResponse, any()) }
//    }
//
//    @Test
//    fun `API가 null을 반환할 때 캐시된 응답을 반환해야 한다`() = runBlocking {
//        // Given
//        val query = "테스트 쿼리"
//        val cachedResponse = KakaoSearchResponse(emptyList(), Meta(0, true, 0))
//        mockWebClientResponse(null)
//        coEvery { reactiveRedisClient.getCache(query, KakaoSearchResponse::class) } returns cachedResponse
//
//        // When
//        val result = kakaoLocalSearchClient.searchByKeyword(query)
//
//        // Then
//        assertEquals(cachedResponse, result)
//    }
//
//    @Test
//    fun `API가 null을 반환하고 캐시가 비어있을 때 NoSuchElementException을 던져야 한다`() = runBlocking {
//        // Given
//        val query = "테스트 쿼리"
//        mockWebClientResponse(null)
//        coEvery { reactiveRedisClient.getCache(query, KakaoSearchResponse::class) } returns null
//
//        // When & Then
//        assertThrows<NoSuchElementException> {
//            kakaoLocalSearchClient.searchByKeyword(query)
//        }
//    }
//
//    @Test
//    fun `API 호출 중 오류 발생시 캐시된 응답을 반환해야 한다`() = runBlocking {
//        // Given
//        val query = "테스트 쿼리"
//        val cachedResponse = KakaoSearchResponse(emptyList(), Meta(0, true, 0))
//        mockWebClientError()
//        coEvery { reactiveRedisClient.getCache(query, KakaoSearchResponse::class) } returns cachedResponse
//
//        // When
//        val result = kakaoLocalSearchClient.searchByKeyword(query)
//
//        // Then
//        assertEquals(cachedResponse, result)
//    }
//
//    @Test
//    fun `API 호출 중 오류 발생하고 캐시가 비어있을 때 NoSuchElementException을 던져야 한다`() = runBlocking {
//        // Given
//        val query = "테스트 쿼리"
//        mockWebClientError()
//        coEvery { reactiveRedisClient.getCache(query, KakaoSearchResponse::class) } returns null
//
//        // When & Then
//        assertThrows<NoSuchElementException> {
//            kakaoLocalSearchClient.searchByKeyword(query)
//        }
//    }
//
//    private fun mockWebClientResponse(response: KakaoSearchResponse?) {
//        val requestHeadersMock = mockk<WebClient.RequestHeadersSpec<*>>()
//        val responseMock = mockk<WebClient.ResponseSpec>()
//
//        every { webClient.get() } returns mockk {
//            every { uri(any<(UriBuilder) -> Unit>()) } returns requestHeadersMock
//            every { header(any(), any()) } returns requestHeadersMock
//        }
//        every { requestHeadersMock.retrieve() } returns responseMock
//        every { responseMock.bodyToMono<KakaoSearchResponse>() } returns Mono.justOrEmpty(response)
//        every { circuitBreaker.executeMonoWithFallback<KakaoSearchResponse>(any(), any()) } answers { firstArg<Mono<KakaoSearchResponse>>().toFuture().get() }
//    }
//
//    private fun mockWebClientError() {
//        val requestHeadersMock = mockk<WebClient.RequestHeadersSpec<*>>()
//        val responseMock = mockk<WebClient.ResponseSpec>()
//
//        every { webClient.get() } returns mockk {
//            every { uri(any<(UriBuilder) -> Unit>()) } returns requestHeadersMock
//            every { header(any(), any()) } returns requestHeadersMock
//        }
//        every { requestHeadersMock.retrieve() } returns responseMock
//        every { responseMock.bodyToMono<KakaoSearchResponse>() } returns Mono.error(RuntimeException("API Error"))
//        every { circuitBreaker.executeMonoWithFallback<KakaoSearchResponse>(any(), any()) } returns Mono.error(RuntimeException("API Error"))
//    }
}