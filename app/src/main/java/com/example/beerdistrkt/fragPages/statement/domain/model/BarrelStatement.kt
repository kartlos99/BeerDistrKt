package com.example.beerdistrkt.fragPages.statement.domain.model

data class BarrelStatement(
    val totalCount: Long,
    val firstOperationDate: String,
    val statements: List<BarrelStatementItem>,
)
