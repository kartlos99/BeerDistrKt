package com.example.beerdistrkt.fragPages.expense.data

import com.example.beerdistrkt.fragPages.expense.domain.ExpenseRepository
import com.example.beerdistrkt.fragPages.expense.domain.model.ExpenseCategory
import com.example.beerdistrkt.network.api.ApiResponse
import com.example.beerdistrkt.network.api.BaseRepository
import com.example.beerdistrkt.network.api.DistributionApi
import com.example.beerdistrkt.network.api.asSuccessResponse
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

@ActivityRetainedScoped
class ExpenseRepositoryImpl @Inject constructor(
    private val api: DistributionApi,
    private val expenseCategoryMapper: ExpenseCategoryMapper,
    ioDispatcher: CoroutineDispatcher
) : BaseRepository(ioDispatcher), ExpenseRepository {

    private var categories: List<ExpenseCategory>? = null

    override suspend fun putExpenseCategory(category: ExpenseCategory): ApiResponse<Any> {
        return apiCall {
            api.putExpenseCategory(expenseCategoryMapper.mapToDto(category))
        }
    }

    override suspend fun getCategories(force: Boolean): ApiResponse<List<ExpenseCategory>> {
        return if (categories == null || force) {
            apiCall {
                api.getExpenseCategories().map(expenseCategoryMapper::mapToDomain).also {
                    categories = it
                }
            }
        } else {
            categories!!.asSuccessResponse()
        }
    }
}