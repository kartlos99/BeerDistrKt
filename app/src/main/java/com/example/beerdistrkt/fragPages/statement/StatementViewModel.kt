package com.example.beerdistrkt.fragPages.statement

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.beerdistrkt.BaseViewModel
import com.example.beerdistrkt.fragPages.customer.domain.model.Customer
import com.example.beerdistrkt.fragPages.customer.domain.usecase.GetCustomerUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = StatementViewModel.Factory::class)
class StatementViewModel @AssistedInject constructor(
    private val getCustomerUseCase: GetCustomerUseCase,
    @Assisted val clientID: Int
) : BaseViewModel() {

    val clientLiveData = MutableLiveData<Customer>()

    init {
        getCustomer()
    }

    private fun getCustomer() {
        viewModelScope.launch {
            getCustomerUseCase.invoke(clientID)?.let { customer: Customer ->
                clientLiveData.value = customer
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(clientID: Int): StatementViewModel
    }
}
