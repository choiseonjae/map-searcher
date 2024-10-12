package com.searcher.mapsearcher.repository

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table
class KeywordHistory(
    @Id
    val id: Long? = null,
    val keyword: String, // 검색 키워드
    val searchCount: Long = 0, // 검색 횟수
)
