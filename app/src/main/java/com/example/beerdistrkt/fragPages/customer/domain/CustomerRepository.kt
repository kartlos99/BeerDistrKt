package com.example.beerdistrkt.fragPages.customer.domain

import com.example.beerdistrkt.fragPages.customer.domain.model.Customer
import com.example.beerdistrkt.network.api.ApiResponse
import com.example.beerdistrkt.network.model.ResultState
import kotlinx.coroutines.flow.StateFlow

interface CustomerRepository {

    suspend fun refreshCustomers()

    suspend fun putCustomer(customer: Customer): ApiResponse<List<Customer>>

    suspend fun deleteCustomer(customerID: Int): ApiResponse<String>

    suspend fun getCustomers(): List<Customer>

    suspend fun getCustomer(customerID: Int): Customer?

    val customersFlow: StateFlow<ResultState<List<Customer>>>
}