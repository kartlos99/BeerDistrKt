package com.example.beerdistrkt.fragPages.statement.domain.model

import com.example.beerdistrkt.utils.DATETIME_PATTERN
import com.example.beerdistrkt.utils.DOUBLE_PRECISION
import com.example.beerdistrkt.utils.daysBetween
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

sealed interface FStatement {

    val dateStr: String

    val date: Date?
        get() = SimpleDateFormat(DATETIME_PATTERN, Locale.getDefault()).parse(dateStr)

    val isSoldToday: Boolean
        get() = date?.daysBetween() == 0L

    data class SaleGroup(
        override val dateStr: String,
        val balance: Double,
        val comment: String?,
        val saleItems: List<SaleItem>,
    ) : FStatement {
        val isGift: Boolean
            get() = saleItems.sumOf { it.price } < DOUBLE_PRECISION
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