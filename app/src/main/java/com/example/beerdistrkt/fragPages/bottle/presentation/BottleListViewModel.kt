package com.example.beerdistrkt.fragPages.bottle.presentation

import androidx.lifecycle.viewModelScope
import com.example.beerdistrkt.BaseViewModel
import com.example.beerdistrkt.fragPages.bottle.domain.model.Bottle
import com.example.beerdistrkt.fragPages.bottle.domain.usecase.GetBottlesUseCase
import com.example.beerdistrkt.fragPages.bottle.domain.usecase.RefreshBottlesUseCase
import com.example.beerdistrkt.fragPages.bottle.domain.usecase.UpdateBottlePositionUseCase
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
    private val updateBottlePositionUseCase: UpdateBottlePositionUseCase,
) : BaseViewModel() {

    private val _bottlesFlow: MutableStateFlow<ResultState<List<Bottle>>> =
        MutableStateFlow(ResultState.Loading)
    val bottlesFlow: StateFlow<ResultState<List<Bottle>>> = _bottlesFlow.asStateFlow()

    private var blockAutoUpdate = false

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
                if (!blockAutoUpdate)
                    _bottlesFlow.emit(it)
            }
    }

    fun refresh() {
        blockAutoUpdate = false
        viewModelScope.launch {
            refreshBottlesUseCase()
        }
    }

    fun onItemMove(startPosition: Int, endPosition: Int) = viewModelScope.launch {
        blockAutoUpdate = true
        val bottles = getBottlesUseCase.invoke()
            .filter { it.isVisible }
            .sortedBy { it.sortValue }
        val newSortValue = when {
            endPosition == 0 -> (bottles.firstOrNull()?.sortValue ?: .0) - 1
            endPosition == bottles.lastIndex -> (bottles.lastOrNull()?.sortValue ?: .0) + 1
            startPosition < endPosition -> {
                (bottles[endPosition].sortValue + bottles[endPosition + 1].sortValue) / 2
            }

            startPosition > endPosition -> {
                (bottles[endPosition].sortValue + bottles[endPosition - 1].sortValue) / 2
            }

            else -> .0
        }
        updateBottlePositionUseCase.invoke(
            bottles[startPosition].id,
            newSortValue
        )
    }
}