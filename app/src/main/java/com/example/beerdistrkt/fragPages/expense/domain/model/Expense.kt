package com.example.beerdistrkt.fragPages.expense.domain.model

import android.os.Parcelable
import com.example.beerdistrkt.utils.DiffItem
import kotlinx.parcelize.Parcelize

@Parcelize
data class Expense(
    val id: String?,
    val distributorID: String,
    val amount: Double,
    val comment: String,
    val date: String,
    val category: ExpenseCategory
) : Parcelable, DiffItem {

    override val key: String?
        get() = id
}