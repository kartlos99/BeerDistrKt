package com.example.beerdistrkt.fragPages.expense.domain.usecase

import com.example.beerdistrkt.fragPages.expense.domain.ExpenseRepository
import com.example.beerdistrkt.network.api.ApiResponse
import javax.inject.Inject

class DeleteExpenseCategoryUseCase @Inject constructor(
    private val repository: ExpenseRepository
) {

    suspend operator fun invoke(categoryId: Int): ApiResponse<Any> {
        return repository.deleteExpenseCategory(categoryId)
    }
}