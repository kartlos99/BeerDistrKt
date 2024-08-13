package com.example.beerdistrkt.fragPages.expense.domain.usecase

import com.example.beerdistrkt.fragPages.expense.domain.ExpenseRepository
import com.example.beerdistrkt.fragPages.expense.domain.model.ExpenseCategory
import com.example.beerdistrkt.network.api.ApiResponse
import javax.inject.Inject

class GetExpenseCategoriesUseCase @Inject constructor(
    private val repository: ExpenseRepository
) {

    suspend operator fun invoke(force: Boolean = false): ApiResponse<List<ExpenseCategory>> {
        return repository.getCategories(force)
    }
}