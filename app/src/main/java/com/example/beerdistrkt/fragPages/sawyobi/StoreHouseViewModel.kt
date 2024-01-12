package com.example.beerdistrkt.fragPages.sawyobi

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.beerdistrkt.BaseViewModel
import com.example.beerdistrkt.fragPages.realisation.RealisationType
import com.example.beerdistrkt.fragPages.realisation.models.TempRealisationModel
import com.example.beerdistrkt.fragPages.sawyobi.models.BottleIoModel
import com.example.beerdistrkt.fragPages.sawyobi.models.IoModel
import com.example.beerdistrkt.fragPages.sawyobi.models.SimpleBeerRowModel
import com.example.beerdistrkt.fragPages.sawyobi.models.SimpleBottleRowModel
import com.example.beerdistrkt.fragPages.sawyobi.models.StoreHouseResponse
import com.example.beerdistrkt.fragPages.sawyobi.models.StoreInsertRequestModel
import com.example.beerdistrkt.models.BeerModelBase
import com.example.beerdistrkt.models.CanModel
import com.example.beerdistrkt.models.TempBeerItemModel
import com.example.beerdistrkt.models.bottle.BaseBottleModel
import com.example.beerdistrkt.models.bottle.TempBottleItemModel
import com.example.beerdistrkt.network.ApeniApiService
import com.example.beerdistrkt.storage.ObjectCache
import com.example.beerdistrkt.storage.ObjectCache.Companion.BARREL_LIST_ID
import com.example.beerdistrkt.storage.ObjectCache.Companion.BEER_LIST_ID
import com.example.beerdistrkt.storage.ObjectCache.Companion.BOTTLE_LIST_ID
import com.example.beerdistrkt.utils.ApiResponseState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import java.util.UUID

class StoreHouseViewModel : BaseViewModel() {

    var selectedDate: Calendar = Calendar.getInstance()
    var editMode = false
    var editingItemID = -1

    var isChecked = false
        set(value) {
            field = value
            getStoreBalance()
        }

    private val _setDayLiveData = MutableLiveData<String>()
    val setDayLiveData: LiveData<String>
        get() = _setDayLiveData

    private val _bottlesLiveData = MutableLiveData<List<SimpleBottleRowModel>>()
    val bottlesLiveData: LiveData<List<SimpleBottleRowModel>>
        get() = _bottlesLiveData

    private val _fullBarrelsListLiveData = MutableLiveData<List<SimpleBeerRowModel>>()
    val fullBarrelsListLiveData: LiveData<List<SimpleBeerRowModel>>
        get() = _fullBarrelsListLiveData

    private val _emptyBarrelsListLiveData = MutableLiveData<List<SimpleBeerRowModel>>()
    val emptyBarrelsListLiveData: LiveData<List<SimpleBeerRowModel>>
        get() = _emptyBarrelsListLiveData

    private val _doneLiveData = MutableLiveData<ApiResponseState<String>>()
    val doneLiveData: LiveData<ApiResponseState<String>>
        get() = _doneLiveData

    private val _editDataReceiveLiveData = MutableLiveData<ApiResponseState<IoModel>>()
    val editDataReceiveLiveData: LiveData<ApiResponseState<IoModel>>
        get() = _editDataReceiveLiveData

    val beerList = ObjectCache.getInstance().getList(BeerModelBase::class, BEER_LIST_ID)
        ?: mutableListOf()
    val bottleList = ObjectCache.getInstance().getList(BaseBottleModel::class, BOTTLE_LIST_ID)
        ?: mutableListOf()
    val cansList = ObjectCache.getInstance().getList(CanModel::class, BARREL_LIST_ID)
        ?: listOf()

    private val receivedItemsList = mutableListOf<TempBeerItemModel>()
    private val receivedBottleItemsList = mutableListOf<TempBottleItemModel>()
    val tempInputItemsLiveData = MutableLiveData<TempRealisationModel>()
    private val barrelOutItems = mutableListOf<StoreInsertRequestModel.BarrelOutItem>()
    val emptyBarrelsEditingLiveData = MutableLiveData<List<StoreInsertRequestModel.BarrelOutItem>>()

    val realisationStateFlow = MutableStateFlow(RealisationType.BARREL)

    val realisationType: RealisationType
        get() {
            return realisationStateFlow.value
        }
    val eventsFlow = MutableSharedFlow<Event>()

    init {
        getStoreBalance()
    }

    fun setCurrentTime() {
        selectedDate.time = Date()
        _setDayLiveData.value = dateTimeFormat.format(selectedDate.time)
    }

    private fun getStoreBalance() {
        sendRequest(
            ApeniApiService.getInstance().getStoreHouseBalance(
                dateFormatDash.format(selectedDate.time),
                if (isChecked) 1 else 0
            ),
            successWithData = {
                Log.d("store", it.empty.toString())
                formFullList(it.full)
                proceedBottleBalance(it.bottles)
                if (it.empty != null)
                    formEmptyList(it.empty, it.full)
            },
            finally = {
                Log.d("storeFinaly", it.toString())
            }
        )
    }

