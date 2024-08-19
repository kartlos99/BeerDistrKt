package com.example.beerdistrkt.fragPages.expense.presentation

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.beerdistrkt.BaseViewModel
import com.example.beerdistrkt.common.domain.model.EntityStatus
import com.example.beerdistrkt.fragPages.expense.domain.model.Expense
import com.example.beerdistrkt.fragPages.expense.domain.model.ExpenseCategory
import com.example.beerdistrkt.fragPages.expense.domain.usecase.GetExpenseCategoriesUseCase
import com.example.beerdistrkt.fragPages.expense.domain.usecase.PutExpenseCategoryUseCase
import com.example.beerdistrkt.fragPages.expense.domain.usecase.PutExpenseUseCase
import com.example.beerdistrkt.network.api.ApiResponse
import com.example.beerdistrkt.network.api.DUPLICATE_ENTRY_API_ERROR_CODE
import com.example.beerdistrkt.utils.Session
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class AddEditExpenseViewModel @Inject constructor(
    private val putExpenseCategoryUseCase: PutExpenseCategoryUseCase,
    private val getExpenseCategoriesUseCase: GetExpenseCategoriesUseCase,
    private val putExpenseUseCase: PutExpenseUseCase,
) : BaseViewModel() {

    private val _categoriesStateFlow = MutableStateFlow<List<ExpenseCategory>>(listOf())
    val categoriesStateFlow = _categoriesStateFlow.asStateFlow()

    private val _errorStateFlow = MutableStateFlow("")
    val errorStateFlow = _errorStateFlow.asStateFlow()

    private val _uiEventsFlow = MutableSharedFlow<AddExpenseUiEvent>()
    val uiEventsFlow = _uiEventsFlow.asSharedFlow()


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
        _categoriesStateFlow.value.firstOrNull {
            it.id == categoryID
        }?.let { category ->
            val expense = Expense(
                Session.get().userID!!,
                amount.toDouble(),
                comment,
                dateTimeFormat.format(Calendar.getInstance().time),
                category
            )
            putExpense(expense)
        }
            ?: _errorStateFlow.emit(ERROR_TEXT_NO_CATEGORY)
    }

    private suspend fun putExpense(expense: Expense) {
        when (val result = putExpenseUseCase(expense)) {
            is ApiResponse.Error -> {
                _errorStateFlow.value = "FAILED ${result.message}"
            }

            is ApiResponse.Success -> {
                _errorStateFlow.value = "DONE"
                delay(DELAY_FOR_NAVIGATION_BACK)
                _uiEventsFlow.emit(AddExpenseUiEvent.GoBack)
            }
        }
    }

    companion object {
        const val ERROR_TEXT_NO_CATEGORY = "აირჩიეთ კატეგორია!"
        const val DELAY_FOR_NAVIGATION_BACK = 1500L
    }
}