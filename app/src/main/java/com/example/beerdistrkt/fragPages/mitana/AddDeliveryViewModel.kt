package com.example.beerdistrkt.fragPages.mitana

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.beerdistrkt.BaseViewModel
import com.example.beerdistrkt.fragPages.mitana.AddDeliveryFragment.Companion.M_OUT
import com.example.beerdistrkt.fragPages.mitana.models.BarrelRowModel
import com.example.beerdistrkt.fragPages.mitana.models.MoneyRowModel
import com.example.beerdistrkt.fragPages.mitana.models.RecordRequestModel
import com.example.beerdistrkt.fragPages.mitana.models.SaleRowModel
import com.example.beerdistrkt.fragPages.sales.models.PaymentType
import com.example.beerdistrkt.fragPages.sales.models.SaleRequestModel
import com.example.beerdistrkt.models.BeerModelBase
import com.example.beerdistrkt.models.CanModel
import com.example.beerdistrkt.models.CustomerWithPrices
import com.example.beerdistrkt.models.ObjToBeerPrice
import com.example.beerdistrkt.models.TempBeerItemModel
import com.example.beerdistrkt.models.bottle.BaseBottleModel
import com.example.beerdistrkt.models.bottle.ClientBottlePrice
import com.example.beerdistrkt.network.ApeniApiService
import com.example.beerdistrkt.repos.ApeniRepo
import com.example.beerdistrkt.round
import com.example.beerdistrkt.storage.ObjectCache
import com.example.beerdistrkt.storage.ObjectCache.Companion.BARREL_LIST_ID
import com.example.beerdistrkt.storage.ObjectCache.Companion.BEER_LIST_ID
import com.example.beerdistrkt.storage.ObjectCache.Companion.BOTTLE_LIST_ID
import com.example.beerdistrkt.utils.ApiResponseState
import com.example.beerdistrkt.utils.Session
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

