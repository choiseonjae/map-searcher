package com.searcher.mapsearcher.client

import org.springframework.web.reactive.function.client.WebClientResponseException
import reactor.util.retry.Retry
import reactor.util.retry.RetryBackoffSpec
import java.io.IOException
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

val DEFAULT_RETRY: RetryBackoffSpec = Retry.backoff(3, 1.seconds.toJavaDuration())
    .filter { throwable ->
        when (throwable) {
            is IOException -> true  // 네트워크 오류
            is WebClientResponseException -> {
                val statusCode = throwable.statusCode.value()
                statusCode in RETRY_ERROR // 요청이 많거나 서버 이슈일 경우 재시도
            }
            else -> false
        }
    }

private const val TOO_MANY_REQUEST = 429
private val SERVER_ERROR: IntRange = 500..599
private val RETRY_ERROR = setOf(TOO_MANY_REQUEST) + SERVER_ERROR
