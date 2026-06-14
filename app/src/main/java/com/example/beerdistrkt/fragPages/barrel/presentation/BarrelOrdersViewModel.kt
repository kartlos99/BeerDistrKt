package com.example.beerdistrkt.fragPages.barrel.presentation

import androidx.lifecycle.viewModelScope
import com.example.beerdistrkt.BaseViewModel
import com.example.beerdistrkt.fragPages.barrel.domain.usecase.GetEmptyBarrelsInfoUseCase
import com.example.beerdistrkt.fragPages.barrel.presentation.mapper.EmptyBarrelsUiMapper
import com.example.beerdistrkt.fragPages.barrel.presentation.model.EmptyBarrelUiModel
import com.example.beerdistrkt.network.api.ApiResponse
import com.example.beerdistrkt.utils.Session
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BarrelOrdersViewModel @Inject constructor(
    override var session: Session,
    private var getEmptyBarrelsInfoUseCase: GetEmptyBarrelsInfoUseCase,
    private val uiMapper: EmptyBarrelsUiMapper,
) : BaseViewModel() {

    private val _stateFlow = MutableStateFlow(State())
    val stateFlow = _stateFlow.asStateFlow()

    init {
        loadBarrelOrders()
    }

    private fun loadBarrelOrders() {
        viewModelScope.launch {
            _stateFlow.update { it.copy(isLoading = true) }
            when (val result = getEmptyBarrelsInfoUseCase("2026-05-29")) {
                is ApiResponse.Error -> _stateFlow.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = result.message ?: "ჩამოტვირთვის ხარვეზი"
                    )
                }

                is ApiResponse.Success -> _stateFlow.update {
                    it.copy(
                        isLoading = false,
                        items = result.data.map(uiMapper::mapToUI),
                        errorMessage = null,
                    )
                }
            }
        }
    }

    data class State(
        val isLoading: Boolean = true,
        val items: List<EmptyBarrelUiModel>? = null,
        val errorMessage: String? = null,
    )
}