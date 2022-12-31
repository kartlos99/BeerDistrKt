package com.example.beerdistrkt.repos

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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
import kotlinx.coroutines.launch

class ApeniRepo {

    private val ioScope = CoroutineScope(Dispatchers.IO)
    private val database: ApeniDatabaseDao = ApeniDataBase.getInstance().apeniDataBaseDao

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

    var customersIdleInfoLiveData: MutableLiveData<List<CustomerIdlInfo>> = MutableLiveData<List<CustomerIdlInfo>>()
    var customers = listOf<Obieqti>()

    fun getCustomers(): LiveData<List<Obieqti>> {
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
        return database.getAllObieqts().also {
            customers = it.value ?: listOf()
        }
    }

    fun getCustomersIdleInfo(): LiveData<List<CustomerIdlInfo>> {
        ApeniApiService.getInstance().getCustomersIdleInfo().sendRequest(
            successWithData = {
                customersIdleInfoLiveData.postValue(it)
            },
            failure = {},
            onConnectionFailure = {}
        )
        return customersIdleInfoLiveData
    }
}