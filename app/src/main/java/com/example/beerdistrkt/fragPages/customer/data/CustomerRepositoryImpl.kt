package com.example.beerdistrkt.fragPages.customer.data

import com.example.beerdistrkt.fragPages.customer.domain.CustomerRepository
import com.example.beerdistrkt.fragPages.customer.domain.model.Customer
import com.example.beerdistrkt.network.api.BaseRepository
import com.example.beerdistrkt.network.api.DistributionApi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class CustomerRepositoryImpl @Inject constructor(
    private val api: DistributionApi,
    private val customerMapper: CustomerMapper,
    ioDispatcher: CoroutineDispatcher,
): BaseRepository(ioDispatcher), CustomerRepository {

    private var customers: List<Customer> = emptyList()

    override val customersFlow: MutableStateFlow<List<Customer>?> = MutableStateFlow(null)

    override suspend fun refreshCustomers() {
        fetchCustomers()
    }

    override suspend fun putCustomer(customer: Customer): List<Customer> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteCustomer(customerID: Int): List<Customer> {
        TODO("Not yet implemented")
    }

    override suspend fun getCustomers(): List<Customer> {
        TODO("Not yet implemented")
    }

    override suspend fun getCustomer(customerID: Int): Customer {
        TODO("Not yet implemented")
    }

    private suspend fun fetchCustomers() {
        apiCall {
            api.getCustomers()
                .map(customerMapper::toDomain)
                .also {
                    customers = it
                    customersFlow.emit(customers)
                }
        }
    }
}