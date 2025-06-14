package com.example.beerdistrkt.fragPages.expense.data.model

import androidx.annotation.Keep

@Keep
data class ExpenseDto(
    val id: String?,
    val distributorID: String,
    val amount: Double,
    val comment: String,
    val date: String,
    val category: Int
)