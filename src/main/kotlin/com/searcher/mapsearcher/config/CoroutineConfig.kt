package com.searcher.mapsearcher.config

import kotlinx.coroutines.*
import mu.KotlinLogging
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class CoroutineConfig {

    @Bean
    fun supervisorIoScope() = CoroutineScope(SupervisorJob() + COROUTINE_EXCEPTION_HANDLER + SERVICE_DISPATCHERS)

    companion object {
        private val log = KotlinLogging.logger { }
        // Dispatcher thread pool 공유 하지 않고 해당 scope 를 위한 격리된 pool
        @OptIn(ExperimentalCoroutinesApi::class)
        private val SERVICE_DISPATCHERS = Dispatchers.IO.limitedParallelism(100)
        private val COROUTINE_EXCEPTION_HANDLER = CoroutineExceptionHandler { context, throwable ->
            val coroutineName = context[CoroutineName]?.name ?: "Unknown"
            log.error(throwable) { "[$coroutineName] Coroutine Exception 발생" }
        }
    }
}