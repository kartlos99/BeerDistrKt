package com.example.beerdistrkt.fragPages.expensecategory.presentation

import androidx.lifecycle.viewModelScope
import com.example.beerdistrkt.BaseViewModel
import com.example.beerdistrkt.common.domain.model.EntityStatus
import com.example.beerdistrkt.fragPages.expense.domain.model.ExpenseCategory
import com.example.beerdistrkt.fragPages.expense.domain.usecase.GetExpenseCategoriesUseCase
import com.example.beerdistrkt.network.api.ApiResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExpenseCategoryListViewModel @Inject constructor (
    private val getExpenseCategoriesUseCase: GetExpenseCategoriesUseCase,
): BaseViewModel() {

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

}