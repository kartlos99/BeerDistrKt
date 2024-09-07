package com.example.beerdistrkt.fragPages.expense.domain.model

data class Expense(
    val id: String?,
    val distributorID: String,
    val amount: Double,
    val comment: String,
    val date: String,
    val category: ExpenseCategory
)