class AddDeliveryViewModel(
    private val clientID: Int,
    private val orderID: Int = 0
) : BaseViewModel() {

    val clientLiveData = MutableLiveData<CustomerWithPrices>()

    val beerListLiveData = MutableLiveData<List<BeerModelBase>>()
    val beerList =
        ObjectCache.getInstance().getList(BeerModelBase::class, BEER_LIST_ID)?.toMutableList()
            ?: mutableListOf()

    val cansList = ObjectCache.getInstance().getList(CanModel::class, BARREL_LIST_ID)
        ?: listOf()

    val bottleListLiveData = MutableLiveData<List<BaseBottleModel>>()
    val bottleList = ObjectCache.getInstance().getList(BaseBottleModel::class, BOTTLE_LIST_ID)
        ?: listOf()

    var saleDateCalendar: Calendar = Calendar.getInstance()
    private val _saleDayLiveData = MutableLiveData<String>()
    val saleDayLiveData: LiveData<String>
        get() = _saleDayLiveData

    private val _addSaleLiveData = MutableLiveData<ApiResponseState<String>>()
    val addSaleLiveData: LiveData<ApiResponseState<String>>
        get() = _addSaleLiveData

    val saleItemsList = mutableListOf<TempBeerItemModel>()
    val saleItemsLiveData = MutableLiveData<List<TempBeerItemModel>>()

    val barrelOutItems = mutableListOf<SaleRequestModel.BarrelOutItem>()
    var moneyOut: MutableList<SaleRequestModel.MoneyOutItem> = mutableListOf()
    var isGift = false
    var isReplace = false

    var operation: String? = null
    var recordID = 0

    val saleItemDuplicateLiveData = MutableLiveData(false)

    private val repository = ApeniRepo()

    val infoSharedFlow = MutableSharedFlow<String>()

    val realisationState = MutableStateFlow(RealisationType.BARREL)

    init {
        _saleDayLiveData.value = dateTimeFormat.format(saleDateCalendar.time)
        viewModelScope.launch {
            repository.getCustomerDataFlow(clientID)
                .onEach {
                    it?.let { customer ->
                        clientLiveData.value = customer
                        attachPrices(customer.beerPrices)
                        attachBottlePrices(customer.bottlePrices)
                    } ?: infoSharedFlow.emit("ობიექტი არ იძებნება")
                }
                .collect()
        }
    }

    /**
     * if beer price not defined for customer, takes 0 as price
     */
    private fun attachPrices(pricesForClient: List<ObjToBeerPrice>) {
        beerListLiveData.value = beerList
            .map { beerModel ->
                val price = findBeerPrice(beerModel, pricesForClient)
                beerModel.copy(fasi = price.round())
            }
    }

    /**
     * if bottle price not defined for customer, takes default price
     */
    private fun attachBottlePrices(pricesForClient: List<ClientBottlePrice>) {
        bottleListLiveData.value = bottleList
            .map { bottleItem ->
                pricesForClient.firstOrNull {
                    it.bottleID == bottleItem.id
                }?.let {
                    bottleItem.copy(price = it.price)
                }
                    ?: bottleItem
            }
    }

    private fun findBeerPrice(
        beerModel: BeerModelBase,
        pricesForClient: List<ObjToBeerPrice>
    ): Double = pricesForClient.find { objToBeerPrice ->
        objToBeerPrice.beerID == beerModel.id
    }?.fasi?.toDouble() ?: 0.0

    fun onDoneClick(deliveryDataComment: String) {
        if (callIsBlocked) return
        callIsBlocked = true

        val hasZeroPrice = saleItemsList.any {
            (it.beer.fasi ?: .0) < 0.01
        }
        if (hasZeroPrice && !isGift) {
            viewModelScope.launch {
                infoSharedFlow.emit("რომელიღაც ლუდს ფასი არ ააქვს გაწერილი!")
            }
            return
        }

        val saleRequestModel = SaleRequestModel(
            clientID,
            Session.get().getUserID(),
            deliveryDataComment,
            Session.get().getUserID(),
            isReplace = if (isReplace) "1" else "0",
            sales = saleItemsList.map {
                it.toRequestSaleItem(
                    dateTimeFormat.format(saleDateCalendar.time),
                    orderID,
                    isGift || isReplace
                )
            },
            barrels = getEmptyBarrelsList(),
            money = moneyOut

        )

        if (operation == null)
            addDelivery(saleRequestModel)
        else
            updateDelivery(saleRequestModel)
    }

    private fun addDelivery(saleRequestModel: SaleRequestModel) {

        Log.d(TAG, saleRequestModel.toString())

        _addSaleLiveData.value = ApiResponseState.Loading(true)
        sendRequest(
            ApeniApiService.getInstance().addSales(saleRequestModel),
            successWithData = {
                Log.d(TAG, it)
                _addSaleLiveData.value = ApiResponseState.Success(it)
            },
            finally = {
                moneyOut.clear()
                barrelOutItems.clear()
                _addSaleLiveData.value = ApiResponseState.Loading(false)
            }
        )
    }

    private fun updateDelivery(saleRequestModel: SaleRequestModel) {
        _addSaleLiveData.value = ApiResponseState.Loading(true)
        sendRequest(
            ApeniApiService.getInstance().updateSale(saleRequestModel),
            successWithData = {
                _addSaleLiveData.value = ApiResponseState.Success(it)
            },
            finally = {
                _addSaleLiveData.value = ApiResponseState.Loading(false)
            }
        )
    }

    fun onSaleDateSelected(year: Int, month: Int, day: Int) {
        saleDateCalendar.set(year, month, day)
        _saleDayLiveData.value = dateTimeFormat.format(saleDateCalendar.time)
    }

    fun onSaleTimeSelected(hour: Int, minute: Int) {
        saleDateCalendar.set(Calendar.HOUR_OF_DAY, hour)
        saleDateCalendar.set(Calendar.MINUTE, minute)
        _saleDayLiveData.value = dateTimeFormat.format(saleDateCalendar.time)
    }

    fun removeSaleItemFromList(saleItem: TempBeerItemModel) {
        saleItemsList.remove(saleItem)
        saleItemsLiveData.value = saleItemsList
    }

    fun addSaleItemToList(saleItem: TempBeerItemModel) {
        if (saleItemsList.any {
                it.beer.id == saleItem.beer.id && it.canType.id == saleItem.canType.id
            })
            saleItemDuplicateLiveData.value = true
        else {
            saleItemsList.add(saleItem)
            saleItemsLiveData.value = saleItemsList
        }
    }

    fun addBarrelToList(barrelType: Int, count: Int) {
        barrelOutItems.add(
            SaleRequestModel.BarrelOutItem(
                recordID,
                dateTimeFormat.format(saleDateCalendar.time),
                barrelType,
                count
            )
        )
    }

    private fun getEmptyBarrelsList(): List<SaleRequestModel.BarrelOutItem>? {
        return if (barrelOutItems.size > 0)
            barrelOutItems
        else
            null
    }

    fun setMoney(cashValue: String, transferValue: String) {
        moneyOut.clear()
        if (operation == null || operation == M_OUT) {
            if (cashValue.isNotEmpty() && cashValue.toDouble() > 0)
                moneyOut.add(
                    SaleRequestModel.MoneyOutItem(
                        recordID,
                        dateTimeFormat.format(saleDateCalendar.time),
                        cashValue.toDouble(),
                        PaymentType.Cash
                    )
                )
            if (transferValue.isNotEmpty() && transferValue.toDouble() > 0)
                moneyOut.add(
                    SaleRequestModel.MoneyOutItem(
                        recordID,
                        dateTimeFormat.format(saleDateCalendar.time),
                        transferValue.toDouble(),
                        PaymentType.Transfer
                    )
                )
        }
    }

    val saleItemEditLiveData = MutableLiveData<SaleRowModel?>()
    val kOutEditLiveData = MutableLiveData<BarrelRowModel?>()
    val mOutEditLiveData = MutableLiveData<MoneyRowModel?>()

    fun getRecordData() {
        sendRequest(
            ApeniApiService.getInstance().getRecord(
                RecordRequestModel(operation ?: "", recordID)
            ),
            successWithData = {
                if (it.mitana != null) {
                    saleItemEditLiveData.value = it.mitana

                    isGift = it.mitana.unitPrice == 0.0
                    val date = dateTimeFormat.parse(it.mitana.saleDate)
                    saleDateCalendar.time = date ?: Date()
                    _saleDayLiveData.value = dateTimeFormat.format(saleDateCalendar.time)
                }
                if (it.kout != null) {
                    kOutEditLiveData.value = it.kout

                    val date = dateTimeFormat.parse(it.kout.outputDate)
                    saleDateCalendar.time = date ?: Date()
                    _saleDayLiveData.value = dateTimeFormat.format(saleDateCalendar.time)
                }
                if (it.mout != null) {
                    mOutEditLiveData.value = it.mout

                    val date = dateTimeFormat.parse(it.mout.takeMoneyDate)
                    saleDateCalendar.time = date ?: Date()
                    _saleDayLiveData.value = dateTimeFormat.format(saleDateCalendar.time)
                }

                Log.d("respRECORD", it.toString())
            },
            finally = {
                Log.d("respRECORD_Finaly", it.toString())
            }
        )
    }

    fun getPrice(): Double {
        var price = 0.0
        saleItemsList.forEach {
            price += it.canType.volume * it.count * (it.beer.fasi ?: 0.0)
        }
        return price.round()
    }

    fun switchToBarrel() {
        realisationState.value = RealisationType.BARREL
    }

    fun switchToBottle() {
        realisationState.value = RealisationType.BOTTLE
    }

    companion object {
        const val TAG = "TAG AddDelivery-----"
    }
}

enum class RealisationType {
    BARREL,
    BOTTLE
}