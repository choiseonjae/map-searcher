package com.searcher.mapsearcher.service.search_strategy

data class SearchResult(
    val placeName: String,
    val category: String,
    val address: String,
    val roadAddress: String?,
    val x: Double,
    val y: Double,
    val link: String?
) {
    val placeNameWithoutWhiteSpace: String =
        placeName.replace("\\s".toRegex(), "")
}
