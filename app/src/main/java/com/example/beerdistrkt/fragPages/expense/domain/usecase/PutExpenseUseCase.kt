package com.example.beerdistrkt.fragPages.expense.domain.usecase

import com.example.beerdistrkt.fragPages.expense.domain.ExpenseRepository
import com.example.beerdistrkt.fragPages.expense.domain.model.Expense
import com.example.beerdistrkt.network.api.ApiResponse
import javax.inject.Inject

class PutExpenseUseCase @Inject constructor(
    private val repository: ExpenseRepository
) {

    suspend operator fun invoke(expense: Expense): ApiResponse<Any> {
        return repository.putExpense(expense)
    }
}