package com.searcher.mapsearcher.service.search_strategy

import com.searcher.mapsearcher.client.naver.NaverLocalSearchClient
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

@Order(2)
@Component
class NaverMapSearchStrategy(
    private val naverLocalSearchClient: NaverLocalSearchClient,
) : SearchStrategy {
    override suspend fun search(keyword: String): List<SearchResult> {
        return naverLocalSearchClient.searchByKeyword(keyword)?.items?.map {
            SearchResult(
                placeName = it.title.replace("<b>", "").replace("</b>", ""),
                category = it.category,
                address = it.address,
                roadAddress = it.roadAddress,
                x = it.mapx.toDouble(),
                y = it.mapy.toDouble(),
                link = it.link
            )
        } ?: emptyList()
    }
}