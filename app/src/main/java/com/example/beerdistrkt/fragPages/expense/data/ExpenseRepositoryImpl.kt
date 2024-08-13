package com.example.beerdistrkt.fragPages.expense.data

import com.example.beerdistrkt.fragPages.expense.domain.ExpenseRepository
import com.example.beerdistrkt.fragPages.expense.domain.model.ExpenseCategory
import com.example.beerdistrkt.network.api.ApiResponse
import com.example.beerdistrkt.network.api.BaseRepository
import com.example.beerdistrkt.network.api.DistributionApi
import kotlinx.coroutines.CoroutineDispatcher

class ExpenseRepositoryImpl(
    private val api: DistributionApi,
    private val expenseCategoryMapper: ExpenseCategoryMapper,
    ioDispatcher: CoroutineDispatcher
) : BaseRepository(ioDispatcher), ExpenseRepository {

    override suspend fun putExpenseCategory(category: ExpenseCategory): ApiResponse<Any> {
        return apiCall {
            api.putExpenseCategory(expenseCategoryMapper.mapToDto(category))
        }
    }

    override suspend fun getCategories(): ApiResponse<List<ExpenseCategory>> {
        return apiCall { api.getExpenseCategories().map(expenseCategoryMapper::mapToDomain) }
    }
}