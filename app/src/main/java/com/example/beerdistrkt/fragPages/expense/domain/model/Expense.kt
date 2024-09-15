package com.example.beerdistrkt.fragPages.expense.domain.model

import com.example.beerdistrkt.utils.DiffItem

data class Expense(
    val id: String?,
    val distributorID: String,
    val amount: Double,
    val comment: String,
    val date: String,
    val category: ExpenseCategory
): DiffItem {
    override val key: String?
        get() = id
}