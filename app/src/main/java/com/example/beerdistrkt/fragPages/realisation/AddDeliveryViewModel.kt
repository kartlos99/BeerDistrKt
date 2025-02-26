package com.example.beerdistrkt.fragPages.realisation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.beerdistrkt.BaseViewModel
import com.example.beerdistrkt.common.model.Barrel
import com.example.beerdistrkt.fragPages.beer.domain.model.Beer
import com.example.beerdistrkt.fragPages.beer.domain.usecase.GetBeerUseCase
import com.example.beerdistrkt.fragPages.bottle.domain.usecase.GetBottlesUseCase
import com.example.beerdistrkt.fragPages.customer.domain.model.ClientBeerPrice
import com.example.beerdistrkt.fragPages.realisation.RealisationType.BARREL
import com.example.beerdistrkt.fragPages.realisation.RealisationType.BOTTLE
import com.example.beerdistrkt.fragPages.realisation.RealisationType.NONE
import com.example.beerdistrkt.fragPages.realisation.models.RecordRequestModel
import com.example.beerdistrkt.fragPages.realisation.models.TempRealisationModel
import com.example.beerdistrkt.fragPages.realisationtotal.models.PaymentType
import com.example.beerdistrkt.fragPages.realisationtotal.models.SaleRequestModel
import com.example.beerdistrkt.models.TempBeerItemModel
import com.example.beerdistrkt.fragPages.bottle.domain.model.Bottle
import com.example.beerdistrkt.fragPages.customer.domain.model.ClientBottlePrice
import com.example.beerdistrkt.fragPages.customer.domain.model.Customer
import com.example.beerdistrkt.fragPages.customer.domain.usecase.GetCustomerUseCase
import com.example.beerdistrkt.fragPages.homePage.domain.usecase.GetBarrelsUseCase
import com.example.beerdistrkt.fragPages.bottle.presentation.model.TempBottleItemModel
import com.example.beerdistrkt.network.ApeniApiService
import com.example.beerdistrkt.round
import com.example.beerdistrkt.utils.ApiResponseState
import com.example.beerdistrkt.utils.M_OUT
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

