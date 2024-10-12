package com.searcher.mapsearcher.service

import com.searcher.mapsearcher.repository.KeywordHistory
import com.searcher.mapsearcher.service.search_strategy.SearchResult
import com.searcher.mapsearcher.service.search_strategy.SearchStrategyService
import kotlinx.coroutines.coroutineScope
import org.springframework.stereotype.Service

@Service
class MapSearchService(
    private val keywordHistoryService: KeywordHistoryService,
    private val searchStrategyService: SearchStrategyService,
) {

    suspend fun searchKeyword(keyword: String): List<SearchResult> = coroutineScope {
        // 검색 count + 1
        keywordHistoryService.asyncIncreaseSearchCount(keyword)

        // API 별 장소 정보 조회
        val searchResultsList = searchStrategyService.executeSearchStrategies(keyword)

        // 우선 순위 정렬
        return@coroutineScope sortedResult(searchResultsList)
    }

    private fun sortedResult(searchResultsList: List<List<SearchResult>>): List<SearchResult> {
        val finalResults = searchResultsList.filteringResult(10) // 총 10개의 Result 추출

        // 각 List 의 SearchResult들을 placeName 에서 공백을 제거한 후 Map 에 추가
        val resultMap = mutableMapOf<String, MutableList<SearchResult>>()
        finalResults.forEach { searchResults ->
            searchResults.forEach { searchResult ->
                val key = searchResult.placeNameWithoutWhiteSpace // 공백 제거
                resultMap.computeIfAbsent(key) { mutableListOf() }.add(searchResult)
            }
        }

        // 중복된 항목을 처리하여 상위에 배치
        val sortedResults = resultMap.values
            .filter { it.size > 1 }   // 중복된 항목들
            .map { it[0] } // 중복된 항목에서 첫 번째로 등장한 결과 선택

        // 중복되지 않은 항목을 각 리스트에서 채워 넣음
        val uniqueResults = finalResults.flatMap { searchResults ->
            searchResults.filter { searchResult ->
                val key = searchResult.placeNameWithoutWhiteSpace // 공백 제거
                resultMap[key]?.size == 1 // 중복되지 않은 항목
            }
        }

        // 중복된 항목 + 유일한 항목 순서대로 결과 반환
        return sortedResults + uniqueResults
    }

    // 총 10개의 Result 가지도록 filtering
    private fun List<List<SearchResult>>.filteringResult(totalCount: Int): List<List<SearchResult>> {
        var remainingCount = totalCount

        val prioritizedResults = this.map { searchResults ->
            searchResults.take(5) // 각 리스트에서 최대 5개씩 추출
                .also { remainingCount -= it.size }
        }

        val additionalResults = this.mapIndexed { index, searchResults ->
            val results = prioritizedResults[index].toMutableList()
            if (remainingCount > 0) {
                searchResults.drop(5).forEach { searchResult ->
                    // 중복되지 않고 남은 항목이 있다면 추가
                    if (remainingCount > 0 && results.none { it.placeName == searchResult.placeName }) {
                        results.add(searchResult)
                        remainingCount--
                    }
                }
            }
            results
        }

        return additionalResults
    }

    suspend fun findTop10SearchCount(): List<KeywordHistory> = keywordHistoryService.findTopNBySearchCount(10)
}