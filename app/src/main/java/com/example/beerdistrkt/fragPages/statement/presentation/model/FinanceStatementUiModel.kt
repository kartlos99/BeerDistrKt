package com.example.beerdistrkt.fragPages.statement.presentation.model

import com.example.beerdistrkt.fragPages.statement.domain.model.StatementRecordType
import com.example.beerdistrkt.orZero
import com.example.beerdistrkt.utils.DiffItem


sealed interface FStatementUiItem : DiffItem {

    data class Sale(
        val dateStr: String,
        val comment: String?,
        val price: Double,
        val balance: Double,
        val isGift: Boolean,
        val isSoldToday: Boolean,
        val items: List<SaleItemUiModel>,
    ) : FStatementUiItem {

        var isExpanded = false

        override val key: String
            get() = dateStr
    }

    data class Money(
        val dateStr: String,
        val comment: String?,
        val pay: Double,
        val balance: Double,
        val recordId: Long?,
    ) : FStatementUiItem {

        override val key: String
            get() = "${recordId.orZero()}_$dateStr"
    }
}

data class SaleItemUiModel(
    val price: Double,
    val productName: String,
    val recordId: Long,
    val recordType: StatementRecordType,
    val itemColor: Int? = null,
    val details: String,
) : DiffItem {
    override val key: Any?
        get() = "$recordId-${recordType.name}"
}