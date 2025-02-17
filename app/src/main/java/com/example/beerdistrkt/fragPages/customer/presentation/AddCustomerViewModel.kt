package com.example.beerdistrkt.fragPages.customer.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.beerdistrkt.BaseViewModel
import com.example.beerdistrkt.fragPages.customer.domain.mapper.PriceMapper
import com.example.beerdistrkt.fragPages.customer.domain.model.Customer
import com.example.beerdistrkt.fragPages.customer.domain.model.PriceEditModel
import com.example.beerdistrkt.fragPages.customer.domain.usecase.GetCustomerUseCase
import com.example.beerdistrkt.fragPages.customer.domain.usecase.PutCustomersUseCase
import com.example.beerdistrkt.fragPages.login.models.AttachedRegion
import com.example.beerdistrkt.models.AttachRegionsRequest
import com.example.beerdistrkt.network.ApeniApiService
import com.example.beerdistrkt.network.api.ApiResponse
import com.example.beerdistrkt.network.api.toResultState
import com.example.beerdistrkt.network.model.ResultState
import com.example.beerdistrkt.utils.ApiResponseState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = AddCustomerViewModel.Factory::class)
class AddCustomerViewModel @AssistedInject constructor(
    private val priceMapper: PriceMapper,
    private val putCustomersUseCase: PutCustomersUseCase,
    private val getCustomerUseCase: GetCustomerUseCase,
    @Assisted val clientID: Int,
) : BaseViewModel() {

    val clientObjectLiveData =
        MutableLiveData<Triple<Customer?, List<PriceEditModel>, List<PriceEditModel>>>()
    val clientRegionsLiveData = MutableLiveData<ApiResponseState<List<AttachedRegion>>>()
    val regions = mutableListOf<AttachedRegion>()
    val selectedRegions = mutableListOf<AttachedRegion>()

    private val _customerFlow: MutableStateFlow<ResultState<Customer?>> =
        MutableStateFlow(ResultState.Success(null))
    val customerFlow: StateFlow<ResultState<Customer?>> = _customerFlow.asStateFlow()

    private var customer: Customer? = null

    init {
        viewModelScope.launch {
            if (clientID > 0) {
                customer = getCustomerUseCase(clientID)
                proceedCustomer(customer)
            } else {
                proceedCustomer(null)
            }
        }
    }

    private suspend fun proceedCustomer(customerData: Customer?) {

        clientObjectLiveData.value = Triple(
            customerData,
            priceMapper.getBeerPrices(customerData?.beerPrices),
            priceMapper.getBottlePrices(customerData?.bottlePrices)
        )
    }

    fun putCustomer(customer: Customer) {
        viewModelScope.launch {
            _customerFlow.emit(ResultState.Loading)
            when (val result = putCustomersUseCase(customer)) {
                is ApiResponse.Error -> _customerFlow.emit(result.toResultState())
                is ApiResponse.Success -> {
                    _customerFlow.emit(ResultState.Success(customer))
                }
            }
        }
    }

    fun getRegionForClient() {
        sendRequest(
            ApeniApiService.getInstance().getAttachedRegions(clientID),
            successWithData = {
                regions.clear()
                regions.addAll(it)
                clientRegionsLiveData.value =
                    ApiResponseState.Success(regions.filter { r -> r.isAttached })
            }
        )
    }

    fun getAllRegionNames(): Array<String> {
        return regions.map { it.name }.toTypedArray()
    }

    fun getSelectedRegions(): BooleanArray {
        selectedRegions.clear()
        selectedRegions.addAll(regions.filter { r -> r.isAttached })
        return regions.map { it.isAttached }.toBooleanArray()
    }

    fun setNewRegions() {
        val request = AttachRegionsRequest(
            clientID,
            selectedRegions.map { it.ID }
        )
        sendRequest(
            ApeniApiService.getInstance().setRegions(request),
            successWithData = {
                getRegionForClient()
            }
        )
    }

    @AssistedFactory
    interface Factory {
        fun create(clientID: Int): AddCustomerViewModel
    }
}
