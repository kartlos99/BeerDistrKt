package com.example.beerdistrkt.fragPages.customer.domain.usecase

import com.example.beerdistrkt.fragPages.customer.domain.CustomerRepository
import javax.inject.Inject

class RefreshCustomersUseCase @Inject constructor(
    private val customerRepository: CustomerRepository
) {
    suspend operator fun invoke() = customerRepository.refreshCustomers()
}