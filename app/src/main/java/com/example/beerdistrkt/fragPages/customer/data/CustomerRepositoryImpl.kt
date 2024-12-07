package com.example.beerdistrkt.fragPages.customer.data

import com.example.beerdistrkt.fragPages.customer.data.model.CustomerDeactivationDto
import com.example.beerdistrkt.fragPages.customer.domain.CustomerRepository
import com.example.beerdistrkt.fragPages.customer.domain.model.Customer
import com.example.beerdistrkt.network.api.ApiResponse
import com.example.beerdistrkt.network.api.BaseRepository
import com.example.beerdistrkt.network.api.DistributionApi
import com.example.beerdistrkt.network.api.toResultState
import com.example.beerdistrkt.network.model.ResultState
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

    override val customersFlow: MutableStateFlow<ResultState<List<Customer>>> =
        MutableStateFlow(ResultState.Loading)

    override suspend fun refreshCustomers() {
        fetchCustomers()
    }

    override suspend fun putCustomer(customer: Customer): ApiResponse<List<Customer>> {
        val result = apiCall {
            if (customer.id == null)
                api.addCustomer(customerMapper.toDto(customer))
            else
                api.updateCustomer(customerMapper.toDto(customer))
        }
        if (result is ApiResponse.Success)
            fetchCustomers()

        return when (result) {
            is ApiResponse.Error -> result
            is ApiResponse.Success -> ApiResponse.Success(customers)
        }
    }

    override suspend fun deleteCustomer(customerID: Int): ApiResponse<String> {
        return apiCall {
            api.deactivateCustomer(CustomerDeactivationDto(customerID))
        }.also {
            if (it is ApiResponse.Success)
                fetchCustomers()
        }
    }

    override suspend fun getCustomers(): List<Customer> {
        return customers
    }

    override suspend fun getCustomer(customerID: Int): Customer? {
        return customers.firstOrNull { it.id == customerID }
    }

    private suspend fun fetchCustomers() {
        customersFlow.emit(ResultState.Loading)
        apiCall {
            withContext(ioDispatcher) {
                val idleResult = async { api.getIdleInfo() }
                val customerResult = async { api.getCustomers() }
                val idlesMap = idleResult.await().groupBy { it.clientID }
                customerResult.await().map {
                    customerMapper.toDomain(it, idlesMap)
                }.also {
                    customers = it
                }
            }
        }.also {
            customersFlow.emit(it.toResultState())
        }
    }
}