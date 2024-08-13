package com.example.beerdistrkt.fragPages.expense.domain

import com.example.beerdistrkt.fragPages.expense.domain.model.ExpenseCategory
import com.example.beerdistrkt.network.api.ApiResponse

interface ExpenseRepository {

    suspend fun putExpenseCategory(category: ExpenseCategory): ApiResponse<Any>

    suspend fun getCategories(): ApiResponse<List<ExpenseCategory>>
}