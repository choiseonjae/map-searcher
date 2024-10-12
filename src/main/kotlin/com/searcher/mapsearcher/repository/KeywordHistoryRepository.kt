package com.searcher.mapsearcher.repository

import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Modifying
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface KeywordHistoryRepository : CoroutineCrudRepository<KeywordHistory, Long> {
    @Modifying
    @Query("UPDATE keyword_history SET search_count = search_count + 1 WHERE keyword = :keyword")
    suspend fun incrementSearchCount(keyword: String): Long

    suspend fun existsByKeyword(keyword: String): Boolean

    @Query("SELECT * FROM keyword_history ORDER BY search_count DESC LIMIT :n")
    fun findTopNBySearchCount(n: Int): Flow<KeywordHistory>
}