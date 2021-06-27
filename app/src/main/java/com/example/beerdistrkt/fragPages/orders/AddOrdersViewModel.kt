package com.example.beerdistrkt.fragPages.orders

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.beerdistrkt.BaseViewModel
import com.example.beerdistrkt.fragPages.login.models.WorkRegion
import com.example.beerdistrkt.fragPages.orders.models.OrderRequestModel
import com.example.beerdistrkt.models.*
import com.example.beerdistrkt.network.ApeniApiService
import com.example.beerdistrkt.storage.ObjectCache
import com.example.beerdistrkt.storage.ObjectCache.Companion.BARREL_LIST_ID
import com.example.beerdistrkt.storage.ObjectCache.Companion.BEER_LIST_ID
import com.example.beerdistrkt.storage.ObjectCache.Companion.USERS_LIST_ID
import com.example.beerdistrkt.utils.ApiResponseState
import com.example.beerdistrkt.utils.Session
import kotlinx.coroutines.launch
import java.util.*

class AddOrdersViewModel(private val clientID: Int, var editingOrderID: Int) : BaseViewModel() {

    val getDebtLiveData = MutableLiveData<ApiResponseState<DebtResponse>>()
    val clientLiveData = MutableLiveData<ObiectWithPrices>()

    val beerList = ObjectCache.getInstance().getList(BeerModel::class, BEER_LIST_ID)
        ?: mutableListOf()
    val cansList = ObjectCache.getInstance().getList(CanModel::class, BARREL_LIST_ID)
        ?: listOf()
    var usersList = ObjectCache.getInstance().getList(User::class, USERS_LIST_ID)
        ?: listOf()

    var selectedCan: CanModel? = null
    var selectedDistributorID: Int = 0
    var selectedDistributorRegionID: Int = 0
    var selectedStatus = OrderStatus.ACTIVE
    val orderStatusList = listOf(OrderStatus.ACTIVE, OrderStatus.COMPLETED, OrderStatus.CANCELED)

    val orderItemsList = mutableListOf<TempBeerItemModel>()
    val orderItemsLiveData = MutableLiveData<List<TempBeerItemModel>>()

    var orderDateCalendar: Calendar = Calendar.getInstance()

    private val _orderDayLiveData = MutableLiveData<String>()
    val orderDayLiveData: LiveData<String>
        get() = _orderDayLiveData

    val orderItemDuplicateLiveData = MutableLiveData<Boolean>(false)

    private val _addOrderLiveData = MutableLiveData<ApiResponseState<String>>()
    val addOrderLiveData: LiveData<ApiResponseState<String>>
        get() = _addOrderLiveData

    val getOrderLiveData = MutableLiveData<Order?>()
    private val clientsLiveData = database.getAllObieqts()
    private lateinit var clients: List<Obieqti>

    val orderItemEditLiveData = MutableLiveData<TempBeerItemModel?>()
    var editingOrderItemID = -1

    lateinit var availableRegions: List<WorkRegion>
    private val allMappedUsers = mutableListOf<MappedUser>()

    init {
        Log.d("addOrderVM", clientID.toString())

        clientsLiveData.observeForever { clients = it }
        getClient()
        _orderDayLiveData.value = dateFormatDash.format(orderDateCalendar.time)
        getDebt()
        getAllUsers()
    }

    private fun getAllUsers() {
        sendRequest(
            ApeniApiService.getInstance().getAllUsers(),
            successWithData = {
                allMappedUsers.clear()
                allMappedUsers.addAll(it)
            }
        )
    }

    fun updateDistributorList(selectedRegionID: Int) {
        selectedDistributorRegionID = selectedRegionID
        usersList = allMappedUsers.filter {
            it.regionID == selectedRegionID
        }.map {
            User(
                it.userID.toString(),
                it.username,
                it.userDisplayName,
                "", "", "", "", ""
            )
        }
    }

    private fun getDebt(){
        sendRequest(
            ApeniApiService.getInstance().getDebt(clientID),
            successWithData = {
                availableRegions = it.availableRegions
                selectedDistributorRegionID = Session.get().region?.regionID?.toInt() ?: availableRegions.first().regionID.toInt()
                getDebtLiveData.value = ApiResponseState.Success(it)
                getOrder(editingOrderID)
            }
        )
    }

    private fun getClient() {
        ioScope.launch {
            val clientData = database.getObiectsWithPrices(clientID)
            uiScope.launch {
                clientLiveData.value = clientData
            }
        }
    }

