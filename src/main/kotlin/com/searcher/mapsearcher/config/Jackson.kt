package com.searcher.mapsearcher.config

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object Jackson {
    val snakeCaseObjectMapper = createObjectMapper().apply {
        propertyNamingStrategy = PropertyNamingStrategies.SNAKE_CASE
    }

    private fun createObjectMapper() = jacksonObjectMapper().apply {
        registerModule(Jdk8Module())
        registerModule(isoDateModule()) // ISO 8601 형식 파싱
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false) // json에만 존재하는 필드 무시
        setDefaultPropertyInclusion(JsonInclude.Include.NON_NULL) // null은 직렬화 시 제외
    }

    private fun isoDateModule() = JavaTimeModule().apply {
        addSerializer(LocalDateTime::class.java, LocalDateTimeSerializer(DateTimeFormatter.ISO_DATE_TIME))
        addDeserializer(LocalDateTime::class.java, LocalDateTimeDeserializer(DateTimeFormatter.ISO_DATE_TIME))
    }
}