    private fun formEmptyList(
        empty: List<StoreHouseResponse.EmptyBarrelModel>,
        fList: List<StoreHouseResponse.FullBarrelModel>
    ) {
        val result = mutableListOf<SimpleBeerRowModel>()
        val valueOfDiff = mutableMapOf<Int, Int>()
        empty.forEach { ebm ->
            valueOfDiff[ebm.barrelID] = ebm.inputEmptyToStore - ebm.outputEmptyFromStoreCount
        }
        result.add(SimpleBeerRowModel("საწყობში:", valueOfDiff))

        val barrelsAtClient = mutableMapOf<Int, Int>()

        val fGr = fList.groupBy { it.barrelID }
        fGr.keys.forEach {
            val sumOfSale = fGr[it]?.sumOf { fItem -> fItem.saleCount } ?: 0
            val sumOfReceived = empty.find { e -> e.barrelID == it }?.inputEmptyToStore ?: 0
            barrelsAtClient[it] = sumOfSale - sumOfReceived
        }
        result.add(SimpleBeerRowModel("დასაბრუნებელი:", barrelsAtClient))

        _emptyBarrelsListLiveData.value = result
    }

    private fun formFullList(fList: List<StoreHouseResponse.FullBarrelModel>) {
        val result = mutableListOf<SimpleBeerRowModel>()
        val a = fList.groupBy { it.beerID }
        a.values.forEach {
            val valueOfDiff = mutableMapOf<Int, Int>()
            it.forEach { fbm ->
                valueOfDiff[fbm.barrelID] = fbm.inputToStore - fbm.saleCount
            }
            val title = beerList.firstOrNull { b ->
                b.id == it[0].beerID
            }?.dasaxeleba ?: "_"
            result.add(SimpleBeerRowModel(title, valueOfDiff))
        }
        _fullBarrelsListLiveData.value = result
    }

    private fun proceedBottleBalance(bottles: List<StoreHouseResponse.BottleModel>) {
        _bottlesLiveData.value = bottles.mapNotNull { bottleModel ->
            bottleList.firstOrNull {
                it.id == bottleModel.bottleID
            }?.let { bottle ->
                SimpleBottleRowModel(
                    bottle.name,
                    bottleModel.inputToStore - bottleModel.saleCount,
                    bottle.imageLink
                )
            }
        }
    }

    fun onDateSelected(year: Int, month: Int, day: Int) {
        selectedDate.set(year, month, day)
        _setDayLiveData.value = dateTimeFormat.format(selectedDate.time)
        getStoreBalance()
    }

    fun onSaleTimeSelected(hour: Int, minute: Int) {
        selectedDate.set(Calendar.HOUR_OF_DAY, hour)
        selectedDate.set(Calendar.MINUTE, minute)
        _setDayLiveData.value = dateTimeFormat.format(selectedDate.time)
    }

    fun onDoneClick(comment: String?, barrelsEntered: List<Int>, groupID: String) {
        barrelOutItems.clear()
        barrelsEntered.forEachIndexed { index, count ->
            // es gasasworebelia, kasris tipi indexidan ar unda avigo
            if (count > 0)
                addBarrelToList(index + 1, count)
        }

        val storeInsertRequestModel = StoreInsertRequestModel(
            comment,
            groupID.ifEmpty { UUID.randomUUID().toString() },
            if (isChecked) 1 else 0,
            operationTime = dateTimeFormat.format(selectedDate.time),
            inputBeer = receivedItemsList.map {
                StoreInsertRequestModel.ReceiveItem(
                    0, it.beer.id, it.canType.id, it.count
                )
            },
            inputBottle = receivedBottleItemsList.map {
                StoreInsertRequestModel.ReceiveBottleItem(0, it.bottle.id, it.count)
            },
            outputBarrels = barrelOutItems
        )

        addStoreHouseOperation(storeInsertRequestModel)
    }

    private fun addStoreHouseOperation(requestModel: StoreInsertRequestModel) {
        _doneLiveData.value = ApiResponseState.Loading(true)
        sendRequest(
            ApeniApiService.getInstance().addStoreHouseOperation(requestModel),
            successWithData = {
                Log.d("insToStore", it)
                _doneLiveData.value = ApiResponseState.Success(it)
                clearEnteredData()
                getStoreBalance()
            },
            finally = {
                _doneLiveData.value = ApiResponseState.Loading(false)
            }
        )
    }

    fun sleepDoneLiveData() {
        _doneLiveData.value = ApiResponseState.Sleep
    }

