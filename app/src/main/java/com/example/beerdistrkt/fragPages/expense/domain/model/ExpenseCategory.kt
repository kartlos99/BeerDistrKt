package com.example.beerdistrkt.fragPages.expense.domain.model

import com.example.beerdistrkt.common.domain.model.EntityStatus

data class ExpenseCategory(
    val id: Int? = null,
    val name: String,
    val status: EntityStatus,
    val color: String,
)
