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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditExpenseViewModel @Inject constructor(
    private val putExpenseCategoryUseCase: PutExpenseCategoryUseCase,
    private val getExpenseCategoriesUseCase: GetExpenseCategoriesUseCase,
) : BaseViewModel() {

    private val _categoriesStateFlow = MutableStateFlow<List<ExpenseCategory>>(listOf())
    val categoriesStateFlow = _categoriesStateFlow.asStateFlow()

    private val _errorStateFlow = MutableStateFlow("")
    val errorStateFlow = _errorStateFlow.asStateFlow()


    init {
        getCategories()
    }

    private fun getCategories() {
        viewModelScope.launch {
            when (val result = getExpenseCategoriesUseCase()) {
                is ApiResponse.Error -> {
                    _errorStateFlow.emit(result.message.orEmpty())
                }

                is ApiResponse.Success -> {
                    _categoriesStateFlow.emit(result.data.filter {
                        it.status == EntityStatus.ACTIVE
                    })
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

    fun onDoneClick(
        amount: String,
        comment: String,
        categoryID: Int
    ) = viewModelScope.launch {
        _errorStateFlow.value = ""
        if (categoryID == -1) {
            _errorStateFlow.value = ERROR_TEXT_NO_CATEGORY
        }

    }

    companion object {
        const val ERROR_TEXT_NO_CATEGORY = "აირჩიეთ კატეგორია!"
    }
}