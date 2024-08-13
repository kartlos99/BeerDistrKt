package com.example.beerdistrkt.fragPages.expense.presentation

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.beerdistrkt.BaseViewModel
import com.example.beerdistrkt.common.domain.model.EntityStatus
import com.example.beerdistrkt.fragPages.expense.domain.model.ExpenseCategory
import com.example.beerdistrkt.fragPages.expense.domain.usecase.GetExpenseCategoriesUseCase
import com.example.beerdistrkt.fragPages.expense.domain.usecase.PutExpenseCategoryUseCase
import com.example.beerdistrkt.network.api.ApiResponse
import com.example.beerdistrkt.network.api.DUPLICATE_ENTRY_API_ERROR_CODE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditExpenseViewModel @Inject constructor(
    private val putExpenseCategoryUseCase: PutExpenseCategoryUseCase,
    private val  getExpenseCategoriesUseCase: GetExpenseCategoriesUseCase,
) : BaseViewModel() {

    init {

        getCategories()
        addCategory()
    }

    private fun getCategories() {
        viewModelScope.launch {

            when (val result = getExpenseCategoriesUseCase()) {
                is ApiResponse.Error -> {
                    Log.d(TAG, "getCategories ER: ${result.message}")
                }
                is ApiResponse.Success -> {
                    Log.d(TAG, "getCategories SS: ${result.data}")
                }
            }
        }
    }

    private fun addCategory() {
        viewModelScope.launch {
            val result = putExpenseCategoryUseCase.invoke(
                ExpenseCategory(
                    null, "realCat", EntityStatus.ACTIVE, "#345678"
                )
            )
            when (result) {
                is ApiResponse.Error -> {
                    if (result.errorCode == DUPLICATE_ENTRY_API_ERROR_CODE) {
//                        already exists error
                    } else {
//                        base error
                    }
                    Log.d(TAG, "addCategory: ERRRR - ${result.message}")
                    Log.d(TAG, "addCategory: ERRRR - ${result.errorCode}")
                    Log.d(TAG, "addCategory: ERRRR - ${result.statusCode}")
                }
                is ApiResponse.Success -> {
                    val ss = result.data.toString()
                    Log.d(TAG, "addCategory: SUccsess $ss")
                }
            }
        }
    }
}