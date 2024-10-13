package com.searcher.mapsearcher.service

import kotlinx.coroutines.*
import mu.KotlinLogging
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import kotlin.system.measureNanoTime
import kotlin.time.Duration.Companion.seconds

@SpringBootTest
@Disabled("성능 테스트 용도")
class MapSearchStrategyServiceTest @Autowired constructor(
    private val mapSearchService: MapSearchService,
) {

    @Test
    fun `부하 테스트 - 캐시가 존재하는 경우`(): Unit = runBlocking {
        val keyword = "하나은행"
        val callSize = 10_000
        mapSearchService.searchKeyword(keyword) // cache 사전 적재
        delay(2.seconds)

        val spentTime = measureNanoTime {
            List(callSize) {
                launch(Dispatchers.Default) {
                    mapSearchService.searchKeyword(keyword)
                }
            }.joinAll()
        }

        // 경과 시간 (초 단위로 변환)
        val elapsedSeconds = spentTime.toDouble() / 1_000_000_000
        // TPS 계산 (요청 수 / 경과 시간)
        val tps = callSize / elapsedSeconds

        // 로그 출력
        log.info { "부하 테스트 완료 - 총 요청 수: $callSize, 경과 시간: ${"%.2f".format(elapsedSeconds)}초, TPS: ${"%.2f".format(tps)}" }
    }

    private val log = KotlinLogging.logger { }
}
