package com.searcher.mapsearcher.service.search_strategy

import com.searcher.mapsearcher.client.kakao.KakaoLocalSearchClient
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

@Order(1)
@Component
class KakaoMapSearchStrategy(
    private val kakaoLocalSearchClient: KakaoLocalSearchClient,
) : SearchStrategy {
    override suspend fun search(keyword: String): List<SearchResult> {
        return kakaoLocalSearchClient.searchByKeyword(keyword).documents.map {
            SearchResult(
                placeName = it.placeName,
                category = it.categoryName,
                address = it.addressName,
                roadAddress = it.roadAddressName,
                x = it.x.toDouble(),
                y = it.y.toDouble(),
                link = it.placeUrl
            )
        }
    }
}