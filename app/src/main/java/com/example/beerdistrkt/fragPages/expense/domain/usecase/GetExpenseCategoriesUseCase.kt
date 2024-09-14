package com.example.beerdistrkt.fragPages.expense.domain.usecase

import com.example.beerdistrkt.fragPages.expense.domain.ExpenseRepository
import com.example.beerdistrkt.fragPages.expense.domain.model.ExpenseCategory
import com.example.beerdistrkt.network.model.ResultState
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class GetExpenseCategoriesUseCase @Inject constructor(
    private val repository: ExpenseRepository
) {

    operator fun invoke(): StateFlow<ResultState<List<ExpenseCategory>>> =
        repository.categoriesFlow.asStateFlow()

    suspend fun refresh() = repository.refreshCategories()

    val categories: List<ExpenseCategory>
        get() = repository.getCategoriesList()
}