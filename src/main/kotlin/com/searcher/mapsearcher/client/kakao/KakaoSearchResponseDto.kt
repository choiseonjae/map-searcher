package com.searcher.mapsearcher.client.kakao

data class KakaoSearchResponse(
    val documents: List<Place>,
    val meta: Meta
) {
    data class Place(
        val id: String,
        val placeName: String,
        val categoryName: String,
        val categoryGroupCode: String,
        val categoryGroupName: String,
        val phone: String,
        val addressName: String,
        val roadAddressName: String,
        val x: String,
        val y: String,
        val placeUrl: String,
        val distance: String
    )

    data class Meta(
        val totalCount: Int,
        val pageableCount: Int,
        val isEnd: Boolean
    )
}
