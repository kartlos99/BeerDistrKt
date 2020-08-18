package com.example.beerdistrkt.fragPages.sawyobi

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.beerdistrkt.BaseViewModel
import com.example.beerdistrkt.fragPages.sawyobi.models.IoModel
import com.example.beerdistrkt.fragPages.sawyobi.models.SimpleBeerRowModel
import com.example.beerdistrkt.fragPages.sawyobi.models.StoreHouseResponse
import com.example.beerdistrkt.fragPages.sawyobi.models.StoreInsertRequestModel
import com.example.beerdistrkt.models.BeerModel
import com.example.beerdistrkt.models.CanModel
import com.example.beerdistrkt.models.TempBeerItemModel
import com.example.beerdistrkt.network.ApeniApiService
import com.example.beerdistrkt.storage.ObjectCache
import com.example.beerdistrkt.utils.ApiResponseState
import com.example.beerdistrkt.utils.Session
import java.util.*

class StoreHouseViewModel : BaseViewModel() {

    var selectedDate = Calendar.getInstance()
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

    private val _fullBarrelsListLiveData = MutableLiveData<List<SimpleBeerRowModel>>()
    val fullBarrelsListLiveData: LiveData<List<SimpleBeerRowModel>>
        get() = _fullBarrelsListLiveData

    private val _emptyBarrelsListLiveData = MutableLiveData<List<SimpleBeerRowModel>>()
    val emptyBarrelsListLiveData: LiveData<List<SimpleBeerRowModel>>
        get() = _emptyBarrelsListLiveData

    private val _doneLiveData = MutableLiveData<ApiResponseState<String>>()
    val doneLiveData: LiveData<ApiResponseState<String>>
        get() = _doneLiveData

    private val _editDataReceiveLiveData = MutableLiveData<ApiResponseState<List<IoModel>>>()
    val editDataReceiveLiveData: LiveData<ApiResponseState<List<IoModel>>>
        get() = _editDataReceiveLiveData

    val beerList = ObjectCache.getInstance().getList(BeerModel::class, "beerList")
        ?: mutableListOf()
    val cansList = ObjectCache.getInstance().getList(CanModel::class, "canList")
        ?: listOf()

    val receivedItemsList = mutableListOf<TempBeerItemModel>()
    val barrelOutItems = mutableListOf<StoreInsertRequestModel.BarrelOutItem>()

    val receivedItemsLiveData = MutableLiveData<List<TempBeerItemModel>>()
    val receivedItemDuplicateLiveData = MutableLiveData<Boolean>(false)

    init {
        getStoreBalance()
        _setDayLiveData.value = dateFormatDash.format(selectedDate.time)
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
            val sumOfSale = fGr[it]?.sumBy { fItem -> fItem.saleCount } ?: 0
            val sumOfReceived = empty.find { e -> e.barrelID == it }?.inputEmptyToStore ?: 0
            barrelsAtClient[it] = sumOfSale - sumOfReceived
        }
        result.add(SimpleBeerRowModel("დასაბრუნებელი:", barrelsAtClient))

        _emptyBarrelsListLiveData.value = result
    }

    fun formFullList(fList: List<StoreHouseResponse.FullBarrelModel>) {
        val result = mutableListOf<SimpleBeerRowModel>()
        val a = fList.groupBy { it.beerID }
        a.values.forEach {
            val valueOfDiff = mutableMapOf<Int, Int>()
            it.forEach { fbm ->
                valueOfDiff[fbm.barrelID] = fbm.inputToStore - fbm.saleCount
            }
            val title = beerList.first { b -> b.id == it[0].beerID }.dasaxeleba ?: "_"
            result.add(SimpleBeerRowModel(title, valueOfDiff))
        }
        _fullBarrelsListLiveData.value = result
    }

    fun onDateSelected(year: Int, month: Int, day: Int) {
        selectedDate.set(year, month, day)
        _setDayLiveData.value = dateFormatDash.format(selectedDate.time)
        getStoreBalance()
    }

    fun onDoneClick(comment: String?, barrelsEntered: List<Int>) {
        barrelOutItems.clear()
        barrelsEntered.forEachIndexed { index, count ->
            if (count > 0)
                addBarrelToList(index + 1, count)
        }

        val storeInsertRequestModel = StoreInsertRequestModel(
            comment,
            Session.get().getUserID(),
            if (isChecked) 1 else 0,
            inputBeer = receivedItemsList.map {
                StoreInsertRequestModel.ReceiveItem(
                    0, dateTimeFormat.format(selectedDate.time), it.beer.id, it.canType.id, it.count
                )
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

                receivedItemsList.clear()
                receivedItemsLiveData.value = receivedItemsList
                barrelOutItems.clear()

                getStoreBalance()
            },
            finally = {
                _doneLiveData.value = ApiResponseState.Loading(false)
            }
        )
    }

    fun addBeerReceiveItemToList(beerItem: TempBeerItemModel) {
        if (editingItemID > 0)
            receivedItemsList.removeAll { it.orderItemID == editingItemID }

        if (receivedItemsList.any {
                it.beer.id == beerItem.beer.id && it.canType.id == beerItem.canType.id
            })
            receivedItemDuplicateLiveData.value = true
        else {
            receivedItemsList.add(beerItem)
            receivedItemsLiveData.value = receivedItemsList
                .sortedBy { it.canType.sortValue }
                .sortedBy { it.beer.sortValue }
        }
        editingItemID = 0
    }

    fun addBarrelToList(barrelType: Int, count: Int) {
        barrelOutItems.add(
            StoreInsertRequestModel.BarrelOutItem(
                0,
                dateTimeFormat.format(selectedDate.time),
                barrelType,
                count
            )
        )
    }

    fun removeReceiveItemFromList(item: TempBeerItemModel) {
        receivedItemsList.removeAll { it.beer == item.beer && it.canType == item.canType }
        receivedItemsLiveData.value = receivedItemsList
            .sortedBy { it.canType.sortValue }
            .sortedBy { it.beer.sortValue }
    }

    fun clearEnteredData() {
        barrelOutItems.clear()
        receivedItemsList.clear()
        receivedItemsLiveData.value = receivedItemsList
    }

    fun getEditingData(operationTime: String) {
        _editDataReceiveLiveData.value = ApiResponseState.Loading(true)
        sendRequest(
            ApeniApiService.getInstance().getStoreHouseIoList(operationTime),
            successWithData = {
                processIoData(it)
                _editDataReceiveLiveData.value = ApiResponseState.Success(it)
            },
            finally = {
                _editDataReceiveLiveData.value = ApiResponseState.Loading(false)
            }
        )
    }

    fun processIoData(data: List<IoModel>) {
        data
            .filter { it.beerID == 0 }
            .forEach {
                addBarrelToList(it.barrelID, it.count)
            }

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
                            receivedItemsLiveData.value = receivedItemsList
                        },
                        onEdit = {})
                )
            }
    }
}
