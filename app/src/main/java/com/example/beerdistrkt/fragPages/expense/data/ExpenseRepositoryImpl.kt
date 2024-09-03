package com.example.beerdistrkt.fragPages.expense.data

import com.example.beerdistrkt.fragPages.expense.data.model.DeleteExpenseCategoryRequestDto
import com.example.beerdistrkt.fragPages.expense.domain.ExpenseRepository
import com.example.beerdistrkt.fragPages.expense.domain.model.Expense
import com.example.beerdistrkt.fragPages.expense.domain.model.ExpenseCategory
import com.example.beerdistrkt.network.api.ApiResponse
import com.example.beerdistrkt.network.api.BaseRepository
import com.example.beerdistrkt.network.api.DistributionApi
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@ActivityRetainedScoped
class ExpenseRepositoryImpl @Inject constructor(
    private val api: DistributionApi,
    private val expenseCategoryMapper: ExpenseCategoryMapper,
    private val expenseMapper: ExpenseMapper,
    ioDispatcher: CoroutineDispatcher
) : BaseRepository(ioDispatcher), ExpenseRepository {

    override val categoriesFlow: MutableStateFlow<ApiResponse<List<ExpenseCategory>>> =
        MutableStateFlow(ApiResponse.Success(listOf()))

    init {
        CoroutineScope(ioDispatcher).launch {
            getCategories(true)
        }
    }

    override suspend fun putExpenseCategory(category: ExpenseCategory): ApiResponse<Any> {
        return apiCall {
            api.putExpenseCategory(expenseCategoryMapper.mapToDto(category))
                .map(expenseCategoryMapper::mapToDomain)
        }.also {
            categoriesFlow.emit(it)
        }
    }

    override suspend fun deleteExpenseCategory(categoryId: Int): ApiResponse<Any> {
        return apiCall {
            api.deleteExpenseCategory(DeleteExpenseCategoryRequestDto(categoryId))
                .map(expenseCategoryMapper::mapToDomain)
        }.also {
            categoriesFlow.emit(it)
        }
    }

    override suspend fun putExpense(expense: Expense): ApiResponse<Any> {
        return apiCall {
            api.putExpense(expenseMapper.mapToDto(expense))
        }
    }

    override suspend fun refreshCategories() {
        getCategories(true)
    }

    private suspend fun getCategories(force: Boolean) {
        val categories = (categoriesFlow.value as? ApiResponse.Success)?.data
        if (categories.isNullOrEmpty() || force) {
            apiCall {
                api.getExpenseCategories().map(expenseCategoryMapper::mapToDomain)
            }.also {
                categoriesFlow.emit(it)
            }
        }
    }
}