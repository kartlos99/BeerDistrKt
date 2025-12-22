package com.example.beerdistrkt.fragPages.statement.domain.model

import com.example.beerdistrkt.utils.DATETIME_PATTERN
import com.example.beerdistrkt.utils.DOUBLE_PRECISION
import com.example.beerdistrkt.utils.daysBetween
import java.text.SimpleDateFormat
import java.util.Locale

sealed interface FStatement {

    val dateStr: String

    data class SaleGroup(
        override val dateStr: String,
        val balance: Double,
        val comment: String?,
        val saleItems: List<SaleItem>,
    ) : FStatement {
        val isGift: Boolean
            get() = saleItems.sumOf { it.price } < DOUBLE_PRECISION

        val isSoldToday : Boolean
            get() {
                val opDate = SimpleDateFormat(DATETIME_PATTERN, Locale.getDefault()).parse(dateStr) ?: return false
                return opDate.daysBetween() == 0L
            }
    }

    data class PayMoney(
        override val dateStr: String,
        val amount: Double,
        val balance: Double,
        val recordId: Long,
        val recordType: StatementRecordType,
        val comment: String?,
    ) : FStatement
}

data class SaleItem(
    val price: Double,
    val recordId: Long,
    val recordType: StatementRecordType,
    val details: FinanceStatementDetails?,
)