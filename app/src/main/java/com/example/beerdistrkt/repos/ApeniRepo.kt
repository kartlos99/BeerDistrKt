package com.example.beerdistrkt.repos

import androidx.lifecycle.LiveData
import com.example.beerdistrkt.db.ApeniDataBase
import com.example.beerdistrkt.db.ApeniDatabaseDao
import com.example.beerdistrkt.fragPages.objList.model.Customer
import com.example.beerdistrkt.models.CustomerDataDTO
import com.example.beerdistrkt.models.CustomerIdlInfo
import com.example.beerdistrkt.models.CustomerWithPrices
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

    private val clientDataFlow: MutableStateFlow<CustomerWithPrices?> = MutableStateFlow(null)

    private fun mapCustomerDtoToPm(customerDto: CustomerDataDTO): CustomerWithPrices =
        with(customerDto) {

            val customer = Customer(
                id = id,
                dasaxeleba = dasaxeleba,
                adress = adress,
                tel = tel,
                comment = comment,
                sk = sk,
                sakpiri = sakpiri,
                chek = chek,
                group = group,
            )

            return CustomerWithPrices(
                customer = customer,
                beerPrices = prices,
                bottlePrices = bottlePrices
            )
        }

    fun getCustomerDataFlow(customerID: Int): MutableStateFlow<CustomerWithPrices?> {

        ApeniApiService.getInstance().getCustomerData(customerID).sendRequest(
            successWithData = { customerData ->
                ioScope.launch {
                    database.insertBeerPrices(customerData.prices)
                }
                CoroutineScope(Dispatchers.Main).launch {
                    clientDataFlow.emit(mapCustomerDtoToPm(customerData))
                }
            },
            failure = {
                CoroutineScope(Dispatchers.Main).launch {
                    clientDataFlow.emit(null)
                }
            },
            onConnectionFailure = {}
        )
        return clientDataFlow
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

   /* fun getBaseData() {
        ApeniApiService.getInstance().getBaseData().sendRequest(
            successWithData = {
                val bottleMapper = DefaultBottleDtoMapper(it.beers)
                val bottles = it.bottles.map { dto ->
                    bottleMapper.map(dto)
                }
                ObjectCache.getInstance()
                    .putList(BaseBottleModel::class, ObjectCache.BOTTLE_LIST_ID, bottles)
            },
            failure = {

            },
            onConnectionFailure = {

            }
        )
    }*/
}