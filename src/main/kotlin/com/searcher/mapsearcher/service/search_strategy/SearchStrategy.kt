package com.searcher.mapsearcher.service.search_strategy

interface SearchStrategy {
    suspend fun search(keyword: String): List<SearchResult>
}