    fun addBeerReceiveItemToList(beerItem: TempBeerItemModel) {

        val item = beerItem.withID()

        if (editingItemID >= 0)
            receivedItemsList.removeAll { it.orderItemID == editingItemID }

        if (receivedItemsList.any {
                it.beer.id == item.beer.id && it.canType.id == item.canType.id
            }
        ) {
            viewModelScope.launch {
                eventsFlow.emit(Event.DuplicateBarrelItem)
            }
        } else {
            receivedItemsList.add(item)
            updateTempInputItemList()
        }
        editingItemID = -1
    }

    fun addBottleReceiveItemToList(bottleItem: TempBottleItemModel) {
        val item = bottleItem.withID()
        if (editingItemID >= 0)
            receivedBottleItemsList.removeAll { it.orderItemID == editingItemID }

        if (receivedBottleItemsList.any {
                it.bottle.id == item.bottle.id
            }
        ) {
            viewModelScope.launch {
                eventsFlow.emit(Event.DuplicateBottleItem)
            }
        } else {
            receivedBottleItemsList.add(item)
            updateTempInputItemList()
        }
        editingItemID = -1
    }

    private fun addBarrelToList(barrelType: Int, count: Int) {
        barrelOutItems.add(
            StoreInsertRequestModel.BarrelOutItem(
                0,
                barrelType,
                count
            )
        )
    }

    fun removeReceiveItemFromList(item: TempBeerItemModel) {
        receivedItemsList.removeAll { it.beer == item.beer && it.canType == item.canType }
        updateTempInputItemList()
    }

    fun removeReceiveItemFromList(item: TempBottleItemModel) {
        receivedBottleItemsList.removeAll { it.bottle.id == item.bottle.id }
        updateTempInputItemList()
    }

    private fun updateTempInputItemList() {
        tempInputItemsLiveData.value = TempRealisationModel(
            receivedItemsList
                .sortedBy { it.canType.name }
                .sortedBy { it.beer.sortValue },
            receivedBottleItemsList
                .sortedBy { it.bottle.sortValue }
        )
    }

    fun clearEnteredData() {
        barrelOutItems.clear()
        receivedItemsList.clear()
        receivedBottleItemsList.clear()
        updateTempInputItemList()
    }

    fun getEditingData(groupID: String) {
        editMode = true
        _editDataReceiveLiveData.value = ApiResponseState.Loading(true)
        sendRequest(
            ApeniApiService.getInstance().getStoreHouseIoList(groupID),
            successWithData = {
                processIoData(it.barrelsIo)
                processBottleIoData(it.bottlesIo)
                if (it.barrelsIo.isNotEmpty()) {
                    _editDataReceiveLiveData.value = ApiResponseState.Success(it.barrelsIo[0])
                    val date = dateTimeFormat.parse(it.barrelsIo[0].ioDate)
                    selectedDate.time = date ?: Date()
                    _setDayLiveData.value = dateTimeFormat.format(selectedDate.time)
                }
            },
            finally = {
                _editDataReceiveLiveData.value = ApiResponseState.Loading(false)
            }
        )
    }

    private fun processBottleIoData(data: List<BottleIoModel>) {
        receivedBottleItemsList.clear()
        data.forEach {
            receivedBottleItemsList.add(it.toTempBottleItemModel(
                bottleList,
                { tempBottleModel ->
                    receivedBottleItemsList.removeAll { it.id == tempBottleModel.id }
                    updateTempInputItemList()
                },
                {}
            ))
        }
        updateTempInputItemList()
    }

    private fun processIoData(data: List<IoModel>) {
        data
            .filter { it.beerID == 0 }
            .forEach {
                addBarrelToList(it.barrelID, it.count)
            }
        emptyBarrelsEditingLiveData.value = barrelOutItems

        data
            .filter { it.beerID > 0 }
            .forEach { ioModel ->
                addBeerReceiveItemToList(
                    ioModel.toTempBeerItemModel(
                        cansList,
                        beerList,
                        onRemove = { tbm ->
                            Log.d("itmDel", tbm.toString())
                            receivedItemsList.removeAll { it.ID == tbm.ID }
                            updateTempInputItemList()
                        },
                        onEdit = {})
                )
            }
    }

    fun switchToBarrel() {
        realisationStateFlow.value = RealisationType.BARREL
    }

    fun switchToBottle() {
        realisationStateFlow.value = RealisationType.BOTTLE
    }
}


private fun TempBeerItemModel.withID(): TempBeerItemModel {
    val id = (System.currentTimeMillis() % 2000000000).toInt()
    return if (this.ID == 0)
        this.copy(ID = id, orderItemID = id)
    else
        this
}

private fun TempBottleItemModel.withID(): TempBottleItemModel {
    val id = (System.currentTimeMillis() % 2000000000).toInt()
    return if (this.id == 0)
        this.copy(id = id, orderItemID = id)
    else
        this
}