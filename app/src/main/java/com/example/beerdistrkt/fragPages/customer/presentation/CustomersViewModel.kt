package com.example.beerdistrkt.fragPages.customer.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.beerdistrkt.BaseViewModel
import com.example.beerdistrkt.fragPages.customer.domain.model.Customer
import com.example.beerdistrkt.fragPages.customer.domain.usecase.GetCustomersUseCase
import com.example.beerdistrkt.models.ClientDeactivateModel
import com.example.beerdistrkt.network.ApeniApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CustomersViewModel @Inject constructor(
    private val getCustomersUseCase: GetCustomersUseCase,
) : BaseViewModel() {

    private var customers: List<Customer> = listOf()
    private val _customersLiveData = MutableLiveData<List<Customer>>()
    val customersLiveData: LiveData<List<Customer>>
        get() = _customersLiveData

    private val state = SavedStateHandle()

    val searchQuery = state.getLiveData("searchQuery", "")

    init {
        viewModelScope.launch {
            getCustomersUseCase.customersAsFlow().collectLatest {
                customers = it ?: emptyList()
                _customersLiveData.value = it
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

    fun onNewQuery(query: String) {
        _customersLiveData.value = customers.filter {
            it.name.contains(query)
        }
    }

    fun filterNotableItems(filtering: Boolean) {
        _customersLiveData.value =
            if (filtering) customers.filter { it.warnInfo != null }
            else customers
    }

    companion object {
        const val TAG = "CUSTOMER_LIST_VM"
    }
}
