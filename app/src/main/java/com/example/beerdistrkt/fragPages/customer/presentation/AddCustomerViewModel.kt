package com.example.beerdistrkt.fragPages.customer.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.beerdistrkt.BaseViewModel
import com.example.beerdistrkt.common.domain.model.Goods
import com.example.beerdistrkt.fragPages.customer.domain.mapper.PriceMapper
import com.example.beerdistrkt.fragPages.customer.domain.model.Customer
import com.example.beerdistrkt.fragPages.customer.domain.model.CustomerGroup
import com.example.beerdistrkt.fragPages.customer.presentation.model.PriceEditModel
import com.example.beerdistrkt.fragPages.customer.domain.usecase.GetCustomerUseCase
import com.example.beerdistrkt.fragPages.customer.domain.usecase.PutCustomersUseCase
import com.example.beerdistrkt.fragPages.customer.presentation.model.CustomerUiModel
import com.example.beerdistrkt.fragPages.login.models.AttachedRegion
import com.example.beerdistrkt.fragPages.login.models.Permission
import com.example.beerdistrkt.models.AttachRegionsRequest
import com.example.beerdistrkt.network.ApeniApiService
import com.example.beerdistrkt.network.api.ApiResponse
import com.example.beerdistrkt.network.api.toResultState
import com.example.beerdistrkt.network.model.ResultState
import com.example.beerdistrkt.utils.ApiResponseState
import com.example.beerdistrkt.utils.Session
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = AddCustomerViewModel.Factory::class)
class AddCustomerViewModel @AssistedInject constructor(
    private val priceMapper: PriceMapper,
    private val putCustomersUseCase: PutCustomersUseCase,
    private val getCustomerUseCase: GetCustomerUseCase,
    override var session: Session,
    @Assisted val clientID: Int,
) : BaseViewModel() {

    val clientRegionsLiveData = MutableLiveData<ApiResponseState<List<AttachedRegion>>>()
    val regions = mutableListOf<AttachedRegion>()
    val selectedRegions = mutableListOf<AttachedRegion>()

    private val _customerFlow: MutableStateFlow<ResultState<Customer?>> =
        MutableStateFlow(ResultState.Success(null))
    val customerFlow: StateFlow<ResultState<Customer?>> = _customerFlow.asStateFlow()

    private val _customerStateFlow = MutableStateFlow(CustomerUiModel())
    val customerStateFlow: StateFlow<CustomerUiModel> = _customerStateFlow.asStateFlow()

    private val _availableGroupsFlow = MutableStateFlow(listOf<Int>())
    val availableGroupsFlow = _availableGroupsFlow.asStateFlow()

    init {
        viewModelScope.launch {
            _availableGroupsFlow.emit(
                CustomerGroup.entries
                    .map { it.displayName }
            )
            _customerStateFlow.emit(
                priceMapper.mapToUi(getCustomerUseCase(clientID))
            )
        }
        if (session.hasPermission(Permission.ManageRegion) && clientID > 0)
            getRegionForClient()
    }

    private fun putCustomer(customer: Customer) {
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

    private fun getRegionForClient() {
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

    private fun updatePrices(
        editingIndex: Int,
        value: String,
        index: Int,
        priceEditModel: PriceEditModel
    ): PriceEditModel {
        return if (index == editingIndex)
            priceEditModel.copy(price = value)
        else
            priceEditModel
    }

    fun onPriceInput(index: Int, priceStr: String, itemType: Goods) {
        _customerStateFlow.update { customer ->
            when (itemType) {
                Goods.BEER -> {
                    val newPrices = customer.beerPrices.mapIndexed { i, priceEditModel ->
                        updatePrices(index, priceStr, i, priceEditModel)
                    }
                    customer.copy(beerPrices = newPrices)
                }

                Goods.BOTTLE -> {
                    val newPrices = customer.bottlePrices.mapIndexed { i, priceEditModel ->
                        updatePrices(index, priceStr, i, priceEditModel)
                    }
                    customer.copy(bottlePrices = newPrices)
                }
            }
        }
    }

    fun saveChanges() {
        putCustomer(priceMapper.toDomain(_customerStateFlow.value))
    }

    fun onNameChange(name: CharSequence) = _customerStateFlow.update {
        it.copy(name = name.toString())
    }

    fun onIdentityCodeChange(identifyCode: CharSequence) = _customerStateFlow.update {
        it.copy(identifyCode = identifyCode.toString())
    }

    fun onContactPersonChange(contactPerson: CharSequence) = _customerStateFlow.update {
        it.copy(contactPerson = contactPerson.toString())
    }

    fun onAddressChange(address: CharSequence) = _customerStateFlow.update {
        it.copy(address = address.toString())
    }

    fun onPhoneChange(phone: CharSequence) = _customerStateFlow.update {
        it.copy(tel = phone.toString())
    }

    fun onCommentChange(comment: CharSequence) = _customerStateFlow.update {
        it.copy(comment = comment.toString())
    }

    fun onGroupChange(position: Int) = _customerStateFlow.update {
        it.copy(group = CustomerGroup.entries[position])
    }

    @AssistedFactory
    interface Factory {
        fun create(clientID: Int): AddCustomerViewModel
    }
}
