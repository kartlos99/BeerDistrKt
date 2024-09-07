package com.example.beerdistrkt.fragPages.expense.domain

import com.example.beerdistrkt.fragPages.expense.domain.model.Expense
import com.example.beerdistrkt.fragPages.expense.domain.model.ExpenseCategory
import com.example.beerdistrkt.network.api.ApiResponse
import com.example.beerdistrkt.network.model.ResultState
import kotlinx.coroutines.flow.MutableStateFlow

interface ExpenseRepository {

    suspend fun putExpenseCategory(category: ExpenseCategory): ApiResponse<Any>

    suspend fun deleteExpenseCategory(categoryId: Int): ApiResponse<Any>

    suspend fun putExpense(expense: Expense): ApiResponse<Any>

    suspend fun refreshCategories()

    val categoriesFlow: MutableStateFlow<ResultState<List<ExpenseCategory>>>
}