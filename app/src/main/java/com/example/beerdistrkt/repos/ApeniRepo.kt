package com.example.beerdistrkt.repos

import androidx.lifecycle.LiveData
import com.example.beerdistrkt.db.ApeniDataBase
import com.example.beerdistrkt.db.ApeniDatabaseDao
import com.example.beerdistrkt.models.BeerModelBase
import com.example.beerdistrkt.models.CustomerIdlInfo
import com.example.beerdistrkt.models.ObiectWithPrices
import com.example.beerdistrkt.models.Obieqti
import com.example.beerdistrkt.network.ApeniApiService
import com.example.beerdistrkt.sendRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ApeniRepo {

    private val ioScope = CoroutineScope(Dispatchers.IO)
    private val database: ApeniDatabaseDao = ApeniDataBase.getInstance().apeniDataBaseDao

    private var idleFlow: MutableStateFlow<List<CustomerIdlInfo>> = MutableStateFlow(listOf())
    private var customersFlow: MutableStateFlow<List<Obieqti>> = MutableStateFlow(listOf())
    val allData = combine(
        customersFlow,
        idleFlow
    ) { customers, idleInfo ->
        Pair(customers, idleInfo)
    }

    fun getBeerList(): LiveData<List<BeerModelBase>> {
        ApeniApiService.getInstance().getBeerList().sendRequest(
            successWithData = {
                if (it.isNotEmpty()) {
                    ioScope.launch {
                        database.clearBeerTable()
                        database.insertBeers(it)
                    }
                }
            },
            failure = {},
            onConnectionFailure = {}
        )
        return database.getBeerList()
    }

    fun getCustomerData(customerID: Int): LiveData<ObiectWithPrices?> {
        ApeniApiService.getInstance().getCustomerData(customerID).sendRequest(
            successWithData = { customerData ->
                ioScope.launch {
                    database.insertBeerPrices(customerData.prices)
                }
            },
            failure = {},
            onConnectionFailure = {}
        )

        return database.getCustomerWithPricesLiveData(customerID)
    }

    fun getCustomers() {
        ioScope.launch {
            database.getCustomers().collectLatest {
                withContext(Dispatchers.Main) {
                    customersFlow.emit(it)
                }
            }
        }
        ApeniApiService.getInstance().getObieqts().sendRequest(
            successWithData = {
                ioScope.launch {
                    database.clearObiectsTable()
                    database.insertCustomers(it)
                }
            },
            failure = {},
            onConnectionFailure = {}
        )
    }

    fun getCustomersIdleInfo() {
        ApeniApiService.getInstance().getCustomersIdleInfo().sendRequest(
            successWithData = {
                CoroutineScope(Dispatchers.Main).launch {
                    idleFlow.emit(it)
                }
            },
            failure = {},
            onConnectionFailure = {}
        )
    }
}