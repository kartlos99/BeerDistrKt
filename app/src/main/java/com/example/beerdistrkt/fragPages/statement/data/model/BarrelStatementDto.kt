package com.example.beerdistrkt.fragPages.statement.data.model

import androidx.annotation.Keep
import com.squareup.moshi.Json

@Keep
data class BarrelStatementDto(
    val totalCount: Long,
    val firstOperationDate: String,
    val statements: List<BarrelStatementItemDto>,
)

@Keep
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