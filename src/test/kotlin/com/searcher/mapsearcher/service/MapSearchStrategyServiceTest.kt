package com.searcher.mapsearcher.service

import com.fasterxml.jackson.core.type.TypeReference
import com.searcher.mapsearcher.client.redis.ReactiveRedisClient
import com.searcher.mapsearcher.config.Jackson
import com.searcher.mapsearcher.repository.KeywordHistoryRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class MapSearchStrategyServiceTest @Autowired constructor(
    private val mapSearchService: MapSearchService,
    private val keywordHistoryRepository: KeywordHistoryRepository,
    private val reactiveRedisClient: ReactiveRedisClient,
) {

    @Test
    fun `부하 테스트`(): Unit = runBlocking {
        val keyword = "하나은행"
        val result = mapSearchService.searchKeyword(keyword)
        delay(2000)
        log.info { "result: ${Jackson.snakeCaseObjectMapper.writeValueAsString(result)}" }

//        measureNanoTime {
//            List(1000) {
//                launch(Dispatchers.Default) {
//                    mapSearchService.searchKeyword(keyword)
//                }
//            }.joinAll()
//        }
//            .also {
//                log.info { "실행 시간: ${it / 1_000_000}ms" }
//            }

//        log.info { "DONE!" }

//        delay(3000)

        val type = object : TypeReference<Long>() {}
        reactiveRedisClient.getCache(keyword, type)?.let {
            log.info { "keyword: ${keyword}, searchCount: ${it}" }
        }
        keywordHistoryRepository.findAll().collect {
            log.info { "keyword: ${it.keyword}, searchCount: ${it.searchCount}" }
        }

        log.info { "PRINT DONE!" }
    }

    private val log = KotlinLogging.logger { }
}
