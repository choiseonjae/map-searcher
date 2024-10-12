package com.searcher.mapsearcher.config

import io.github.resilience4j.circuitbreaker.CircuitBreaker
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

@Configuration
class CircuitBreakerConfig {

    @Bean
    fun kakaoCircuitBreaker(): CircuitBreaker =
        defaultCircularBreakerConfig("KakaoLocalSearchClient")

    @Bean
    fun naverCircuitBreaker(): CircuitBreaker =
        defaultCircularBreakerConfig("NaverLocalSearchClient")

    private fun defaultCircularBreakerConfig(name: String): CircuitBreaker {
        return CircuitBreakerConfig.custom()
            .failureRateThreshold(50f) // 실패율 임계값 50%
            .waitDurationInOpenState(5.seconds.toJavaDuration()) // 오픈 상태 유지 시간
            .slidingWindowSize(10) // 최근 요청 10개 체크
            .build()
            .let { CircuitBreaker.of(name, it) }
    }
}
