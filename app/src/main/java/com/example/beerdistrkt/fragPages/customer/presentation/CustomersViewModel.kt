package com.example.beerdistrkt.fragPages.customer.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.beerdistrkt.BaseViewModel
import com.example.beerdistrkt.fragPages.customer.domain.model.Customer
import com.example.beerdistrkt.fragPages.customer.domain.usecase.DeactivateCustomerUseCase
import com.example.beerdistrkt.fragPages.customer.domain.usecase.GetCustomersUseCase
import com.example.beerdistrkt.fragPages.customer.domain.usecase.RefreshCustomersUseCase
import com.example.beerdistrkt.fragPages.customer.presentation.model.CustomerListUiState
import com.example.beerdistrkt.fragPages.customer.presentation.model.CustomerSortType
import com.example.beerdistrkt.fragPages.orders.repository.UserPreferencesRepository
import com.example.beerdistrkt.fragPages.settings.domain.model.SettingCode.IDLE_WARNING
import com.example.beerdistrkt.fragPages.settings.domain.usecase.GetSettingValueUseCase
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
    private val getSettingValueUseCase: GetSettingValueUseCase,
    private val userPreferencesRepository: UserPreferencesRepository,
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
            getCustomers()
        }
    }

    private suspend fun getCustomers() {
        getCustomersUseCase.customersAsFlow().collectLatest { customersResult ->
            when (customersResult) {
                ResultState.Loading -> _customersFlow.emit(ResultState.Loading)
                is ResultState.Error -> _customersFlow.emit(customersResult)
                is ResultState.Success -> {
                    val sortType = userPreferencesRepository.readCustomerSortType()
                    customers = customersResult.data
                        .filter { it.isActive() }
                        .map { customer ->
                            customer.copy(
                                warnInfo = customer.warnInfo?.takeIf { info ->
                                    info.passedDays > getSettingValueUseCase(IDLE_WARNING)
                                }
                            )
                        }
                    if (sortType != null)
                        arrangeCustomers(sortType)
                    else
                        _customersFlow.emit(CustomerListUiState(customers).asSuccessState())
                }
            }
        }
    }

    private suspend fun arrangeCustomers(sortType: CustomerSortType) {
        customers = when (sortType) {
            CustomerSortType.BY_NAME -> customers.sortedBy { it.name }
            CustomerSortType.BY_IDLE -> customers.sortedBy { it.warnInfo?.passedDays }
        }
        val query = searchQuery.value
        val list = if (query.isNullOrBlank())
            customers
        else
            customers.filter { it.name.contains(query) }

        _customersFlow.emit(
            CustomerListUiState(
                customers = list,
                isFiltered = !query.isNullOrBlank(),
                sortType = sortType
            ).asSuccessState()
        )
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

    fun sortCustomers(sortType: CustomerSortType) {
        viewModelScope.launch {
            arrangeCustomers(sortType)
            userPreferencesRepository.saveCustomerSortType(sortType)
        }
    }

    companion object {
        const val TAG = "CUSTOMER_LIST_VM"
    }
}
