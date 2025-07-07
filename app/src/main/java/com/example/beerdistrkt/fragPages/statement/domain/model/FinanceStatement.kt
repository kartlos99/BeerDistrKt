package com.example.beerdistrkt.fragPages.statement.domain.model

data class FinanceStatement(
    val totalCount: Long,
    val statements: List<FinanceStatementItem>,
)

data class FinanceStatementItem(
    val dateStr: String,
    val price: Double,
    val pay: Double,
    val balance: Double,
    val recId: Long,
    val recordType: StatementRecordType,
    val details: FinanceStatementDetails?,
    val comment: String?,
)
