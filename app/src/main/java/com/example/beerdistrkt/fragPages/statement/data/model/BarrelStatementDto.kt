package com.example.beerdistrkt.fragPages.statement.data.model

import com.squareup.moshi.Json

data class BarrelStatementDto(
    val totalCount: Long,
    val statements: List<BarrelStatementItemDto>,
)

data class BarrelStatementItemDto(
    @Json(name = "dt")
    val dateStr: String,
    val countIn: Int,
    val countOut: Int,
    val canType: Int,
    val b50: Int,
    val b30: Int,
    val b20: Int,
    val b10: Int,
    val recId: Long,
    val comment: String?,
)