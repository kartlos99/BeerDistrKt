package com.example.beerdistrkt.fragPages.expensecategory.presentation

import androidx.lifecycle.viewModelScope
import com.example.beerdistrkt.BaseViewModel
import com.example.beerdistrkt.common.domain.model.EntityStatus
import com.example.beerdistrkt.fragPages.expense.domain.model.ExpenseCategory
import com.example.beerdistrkt.fragPages.expense.domain.usecase.PutExpenseCategoryUseCase
import com.example.beerdistrkt.network.api.ApiResponse
import com.example.beerdistrkt.network.api.DUPLICATE_ENTRY_API_ERROR_CODE
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = ExpenseCategoryViewModel.Factory::class)
class ExpenseCategoryViewModel @AssistedInject constructor(
    @Assisted private val category: ExpenseCategory,
    private val putExpenseCategoryUseCase: PutExpenseCategoryUseCase,
) : BaseViewModel() {

    private val _screenStateFlow = MutableStateFlow(UiState())
    val screenStateFlow = _screenStateFlow.asStateFlow()

    private val _categoryStatusesStateFlow = MutableStateFlow(listOf<Int>())
    val categoryStatusesStateFlow = _categoryStatusesStateFlow.asStateFlow()

    val categoryState = MutableStateFlow(category)

    init {
        viewModelScope.launch {
            _categoryStatusesStateFlow.emit(EntityStatus.entries
                .filter { it.value > "0" }
                .map { it.displayName })
        }
    }

    fun onSaveClick(
        name: String?,
        status: EntityStatus?,
    ) {
        when {
            name.isNullOrBlank() -> emitError(ERROR_MESSAGE_NO_CATEGORY_NAME)
            name.length < 3 -> emitError(ERROR_MESSAGE_SHORT_NAME)
            status == null -> emitError(ERROR_MESSAGE_NO_CATEGORY_IS_SET)
            else -> addCategory(
                ExpenseCategory(
                    category.id, name, status, categoryState.value.color
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

    fun setColor(color: Int)  {
        categoryState.update {
            it.copy(color = color)
        }
    }

    fun setName(name: String) {
        categoryState.update {
            it.copy(name = name)
        }
    }

    fun setStatus(status: EntityStatus?) = status?.let { validStatus ->
        categoryState.update {
            it.copy(status = validStatus)
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(category: ExpenseCategory): ExpenseCategoryViewModel
    }

    companion object {
        const val ERROR_MESSAGE_FOR_DUPLICATE = "ამ დასახელების კატეგორია უკვე არსებობს!"
        const val ERROR_MESSAGE_NO_CATEGORY_IS_SET = "კატეგორია არ არის არჩეული!"
        const val ERROR_MESSAGE_NO_CATEGORY_NAME = "შეიყვანეთ კატეგორიის დასახელება!"
        const val ERROR_MESSAGE_SHORT_NAME = "დასახელება მინიმუმ 3 სიმბოლო!"
    }
}