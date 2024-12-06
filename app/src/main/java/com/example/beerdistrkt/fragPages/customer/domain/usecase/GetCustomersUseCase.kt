package com.example.beerdistrkt.fragPages.customer.domain.usecase

import com.example.beerdistrkt.fragPages.customer.domain.CustomerRepository
import com.example.beerdistrkt.fragPages.customer.domain.model.Customer
import com.example.beerdistrkt.network.model.ResultState
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class GetCustomersUseCase @Inject constructor(
    private val customerRepository: CustomerRepository
) {
    suspend operator fun invoke() = customerRepository.getCustomers()

    fun customersAsFlow(): StateFlow<ResultState<List<Customer>>> = customerRepository.customersFlow
}