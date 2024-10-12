package com.searcher.mapsearcher.controller

import com.searcher.mapsearcher.service.search_strategy.SearchResult

data class MapSearchResponse(
    val results: List<MapSearch>
) {
    companion object {
        fun from(searchResults: List<SearchResult>) = MapSearchResponse(
            results = searchResults.map {
                MapSearch(
                    placeName = it.placeName,
                    category = it.category,
                    address = it.address,
                    roadAddress = it.roadAddress,
                    x = it.x,
                    y = it.y,
                    link = it.link
                )
            }
        )
    }

    data class MapSearch(
        val placeName: String,
        val category: String,
        val address: String,
        val roadAddress: String?,
        val x: Double,
        val y: Double,
        val link: String?,
    )
}