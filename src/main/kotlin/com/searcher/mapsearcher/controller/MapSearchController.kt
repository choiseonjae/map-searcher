package com.searcher.mapsearcher.controller

import com.searcher.mapsearcher.controller.dto.MapSearchResponse
import com.searcher.mapsearcher.controller.dto.MapSearchTop10Response
import com.searcher.mapsearcher.service.MapSearchService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/map")
@Tag(name = "지도 검색 API", description = "카카오, 네이버 장소 검색 API를 통합하여 제공하는 서비스")
class MapSearchController(
    private val mapSearchService: MapSearchService,
) {

    @Operation(
        summary = "키워드를 통한 장소 검색",
        description = "키워드를 이용해 카카오와 네이버 API를 통합하여 장소를 검색합니다."
    )
    @GetMapping("/{keyword}")
    suspend fun searchMap(
        @Parameter(description = "검색할 장소 키워드", example = "하나은행")
        @PathVariable keyword: String
    ): MapSearchResponse {
        return MapSearchResponse.from(mapSearchService.searchKeyword(keyword))
    }

    @Operation(
        summary = "검색 횟수가 높은 상위 10개의 키워드 조회",
        description = "검색 횟수가 높은 상위 10개의 키워드를 조회하여 반환합니다."
    )
    @GetMapping("/top10")
    suspend fun findTop10(): MapSearchTop10Response {
        return MapSearchTop10Response.from(mapSearchService.findTop10SearchCount())
    }
}
