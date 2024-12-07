package com.example.beerdistrkt.fragPages.customer.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.beerdistrkt.BaseViewModel
import com.example.beerdistrkt.fragPages.customer.domain.model.Customer
import com.example.beerdistrkt.fragPages.customer.domain.usecase.GetCustomersUseCase
import com.example.beerdistrkt.fragPages.customer.domain.usecase.RefreshCustomersUseCase
import com.example.beerdistrkt.models.ClientDeactivateModel
import com.example.beerdistrkt.network.ApeniApiService
import com.example.beerdistrkt.network.model.ResultState
import com.example.beerdistrkt.network.model.asSuccessState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CustomersViewModel @Inject constructor(
    private val getCustomersUseCase: GetCustomersUseCase,
    private val refreshCustomersUseCase: RefreshCustomersUseCase,
) : BaseViewModel() {

    private var customers: List<Customer> = listOf()

    private val _customersFlow: MutableStateFlow<ResultState<List<Customer>?>> =
        MutableStateFlow(ResultState.Loading)
    val customersFlow: StateFlow<ResultState<List<Customer>?>> = _customersFlow.asStateFlow()

    private val state = SavedStateHandle()

    val searchQuery = state.getLiveData("searchQuery", "")

    init {
        viewModelScope.launch {
            getCustomersUseCase.customersAsFlow().collectLatest { customersResult ->
                when (customersResult) {
                    ResultState.Loading -> _customersFlow.emit(customersResult)
                    is ResultState.Error -> _customersFlow.emit(customersResult)
                    is ResultState.Success -> {
                        customers = customersResult.data
                            .filter { it.isActive() }
                        _customersFlow.emit(customers.asSuccessState())
                    }
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }

    fun deactivateClient(clientID: Int?) {
        if (clientID == null)
            return
        sendRequest(
            ApeniApiService.getInstance().deactivateClient(ClientDeactivateModel(clientID)),
            success = {
                ioScope.launch {
                    database.deleteClient(clientID)
                }
            }
        )
    }

    fun onNewQuery(query: String) = viewModelScope.launch {
        _customersFlow.emit(
            customers
                .filter { it.name.contains(query) }
                .asSuccessState()
        )
    }

    fun filterNotableItems(filtering: Boolean) = viewModelScope.launch {
        _customersFlow.emit(
            (if (filtering) customers.filter { it.warnInfo != null }
            else customers).asSuccessState()
        )
    }

    fun onRefresh() = viewModelScope.launch {
        refreshCustomersUseCase()
    }

    companion object {
        const val TAG = "CUSTOMER_LIST_VM"
    }
}
