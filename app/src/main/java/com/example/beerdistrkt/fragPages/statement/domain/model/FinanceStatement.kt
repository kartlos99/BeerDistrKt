package com.example.beerdistrkt.fragPages.statement.domain.model

data class FinanceStatement(
    val totalCount: Long,
    val firstOperationDate: String,
    val statements: List<FStatement>,
)
