package com.example.beerdistrkt.fragPages.expensecategory.presentation

import androidx.lifecycle.viewModelScope
import com.example.beerdistrkt.BaseViewModel
import com.example.beerdistrkt.common.domain.model.EntityStatus
import com.example.beerdistrkt.fragPages.expense.domain.model.ExpenseCategory
import com.example.beerdistrkt.fragPages.expense.domain.usecase.PutExpenseCategoryUseCase
import com.example.beerdistrkt.network.api.ApiResponse
import com.example.beerdistrkt.network.api.DUPLICATE_ENTRY_API_ERROR_CODE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExpenseCategoryViewModel @Inject constructor(
    private val putExpenseCategoryUseCase: PutExpenseCategoryUseCase,
) : BaseViewModel() {

    private val _screenStateFlow = MutableStateFlow(UiState())
    val screenStateFlow = _screenStateFlow.asStateFlow()


    fun onSaveClick(
        name: String?,
        status: EntityStatus?,
        color: String
    ) {
        when {
            name.isNullOrBlank() -> emitError(ERROR_MESSAGE_NO_CATEGORY_NAME)
            name.length < 3 -> emitError(ERROR_MESSAGE_SHORT_NAME)
            status == null -> emitError(ERROR_MESSAGE_NO_CATEGORY_IS_SET)
            else -> addCategory(
                ExpenseCategory(
                    null, name, status, color
                )
            )
        }
    }

    private fun emitError(msg: String?) = viewModelScope.launch {
        _screenStateFlow.emit(UiState(error = msg))
    }

    private fun addCategory(expenseCategory: ExpenseCategory) {
        viewModelScope.launch {
            when (val result = putExpenseCategoryUseCase.invoke(expenseCategory)) {
                is ApiResponse.Error -> {
                    if (result.errorCode == DUPLICATE_ENTRY_API_ERROR_CODE) {
                        _screenStateFlow.emit(UiState(error = ERROR_MESSAGE_FOR_DUPLICATE))
                    } else {
                        _screenStateFlow.emit(UiState(error = result.message))
                    }
                }

                is ApiResponse.Success -> {
                    _screenStateFlow.emit(UiState(successResult = true))
                }
            }
        }
    }

    companion object {
        const val ERROR_MESSAGE_FOR_DUPLICATE = "ამ დასახელების კატეგორია უკვე არსებობს!"
        const val ERROR_MESSAGE_NO_CATEGORY_IS_SET = "კატეგორია არ არის არჩეული!"
        const val ERROR_MESSAGE_NO_CATEGORY_NAME = "შეიყვანეთ კატეგორიის დასახელება!"
        const val ERROR_MESSAGE_SHORT_NAME = "დასახელება მინიმუმ 3 სიმბოლო!"
    }
}