package com.searcher.mapsearcher.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.codec.json.Jackson2JsonDecoder
import org.springframework.http.codec.json.Jackson2JsonEncoder
import org.springframework.web.reactive.function.client.ExchangeStrategies
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class WebClientConfig {

    @Bean
    fun kakaoMapWebClient(@Value("\${servers.kakao.base-url}") baseUrl: String): WebClient =
        defaultWebClient(baseUrl)

    @Bean
    fun naverMapWebClient(@Value("\${servers.naver.base-url}") baseUrl: String): WebClient =
        defaultWebClient(baseUrl)

    private fun defaultWebClient(
        baseUrl: String
    ): WebClient {
        val exchangeStrategies = ExchangeStrategies.builder()
            .codecs { configurer ->
                configurer.defaultCodecs().jackson2JsonDecoder(Jackson2JsonDecoder(Jackson.snakeCaseObjectMapper))
                configurer.defaultCodecs().jackson2JsonEncoder(Jackson2JsonEncoder(Jackson.snakeCaseObjectMapper))
            }.build()

        return WebClient.builder()
            .baseUrl(baseUrl)
            .exchangeStrategies(exchangeStrategies)
            .build()
    }
}