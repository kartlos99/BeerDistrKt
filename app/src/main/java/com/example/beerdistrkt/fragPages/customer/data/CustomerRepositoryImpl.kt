package com.example.beerdistrkt.fragPages.customer.data

import com.example.beerdistrkt.fragPages.customer.domain.CustomerRepository
import com.example.beerdistrkt.fragPages.customer.domain.model.Customer
import com.example.beerdistrkt.network.api.BaseRepository
import com.example.beerdistrkt.network.api.DistributionApi
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject

@ActivityRetainedScoped
class CustomerRepositoryImpl @Inject constructor(
    private val api: DistributionApi,
    private val customerMapper: CustomerMapper,
    private val ioDispatcher: CoroutineDispatcher,
) : BaseRepository(ioDispatcher), CustomerRepository {

    private var customers: List<Customer> = emptyList()

    override val customersFlow: MutableStateFlow<List<Customer>?> = MutableStateFlow(null)

    override suspend fun refreshCustomers() {
        fetchCustomers()
    }

    override suspend fun putCustomer(customer: Customer): List<Customer> {
        apiCall {
            if (customer.id == null)
                api.addCustomer(customerMapper.toDto(customer))
        }
        fetchCustomers()
        return customers
    }

    override suspend fun deleteCustomer(customerID: Int): List<Customer> {
        TODO("Not yet implemented")
    }

    override suspend fun getCustomers(): List<Customer> {
        return customers
    }

    override suspend fun getCustomer(customerID: Int): Customer? {
        return customers.firstOrNull { it.id == customerID }
    }

    private suspend fun fetchCustomers() {
        apiCall {
            withContext(ioDispatcher) {
                val idleResult = async { api.getIdleInfo() }
                val customerResult = async { api.getCustomers() }
                val idlesMap = idleResult.await().groupBy { it.clientID }
                customerResult.await().map {
                    customerMapper.toDomain(it, idlesMap)
                }.also {
                    customers = it
                    customersFlow.emit(customers)
                }
            }
        }
    }
}