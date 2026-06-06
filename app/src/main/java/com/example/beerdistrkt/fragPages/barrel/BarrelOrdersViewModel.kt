package com.example.beerdistrkt.fragPages.barrel

import com.example.beerdistrkt.BaseViewModel
import com.example.beerdistrkt.utils.Session
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class BarrelOrdersViewModel@Inject constructor(
    override var session: Session,
) : BaseViewModel() {

    private val _stateFlow = MutableStateFlow(State())
    val stateFlow = _stateFlow.asStateFlow()

    init {
        loadBarrelOrders()
    }

    private fun loadBarrelOrders() {

    }

    data class State(
        val isLoading: Boolean = true,
        val items: List<String>? = null,
    )
}