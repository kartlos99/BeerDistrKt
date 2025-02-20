package com.example.beerdistrkt.fragPages.bottle.presentation

import androidx.lifecycle.viewModelScope
import com.example.beerdistrkt.BaseViewModel
import com.example.beerdistrkt.fragPages.bottle.domain.model.Bottle
import com.example.beerdistrkt.fragPages.bottle.domain.usecase.GetBottlesUseCase
import com.example.beerdistrkt.network.model.ResultState
import com.example.beerdistrkt.network.model.asSuccessState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BottleListViewModel @Inject constructor(
    private val getBottlesUseCase: GetBottlesUseCase,
) : BaseViewModel() {

    private val _bottlesFlow: MutableStateFlow<ResultState<List<Bottle>>> =
        MutableStateFlow(ResultState.Loading)
    val bottlesFlow: StateFlow<ResultState<List<Bottle>>> = _bottlesFlow.asStateFlow()

    init {
        viewModelScope.launch {
            initBottles()
        }
    }

    private suspend fun initBottles() {
        _bottlesFlow.emit(
            getBottlesUseCase()
                .filter { it.isVisible }
                .sortedBy { it.sortValue }
                .asSuccessState()
        )
    }

    suspend fun getBottleList(): List<Bottle> = getBottlesUseCase()

}