    fun setCan(pos: Int) {
        selectedCan = if (pos >= 0)
            cansList[pos]
        else
            null
    }

    fun addOrderItemsToList(itemsList: List<TempBeerItemModel>) {
        itemsList.forEach {
            orderItemsList.add(it)
        }
        orderItemsLiveData.value =
            orderItemsList.sortedBy { it.canType.name }.sortedBy { it.beer.sortValue }
    }

    fun addOrderItemToList(item: TempBeerItemModel) {
        if (editingOrderItemID > 0)
            orderItemsList.removeAll {
                it.orderItemID == editingOrderItemID
            }

        if (orderItemsList.any {
                it.beer.id == item.beer.id && it.canType.id == item.canType.id
            })
            orderItemDuplicateLiveData.value = true
        else {
            orderItemsList.add(item)
            orderItemsLiveData.value =
                orderItemsList.sortedBy { it.canType.name }.sortedBy { it.beer.sortValue }
        }
    }

    fun removeOrderItemFromList(item: TempBeerItemModel) {
        orderItemsList.remove(item)
        orderItemsLiveData.value =
            orderItemsList.sortedBy { it.canType.name }.sortedBy { it.beer.sortValue }
    }

    fun editOrderItemFromList(item: TempBeerItemModel) {
        editingOrderItemID = item.orderItemID
        orderItemEditLiveData.value = item
    }

    fun addOrder(comment: String, isChecked: Boolean) {

        val orderRequestModel = OrderRequestModel(
            0,
            dateFormatDash.format(orderDateCalendar.time),
            OrderStatus.ACTIVE.data,
            selectedDistributorID,
            clientID,
            selectedDistributorRegionID,
            comment,
            Session.get().userID ?: "0",
            orderItemsList.map { it.toRequestOrderItem(isChecked, Session.get().userID ?: "0") }
        )

        _addOrderLiveData.value = ApiResponseState.Loading(true)
        sendRequest(
            ApeniApiService.getInstance().addOrder(orderRequestModel),
            successWithData = {
                Log.d("itemCount", it)
                _addOrderLiveData.value = ApiResponseState.Success(it)
            },
            finally = { _addOrderLiveData.value = ApiResponseState.Loading(false) }
        )
    }

    fun editOrder(comment: String, isChecked: Boolean) {
        val orderRequestModel = OrderRequestModel(
            editingOrderID,
            dateFormatDash.format(orderDateCalendar.time),
            selectedStatus.data,
            selectedDistributorID,
            clientID,
            selectedDistributorRegionID,
            comment,
            Session.get().userID ?: "0",
            orderItemsList.map { it.toRequestOrderItem(isChecked, Session.get().userID ?: "0") }
        )
        Log.d("updateOrder1", orderRequestModel.toString())
        _addOrderLiveData.value = ApiResponseState.Loading(true)
        sendRequest(
            ApeniApiService.getInstance().updateOrder(orderRequestModel),
            successWithData = {
                Log.d("updateOrder", it)
                _addOrderLiveData.value = ApiResponseState.Success(it)
            },
            finally = { _addOrderLiveData.value = ApiResponseState.Loading(false) }
        )
    }

    fun onOrderDateSelected(year: Int, month: Int, day: Int) {
        orderDateCalendar.set(year, month, day)
        _orderDayLiveData.value = dateFormatDash.format(orderDateCalendar.time)
    }

    private fun getOrder(id: Int) {
        if (id == 0)
            getActiveOrderID()
        else {
            editingOrderID = id
            sendRequest(
                ApeniApiService.getInstance().getOrderByID(id),
                successWithData = {
                    Log.d("editingOrder", it.toString())
                    if (it.isNotEmpty()) {

                        val order = it[0].toPm(clients, beerList, {}, {})

                        getOrderLiveData.value = order
                        addOrderItemsToList(order.items.map { itm ->
                            itm.toTempBeerItemModel(
                                cansList,
                                onRemove = ::removeOrderItemFromList,
                                onEdit = ::editOrderItemFromList
                            )
                        })
                        val date = dateFormatDash.parse(order.orderDate)
                        orderDateCalendar.time = date ?: Date()
                        _orderDayLiveData.value = dateFormatDash.format(orderDateCalendar.time)
                    }
                },
                finally = { _addOrderLiveData.value = ApiResponseState.Loading(false) }
            )
        }
    }

    private fun getActiveOrderID() {
        sendRequest(
            ApeniApiService.getInstance().getLastActiveOrderID(clientID),
            successWithData = {
                if (it > 0) getOrder(it)
            }
        )
    }
}
