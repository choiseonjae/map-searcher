package com.searcher.mapsearcher.controller

import com.searcher.mapsearcher.repository.KeywordHistory

data class MapSearchTop10Response(
    val results: List<KeywordSearchCount>
) {

    companion object {
        fun from(keywordHistories: List<KeywordHistory>) = MapSearchTop10Response(
            keywordHistories.map { KeywordSearchCount(it.keyword, it.searchCount) }
        )
    }

    data class KeywordSearchCount(
        val keyword: String,
        val searchCount: Long,
    )
}
