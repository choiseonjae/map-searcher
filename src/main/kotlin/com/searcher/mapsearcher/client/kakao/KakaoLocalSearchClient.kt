package com.searcher.mapsearcher.client.kakao

import com.searcher.mapsearcher.client.DEFAULT_RETRY
import io.github.resilience4j.circuitbreaker.CircuitBreaker
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import org.springframework.web.util.UriBuilder

@Service
class KakaoLocalSearchClient(
    @Value("\${servers.kakao.api-key}") private val apiKey: String,
    @Qualifier("kakaoMapWebClient") private val webClient: WebClient,
    @Qualifier("kakaoCircuitBreaker") private val circuitBreaker: CircuitBreaker,
) {

    suspend fun searchByKeyword(
        query: String,
        size: Int = 10
    ): KakaoSearchResponse {
        val generateUri = { uriBuilder: UriBuilder ->
            uriBuilder
                .path(SEARCH_KEYWORD_URL)
                .queryParam("query", query)
                .queryParam("size", size)
                .build()
        }

        return webClient.get()
            .uri(generateUri)
            .header("Authorization", "KakaoAK $apiKey")
            .retrieve()
            .bodyToMono<KakaoSearchResponse>()
            .transformDeferred(CircuitBreakerOperator.of(circuitBreaker))
            .retryWhen(DEFAULT_RETRY)
            .awaitSingle()
    }

    companion object {
        private const val SEARCH_KEYWORD_URL = "/v2/local/search/keyword.json"
    }
}