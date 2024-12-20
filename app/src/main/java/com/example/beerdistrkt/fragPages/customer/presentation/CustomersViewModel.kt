package com.example.beerdistrkt.fragPages.customer.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.beerdistrkt.BaseViewModel
import com.example.beerdistrkt.fragPages.customer.domain.model.Customer
import com.example.beerdistrkt.fragPages.customer.domain.usecase.DeactivateCustomerUseCase
import com.example.beerdistrkt.fragPages.customer.domain.usecase.GetCustomersUseCase
import com.example.beerdistrkt.fragPages.customer.domain.usecase.RefreshCustomersUseCase
import com.example.beerdistrkt.fragPages.customer.presentation.model.CustomerListUiState
import com.example.beerdistrkt.network.api.toResultState
import com.example.beerdistrkt.network.model.ResultState
import com.example.beerdistrkt.network.model.asSuccessState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CustomersViewModel @Inject constructor(
    private val getCustomersUseCase: GetCustomersUseCase,
    private val refreshCustomersUseCase: RefreshCustomersUseCase,
    private val deactivateCustomerUseCase: DeactivateCustomerUseCase,
) : BaseViewModel() {

    private var customers: List<Customer> = listOf()

    private val _customersFlow: MutableStateFlow<ResultState<CustomerListUiState>> =
        MutableStateFlow(ResultState.Loading)
    val customersFlow: StateFlow<ResultState<CustomerListUiState>> = _customersFlow.asStateFlow()

    private val _deactivateFlow: MutableSharedFlow<ResultState<String>> = MutableSharedFlow()
    val deactivateFlow: SharedFlow<ResultState<String>> = _deactivateFlow.asSharedFlow()

    private val state = SavedStateHandle()

    val searchQuery = state.getLiveData("searchQuery", "")

    init {
        viewModelScope.launch {
            if (getCustomersUseCase.customersAsFlow().value is ResultState.Loading || getCustomersUseCase().isEmpty()) {
                refreshCustomersUseCase()
            }
            getCustomersUseCase.customersAsFlow().collectLatest { customersResult ->
                when (customersResult) {
                    ResultState.Loading -> _customersFlow.emit(ResultState.Loading)
                    is ResultState.Error -> _customersFlow.emit(customersResult)
                    is ResultState.Success -> {
                        customers = customersResult.data
                            .filter { it.isActive() }
                        _customersFlow.emit(CustomerListUiState(customers).asSuccessState())
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
        clientID?.let { id ->
            viewModelScope.launch {
                _customersFlow.emit(ResultState.Loading)
                _deactivateFlow.emit(
                    deactivateCustomerUseCase(id).toResultState()
                )
            }
        }
    }

    fun onNewQuery(query: String) = viewModelScope.launch {
        _customersFlow.emit(
            CustomerListUiState(
                customers.filter { it.name.contains(query) },
                true
            )
                .asSuccessState()
        )
    }

    fun filterNotableItems(filtering: Boolean) = viewModelScope.launch {
        _customersFlow.emit(
            CustomerListUiState(
                if (filtering)
                    customers.filter { it.warnInfo != null }
                else
                    customers,
                true
            )
                .asSuccessState()
        )
    }

    fun onRefresh() = viewModelScope.launch {
        refreshCustomersUseCase()
    }

    companion object {
        const val TAG = "CUSTOMER_LIST_VM"
    }
}
