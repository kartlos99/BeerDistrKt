package com.example.beerdistrkt.fragPages.customer.domain.usecase

import com.example.beerdistrkt.fragPages.customer.domain.CustomerRepository
import com.example.beerdistrkt.fragPages.customer.domain.model.Customer
import javax.inject.Inject

class GetCustomerUseCase @Inject constructor(
    private val customerRepository: CustomerRepository
) {
    suspend operator fun invoke(customerID: Int): Customer? = customerRepository.getCustomer(customerID)
}