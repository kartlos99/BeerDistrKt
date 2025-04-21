package com.example.beerdistrkt.fragPages.expense.domain.model

import com.example.beerdistrkt.fragPages.user.domain.model.User
import com.example.beerdistrkt.utils.DiffItem

data class Expense(
    val id: String?,
    val distributor: User,
    val amount: Double,
    val comment: String,
    val date: String,
    val category: ExpenseCategory
) : DiffItem {

    override val key: String?
        get() = id
}