package com.example.beerdistrkt.fragPages.expense.domain.usecase

import com.example.beerdistrkt.fragPages.expense.domain.ExpenseRepository
import com.example.beerdistrkt.fragPages.expense.domain.model.ExpenseCategory
import com.example.beerdistrkt.network.api.ApiResponse
import javax.inject.Inject

class PutExpenseCategoryUseCase @Inject constructor(
    private val repository: ExpenseRepository
) {

    suspend operator fun invoke(category: ExpenseCategory): ApiResponse<Any> {
        return repository.putExpenseCategory(category)
    }
}