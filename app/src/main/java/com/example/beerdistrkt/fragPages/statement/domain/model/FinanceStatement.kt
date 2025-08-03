package com.example.beerdistrkt.fragPages.statement.domain.model

import com.example.beerdistrkt.utils.DOUBLE_PRECISION

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
) {
    fun isGift(): Boolean = price < DOUBLE_PRECISION
            && (
            recordType == StatementRecordType.SALE_BEER
                    || recordType == StatementRecordType.SALE_BOTTLE
            )
}
