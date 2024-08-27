package com.example.beerdistrkt.fragPages.expense.domain.usecase

import com.example.beerdistrkt.fragPages.expense.domain.ExpenseRepository
import com.example.beerdistrkt.fragPages.expense.domain.model.ExpenseCategory
import com.example.beerdistrkt.network.api.ApiResponse
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class GetExpenseCategoriesUseCase @Inject constructor(
    private val repository: ExpenseRepository
) {

    operator fun invoke(): StateFlow<ApiResponse<List<ExpenseCategory>>> =
        repository.categoriesFlow.asStateFlow()
}