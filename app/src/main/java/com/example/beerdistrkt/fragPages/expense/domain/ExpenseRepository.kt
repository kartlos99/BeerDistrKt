package com.example.beerdistrkt.fragPages.expense.domain

import com.example.beerdistrkt.fragPages.expense.domain.model.Expense
import com.example.beerdistrkt.fragPages.expense.domain.model.ExpenseCategory
import com.example.beerdistrkt.network.api.ApiResponse

interface ExpenseRepository {

    suspend fun putExpenseCategory(category: ExpenseCategory): ApiResponse<Any>

    suspend fun putExpense(expense: Expense): ApiResponse<Any>

    suspend fun getCategories(force: Boolean): ApiResponse<List<ExpenseCategory>>
}