@HiltViewModel(assistedFactory = AddDeliveryViewModel.Factory::class)
class AddDeliveryViewModel @AssistedInject constructor(
    private val getBeerUseCase: GetBeerUseCase,
    private val getBottlesUseCase: GetBottlesUseCase,
    private val getCustomerUseCase: GetCustomerUseCase,
    private val getBarrelsUseCase: GetBarrelsUseCase,
    @Assisted(CLIENT_ID_KEY) private val clientID: Int,
    @Assisted(ORDER_ID_KEY) private val orderID: Int,
    @Assisted(RECORD_ID_KEY) private val recordID: Int,
    @Assisted(OPERATION_KEY) private val operation: String?,
) : BaseViewModel() {

    val clientLiveData = MutableLiveData<Customer>()

    val beerListLiveData = MutableLiveData<List<Beer>>()

    var beerList: List<Beer> = listOf()
    var bottleList: List<Bottle> = listOf()
    var barrels: List<Barrel> = listOf()

    val bottleListLiveData = MutableLiveData<List<Bottle>>()

    var saleDateCalendar: Calendar = Calendar.getInstance()
    private val _saleDayLiveData = MutableLiveData<String>()
    val saleDayLiveData: LiveData<String>
        get() = _saleDayLiveData

    private val _addSaleLiveData = MutableLiveData<ApiResponseState<String>>()
    val addSaleLiveData: LiveData<ApiResponseState<String>>
        get() = _addSaleLiveData

    private val saleItemsList = mutableListOf<TempBeerItemModel>()
    private val bottleSaleItemsList = mutableListOf<TempBottleItemModel>()
    val tempRealisationLiveData = MutableLiveData<TempRealisationModel>()

    val barrelOutItems = mutableListOf<SaleRequestModel.BarrelOutItem>()
    var moneyOut: MutableList<SaleRequestModel.MoneyOutItem> = mutableListOf()
    var isGift = false
    var isReplace = false

    val eventsFlow = MutableSharedFlow<Event>()

    private val _editOperationState = MutableStateFlow<Any>(0)
    val editOperationState = _editOperationState.asStateFlow()

    val realisationStateFlow = MutableStateFlow(NONE)

    val realisationType: RealisationType
        get() {
            return realisationStateFlow.value
        }

    init {
        _saleDayLiveData.value = dateTimeFormat.format(saleDateCalendar.time)
        viewModelScope.launch {
            initData()
            getCustomer()
        }
    }

    private suspend fun getCustomer() {
        getCustomerUseCase.invoke(clientID)?.let { customer ->
            clientLiveData.value = customer
            attachPrices(customer.beerPrices)
            attachBottlePrices(customer.bottlePrices)
            if (operation != null)
                getRecordData()
            else
                realisationStateFlow.value = BARREL
        } ?: eventsFlow.emit(Event.CustomerNotFount)
    }


    private suspend fun initData() {
        beerList = getBeerUseCase()
        bottleList = getBottlesUseCase()
        barrels = getBarrelsUseCase()
    }

    /**
     * if beer price not defined for customer, takes 0 as price
     */
    private fun attachPrices(pricesForClient: List<ClientBeerPrice>) {
        beerListLiveData.value = beerList
            .map { beerModel ->
                val price = findBeerPrice(beerModel, pricesForClient)
                beerModel.copy(price = price.round())
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
        beerModel: Beer,
        pricesForClient: List<ClientBeerPrice>
    ): Double = pricesForClient.find { clientPrice ->
        clientPrice.beerID == beerModel.id
    }?.price ?: 0.0

    fun onDoneClick(deliveryDataComment: String) {
        if (callIsBlocked) return
        callIsBlocked = true

        val hasZeroPrice = saleItemsList.any {
            (it.beer.price ?: .0) < 0.01
        } || bottleSaleItemsList.any {
            it.bottle.price < 0.01
        }
        if (hasZeroPrice && !isGift) {
            viewModelScope.launch {
                eventsFlow.emit(Event.NoPriceException)
            }
            return
        }

        val saleRequestModel = SaleRequestModel(
            clientID,
            session.getUserID(),
            deliveryDataComment,
            session.getUserID(),
            isReplace = if (isReplace) "1" else "0",
            operationDate = dateTimeFormat.format(saleDateCalendar.time),
            orderID = orderID,
            sales = saleItemsList.map {
                it.toRequestSaleItem(
                    dateTimeFormat.format(saleDateCalendar.time),
                    orderID,
                    isGift || isReplace
                )
            },
            bottleSales = bottleSaleItemsList.map {
                it.toRequestSaleItem(isGift || isReplace)
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

    private fun updateTempSaleList() {
        tempRealisationLiveData.value = TempRealisationModel(
            saleItemsList,
            bottleSaleItemsList
        )
    }

    fun removeSaleItemFromList(saleItem: TempBeerItemModel) {
        saleItemsList.remove(saleItem)
        updateTempSaleList()
    }

    fun removeBottleSaleItemFromList(saleItem: TempBottleItemModel) {
        bottleSaleItemsList.remove(saleItem)
        updateTempSaleList()
    }

    fun addSaleItemToList(saleItem: TempBeerItemModel) {
        if (isAlreadyInList(saleItem))
            viewModelScope.launch {
                eventsFlow.emit(Event.DuplicateBarrelItem)
            }
        else {
            saleItemsList.add(saleItem)
            updateTempSaleList()
        }
    }

    fun addBottleSaleItem(bottleItemModel: TempBottleItemModel) {
        if (isAlreadyInList(bottleItemModel)) {
            viewModelScope.launch {
                eventsFlow.emit(Event.DuplicateBottleItem)
            }
        } else {
            bottleSaleItemsList.add(bottleItemModel)
            updateTempSaleList()
        }
    }

    private fun isAlreadyInList(saleItem: TempBeerItemModel): Boolean = saleItemsList.any {
        it.beer.id == saleItem.beer.id && it.canType.id == saleItem.canType.id
    }

    private fun isAlreadyInList(bottleItemModel: TempBottleItemModel): Boolean =
        bottleSaleItemsList.any {
            it.bottle.id == bottleItemModel.bottle.id
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

    private fun getRecordData() {
        sendRequest(
            ApeniApiService.getInstance().getRecord(
                RecordRequestModel(operation.orEmpty(), recordID)
            ),
            successWithData = {
                var date: Date? = null
                viewModelScope.launch {
                    when {
                        it.mitana != null -> {
                            _editOperationState.emit(it.mitana)
                            isGift = it.mitana.unitPrice == 0.0
                            date = dateTimeFormat.parse(it.mitana.saleDate)
                        }

                        it.mitanaBottle != null -> {
                            _editOperationState.emit(it.mitanaBottle)
                            isGift = it.mitanaBottle.price == 0.0
                            date = dateTimeFormat.parse(it.mitanaBottle.saleDate)
                        }

                        it.kout != null -> {
                            _editOperationState.emit(it.kout)
                            date = dateTimeFormat.parse(it.kout.outputDate)
                        }

                        it.mout != null -> {
                            _editOperationState.emit(it.mout)
                            date = dateTimeFormat.parse(it.mout.takeMoneyDate)
                        }
                    }
                }
                saleDateCalendar.time = date ?: Date()
                _saleDayLiveData.value = dateTimeFormat.format(saleDateCalendar.time)

                Log.d("respRECORD", it.toString())
            },
            finally = {
                Log.d("respRECORD_Finaly", it.toString())
            }
        )
    }

    fun getPrice(): Double =
        bottleSaleItemsList.sumOf {
            it.bottle.price * it.count
        }.plus(
            saleItemsList.sumOf {
                it.canType.volume * it.count * (it.beer.price ?: 0.0)
            }
        ).round()

    fun switchToBarrel() {
        realisationStateFlow.value = BARREL
    }

    fun switchToBottle() {
        realisationStateFlow.value = BOTTLE
    }

    fun hasAnySaleItem(): Boolean = saleItemsList.isNotEmpty() || bottleSaleItemsList.isNotEmpty()

    fun clearSaleItems() {
        saleItemsList.clear()
        bottleSaleItemsList.clear()
    }


    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted(CLIENT_ID_KEY) clientID: Int,
            @Assisted(ORDER_ID_KEY) orderID: Int,
            @Assisted(RECORD_ID_KEY) recordID: Int,
            @Assisted(OPERATION_KEY) operation: String?,
        ): AddDeliveryViewModel
    }

    companion object {
        private const val CLIENT_ID_KEY = "clientID"
        private const val ORDER_ID_KEY = "orderID"
        private const val RECORD_ID_KEY = "RECORD_ID"
        private const val OPERATION_KEY = "OPERATION"
        const val TAG = "TAG AddDelivery-----"
    }
}
