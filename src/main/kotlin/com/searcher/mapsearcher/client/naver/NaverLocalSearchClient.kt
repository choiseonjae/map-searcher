package com.searcher.mapsearcher.client.naver

import com.searcher.mapsearcher.client.DEFAULT_RETRY
import io.github.resilience4j.circuitbreaker.CircuitBreaker
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator
import kotlinx.coroutines.reactor.awaitSingleOrNull
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import org.springframework.web.util.UriBuilder

@Component
class NaverLocalSearchClient(
    @Qualifier("naverMapWebClient") private val webClient: WebClient,
    @Qualifier("naverCircuitBreaker") private val circuitBreaker: CircuitBreaker,
    @Value("\${servers.naver.client-id}") private val clientId: String,
    @Value("\${servers.naver.client-secret}") private val clientSecret: String,
) {
    suspend fun searchByKeyword(
        query: String,
        display: Int = 10
    ): NaverLocalSearchResponse? {
        val generateUri = { uriBuilder: UriBuilder ->
            uriBuilder
                .path(SEARCH_KEYWORD_URL)
                .queryParam("query", query)
                .queryParam("display", display)
                .build()
        }

        return webClient.get()
            .uri(generateUri)
            .header("X-Naver-Client-Id", clientId)
            .header("X-Naver-Client-Secret", clientSecret)
            .retrieve()
            .bodyToMono<NaverLocalSearchResponse>()
            .transformDeferred(CircuitBreakerOperator.of(circuitBreaker))
            .retryWhen(DEFAULT_RETRY)
            .awaitSingleOrNull()
    }

    companion object {
        const val SEARCH_KEYWORD_URL = "/v1/search/local.json"
        private val log = KotlinLogging.logger { }
    }
}

