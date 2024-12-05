package com.example.beerdistrkt.fragPages.customer.domain.usecase

import com.example.beerdistrkt.fragPages.customer.domain.CustomerRepository
import com.example.beerdistrkt.fragPages.customer.domain.model.Customer
import com.example.beerdistrkt.network.api.ApiResponse
import javax.inject.Inject

class PutCustomersUseCase @Inject constructor(
    private val customerRepository: CustomerRepository
) {
    suspend operator fun invoke(customer: Customer): ApiResponse<List<Customer>> =
        customerRepository.putCustomer(customer)
}