package com.example.beerdistrkt.fragPages.customer.domain.usecase

import com.example.beerdistrkt.fragPages.customer.domain.CustomerRepository
import com.example.beerdistrkt.network.api.ApiResponse
import javax.inject.Inject

class DeactivateCustomerUseCase @Inject constructor(
    private val customerRepository: CustomerRepository
) {
    suspend operator fun invoke(customerID: Int): ApiResponse<String> =
        customerRepository.deleteCustomer(customerID)
}