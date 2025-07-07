package com.example.beerdistrkt.fragPages.statement.data.model

import androidx.annotation.Keep
import com.example.beerdistrkt.fragPages.statement.domain.model.StatementRecordType
import com.squareup.moshi.Json

@Keep
data class FinanceStatementDto(
    val totalCount: Long,
    val statements: List<FinanceStatementItemDto>,
)

@Keep
data class FinanceStatementItemDto(
    @Json(name = "dt")
    val dateStr: String,
    @Json(name = "pr")
    val price: Double,
    val pay: Double,
    @Json(name = "bal")
    val balance: Double,
    val recId: Long,
    val recordType: StatementRecordType,
    val details: String?,
    val comment: String?,
)
