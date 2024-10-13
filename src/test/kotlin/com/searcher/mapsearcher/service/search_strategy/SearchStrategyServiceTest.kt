package com.searcher.mapsearcher.service.search_strategy

import com.navercorp.fixturemonkey.FixtureMonkey
import com.navercorp.fixturemonkey.kotlin.KotlinPlugin
import com.navercorp.fixturemonkey.kotlin.giveMeBuilder
import com.ninjasquad.springmockk.MockkBean
import com.searcher.mapsearcher.client.redis.ReactiveRedisClient
import com.searcher.mapsearcher.config.CoroutineConfig
import com.searcher.mapsearcher.config.RedisConfig
import com.searcher.mapsearcher.config.WebClientConfig
import com.searcher.mapsearcher.service.CacheService
import io.mockk.coEvery
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.minutes

@SpringBootTest(
    classes = [
        SearchStrategyService::class,
        CacheService::class,
        CoroutineConfig::class,
        RedisConfig::class,
        WebClientConfig::class,
        ReactiveRedisClient::class,
    ]
)
class SearchStrategyServiceTest @Autowired constructor(
    private val searchStrategyService: SearchStrategyService,
    private val cacheService: CacheService,
    @MockkBean private val kakaoMapSearchStrategy: KakaoMapSearchStrategy,
    @MockkBean private val naverMapSearchStrategy: NaverMapSearchStrategy,
) {

    private val fixtureMonkey = FixtureMonkey.builder()
        .plugin(KotlinPlugin())
        .build()

    @Test
    fun `Cache MISS 및 API 오류 상황 시, DR 캐시 사용합니다`() = runTest {
        // given
        val searchResults = fixtureMonkey.giveMeBuilder<SearchResult>().sampleList(5)
        cacheService.asyncUpdateCache("DR::MAP_SEARCHER::KakaoMapSearchStrategy::keyword", searchResults, 5.minutes)
        cacheService.asyncUpdateCache("DR::MAP_SEARCHER::NaverMapSearchStrategy::keyword", searchResults, 5.minutes)

        coEvery { kakaoMapSearchStrategy.search(any()) } throws RuntimeException("API 호출 실패")
        coEvery { naverMapSearchStrategy.search(any()) } throws RuntimeException("API 호출 실패")

        // when
        val actual = searchStrategyService.executeSearchStrategies("keyword")

        // then
        assertEquals<List<List<SearchResult>>>(listOf(searchResults, searchResults), actual)
    }
}