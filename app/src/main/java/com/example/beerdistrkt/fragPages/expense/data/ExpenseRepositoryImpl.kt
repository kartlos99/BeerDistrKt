package com.example.beerdistrkt.fragPages.expense.data

import androidx.lifecycle.AtomicReference
import com.example.beerdistrkt.fragPages.expense.domain.ExpenseRepository
import com.example.beerdistrkt.fragPages.expense.domain.model.ExpenseCategory
import com.example.beerdistrkt.network.api.ApiResponse
import com.example.beerdistrkt.network.api.BaseRepository
import com.example.beerdistrkt.network.api.DistributionApi
import com.example.beerdistrkt.network.api.asSuccessResponse
import kotlinx.coroutines.CoroutineDispatcher

class ExpenseRepositoryImpl(
    private val api: DistributionApi,
    private val expenseCategoryMapper: ExpenseCategoryMapper,
    ioDispatcher: CoroutineDispatcher
) : BaseRepository(ioDispatcher), ExpenseRepository {

    private val categories = AtomicReference<List<ExpenseCategory>>()

    override suspend fun putExpenseCategory(category: ExpenseCategory): ApiResponse<Any> {
        return apiCall {
            api.putExpenseCategory(expenseCategoryMapper.mapToDto(category))
        }
    }

    override suspend fun getCategories(force: Boolean): ApiResponse<List<ExpenseCategory>> {
        return if (categories.get() != null)
            categories.get().asSuccessResponse()
        else
            apiCall {
                api.getExpenseCategories().map(expenseCategoryMapper::mapToDomain).also {
                    categories.set(it)
                }
            }
    }
}