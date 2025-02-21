package com.example.beerdistrkt.fragPages.bottle.presentation

import androidx.lifecycle.viewModelScope
import com.example.beerdistrkt.BaseViewModel
import com.example.beerdistrkt.fragPages.bottle.domain.model.Bottle
import com.example.beerdistrkt.fragPages.bottle.domain.usecase.GetBottlesUseCase
import com.example.beerdistrkt.fragPages.bottle.domain.usecase.RefreshBottlesUseCase
import com.example.beerdistrkt.network.model.ResultState
import com.example.beerdistrkt.network.model.asSuccessState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BottleListViewModel @Inject constructor(
    private val getBottlesUseCase: GetBottlesUseCase,
    private val refreshBottlesUseCase: RefreshBottlesUseCase,
) : BaseViewModel() {

    private val _bottlesFlow: MutableStateFlow<ResultState<List<Bottle>>> =
        MutableStateFlow(ResultState.Loading)
    val bottlesFlow: StateFlow<ResultState<List<Bottle>>> = _bottlesFlow.asStateFlow()

    init {
        viewModelScope.launch {
            retrieveBottles()
        }
    }

    private suspend fun retrieveBottles() {
        _bottlesFlow.emit(ResultState.Loading)
        getBottlesUseCase.asFlow()
            .map { result ->
                if (result is ResultState.Success)
                    result.data.filter { it.isVisible }
                        .sortedBy { it.sortValue }
                        .asSuccessState()
                else
                    result
            }
            .collectLatest {
                _bottlesFlow.emit(it)
            }


    }

    fun refresh() {
        viewModelScope.launch {
            refreshBottlesUseCase()
        }
    }

}