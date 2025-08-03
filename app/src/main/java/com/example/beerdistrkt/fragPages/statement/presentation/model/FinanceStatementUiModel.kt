package com.example.beerdistrkt.fragPages.statement.presentation.model

import androidx.annotation.DrawableRes
import com.example.beerdistrkt.fragPages.statement.domain.model.StatementRecordType
import com.example.beerdistrkt.orZero
import com.example.beerdistrkt.utils.DATETIME_PATTERN
import com.example.beerdistrkt.utils.DiffItem
import com.example.beerdistrkt.utils.daysBetween
import java.text.SimpleDateFormat
import java.util.Locale

data class FinanceStatementUiModel(
    val dateStr: String,
    val comment: String?,
    val price: Double,
    val pay: Double,
    val balance: Double,
    val recordType: StatementRecordType,
    val recordId: Long?,
    @DrawableRes
    val iconRes: Int? = null,
    val details: String? = null,
    val color: Int? = null,
) : DiffItem {

    override val key: String
        get() = "${ recordId.orZero() } ${recordType.name} $this"

    val isGift: Boolean
        get() = price == .0 && recordType.isSaleType()

    fun isSaleToday(): Boolean {
        val opDate = SimpleDateFormat(DATETIME_PATTERN, Locale.getDefault()).parse(dateStr) ?: return false
        return opDate.daysBetween() == 0L
    }
}
