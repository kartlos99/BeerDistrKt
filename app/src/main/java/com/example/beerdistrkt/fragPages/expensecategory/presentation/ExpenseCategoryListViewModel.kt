package com.example.beerdistrkt.fragPages.expensecategory.presentation

import androidx.lifecycle.viewModelScope
import com.example.beerdistrkt.BaseViewModel
import com.example.beerdistrkt.common.domain.model.EntityStatus
import com.example.beerdistrkt.fragPages.expense.domain.model.ExpenseCategory
import com.example.beerdistrkt.fragPages.expense.domain.usecase.GetExpenseCategoriesUseCase
import com.example.beerdistrkt.network.model.ResultState
import com.example.beerdistrkt.network.model.asSuccessState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExpenseCategoryListViewModel @Inject constructor(
    private val getExpenseCategoriesUseCase: GetExpenseCategoriesUseCase,
) : BaseViewModel() {

    private val _uiStateFlow: MutableStateFlow<ResultState<List<ExpenseCategory>>> =
        MutableStateFlow(ResultState.Loading)
    val uiStateFlow = _uiStateFlow.asStateFlow()


    init {
        observeCategories()
    }

    private fun observeCategories() = viewModelScope.launch {
        _uiStateFlow.emit(ResultState.Loading)
        getExpenseCategoriesUseCase.invoke()
            .map { result ->
                if (result is ResultState.Success)
                    result.data.filter {
                        it.status == EntityStatus.ACTIVE || it.status == EntityStatus.INACTIVE
                    }.asSuccessState()
                else result
            }
            .collectLatest { result ->
                _uiStateFlow.emit(result)
            }
    }

    fun refreshCategories() = viewModelScope.launch {
        getExpenseCategoriesUseCase.refresh()
    }
}