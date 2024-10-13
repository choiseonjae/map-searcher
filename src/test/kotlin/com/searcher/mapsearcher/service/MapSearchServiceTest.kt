package com.searcher.mapsearcher.service

import com.navercorp.fixturemonkey.FixtureMonkey
import com.navercorp.fixturemonkey.kotlin.KotlinPlugin
import com.navercorp.fixturemonkey.kotlin.giveMeBuilder
import com.searcher.mapsearcher.service.search_strategy.SearchResult
import com.searcher.mapsearcher.service.search_strategy.SearchStrategyService
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class MapSearchServiceTest {

    private val keywordHistoryService = mockk<KeywordHistoryService>(relaxed = true)
    private val searchStrategyService = mockk<SearchStrategyService>()
    private val mapSearchService: MapSearchService = MapSearchService(keywordHistoryService, searchStrategyService)
    private val fixtureMonkey = FixtureMonkey.builder()
        .plugin(KotlinPlugin())
        .build()


    @Test
    fun `중복 없는 10개, 10개 입력 시 각각 5개씩 추출`() = runTest {
        // given
        val searchResults1 = fixtureMonkey.giveMeBuilder<SearchResult>().sampleList(10)
            .mapIndexed { index, searchResult -> searchResult.copy(placeName = "PlaceA$index")  }
        val searchResults2 = fixtureMonkey.giveMeBuilder<SearchResult>().sampleList(10)
            .mapIndexed { index, searchResult -> searchResult.copy(placeName = "PlaceB$index")  }

        val resultList = listOf(searchResults1, searchResults2)

        coEvery { searchStrategyService.executeSearchStrategies(any()) } returns resultList

        // when
        val result = mapSearchService.searchKeyword("keyword")

        // then
        assertEquals(10, result.size)  // 중복이 없으므로 5개씩 총 10개 추출
        assertEquals(5, result.count { it.placeName.startsWith("PlaceA") })
        assertEquals(5, result.count { it.placeName.startsWith("PlaceB") })
    }

    @Test
    fun `2개 중복 있을 시 총 8개 추출`() = runTest {
        // given
        val searchResults1 = fixtureMonkey.giveMeBuilder<SearchResult>().sampleList(10)
            .mapIndexed { index, searchResult -> searchResult.copy(placeName = "PlaceA$index")  }
        val searchResults2 = fixtureMonkey.giveMeBuilder<SearchResult>().sampleList(10)
            .mapIndexed { index, searchResult -> searchResult.copy(placeName = "PlaceB$index")  }
            .toMutableList()
        searchResults2[0] = searchResults2[0].copy(placeName = "PlaceA0")
        searchResults2[1] = searchResults2[1].copy(placeName = "PlaceA1")
        val resultList = listOf(searchResults1, searchResults2)

        coEvery { searchStrategyService.executeSearchStrategies(any()) } returns resultList

        // when
        val result = mapSearchService.searchKeyword("keyword")

        // then
        assertEquals(8, result.size)  // 중복된 2개를 제외하고 8개 추출
        assertEquals(5, result.count { it.placeName.startsWith("PlaceA") })
        assertEquals(3, result.count { it.placeName.startsWith("PlaceB") })
    }

    @Test
    fun `특정 API 결과 부족할 시 총 10개로 계산`() = runTest {
        // given
        val searchResults1 = fixtureMonkey.giveMeBuilder<SearchResult>().sampleList(10)
            .mapIndexed { index, searchResult -> searchResult.copy(placeName = "PlaceA$index")  }
        val searchResults2 = fixtureMonkey.giveMeBuilder<SearchResult>().sampleList(3)
            .mapIndexed { index, searchResult -> searchResult.copy(placeName = "PlaceB$index")  }
            .toMutableList()
        val resultList = listOf(searchResults1, searchResults2)

        coEvery { searchStrategyService.executeSearchStrategies(any()) } returns resultList

        // when
        val result = mapSearchService.searchKeyword("keyword")

        // then
        assertEquals(10, result.size)  // 중복된 2개를 제외하고 8개 추출
        assertEquals(7, result.count { it.placeName.startsWith("PlaceA") })
        assertEquals(3, result.count { it.placeName.startsWith("PlaceB") })
    }
}
