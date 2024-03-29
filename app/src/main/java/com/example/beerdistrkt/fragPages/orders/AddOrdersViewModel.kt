package com.example.beerdistrkt.fragPages.orders

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.beerdistrkt.BaseViewModel
import com.example.beerdistrkt.fragPages.login.models.WorkRegion
import com.example.beerdistrkt.fragPages.orders.models.OrderRequestModel
import com.example.beerdistrkt.fragPages.realisation.RealisationType
import com.example.beerdistrkt.fragPages.realisation.models.TempRealisationModel
import com.example.beerdistrkt.models.BeerModelBase
import com.example.beerdistrkt.models.CanModel
import com.example.beerdistrkt.models.DebtResponse
import com.example.beerdistrkt.models.MappedUser
import com.example.beerdistrkt.models.ObiectWithPrices
import com.example.beerdistrkt.models.Obieqti
import com.example.beerdistrkt.models.Order
import com.example.beerdistrkt.models.OrderStatus.ACTIVE
import com.example.beerdistrkt.models.OrderStatus.CANCELED
import com.example.beerdistrkt.models.OrderStatus.COMPLETED
import com.example.beerdistrkt.models.TempBeerItemModel
import com.example.beerdistrkt.models.User
import com.example.beerdistrkt.models.bottle.BaseBottleModel
import com.example.beerdistrkt.models.bottle.TempBottleItemModel
import com.example.beerdistrkt.network.ApeniApiService
import com.example.beerdistrkt.storage.ObjectCache
import com.example.beerdistrkt.storage.ObjectCache.Companion.BARREL_LIST_ID
import com.example.beerdistrkt.storage.ObjectCache.Companion.BEER_LIST_ID
import com.example.beerdistrkt.storage.ObjectCache.Companion.BOTTLE_LIST_ID
import com.example.beerdistrkt.storage.ObjectCache.Companion.USERS_LIST_ID
import com.example.beerdistrkt.utils.ApiResponseState
import com.example.beerdistrkt.utils.Session
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

class AddOrdersViewModel(private val clientID: Int, var editingOrderID: Int) : BaseViewModel() {

    val getDebtLiveData = MutableLiveData<ApiResponseState<DebtResponse>>()
    val clientLiveData = MutableLiveData<ObiectWithPrices>()

    val beerList = ObjectCache.getInstance().getList(BeerModelBase::class, BEER_LIST_ID)
        ?: mutableListOf()
    val bottleList = ObjectCache.getInstance().getList(BaseBottleModel::class, BOTTLE_LIST_ID)
        ?: mutableListOf()
    val cansList = ObjectCache.getInstance().getList(CanModel::class, BARREL_LIST_ID)
        ?: listOf()
    private var usersList = ObjectCache.getInstance().getList(User::class, USERS_LIST_ID)
        ?.sortedBy { it.username }
        ?: mutableListOf()

    val visibleDistributors
        get() = usersList.filter {
            it.isActive
        }

    lateinit var selectedDistributor: User
    private var selectedDistributorRegionID: Int = 0
    var selectedStatus = ACTIVE
    val orderStatusList = listOf(ACTIVE, COMPLETED, CANCELED)

    private val orderItemsList = mutableListOf<TempBeerItemModel>()
    private val bottleOrderItemsList = mutableListOf<TempBottleItemModel>()
    val tempOrderLiveData = MutableLiveData<TempRealisationModel>()

    var orderDateCalendar: Calendar = Calendar.getInstance()

    private val _orderDayLiveData = MutableLiveData<String>()
    val orderDayLiveData: LiveData<String>
        get() = _orderDayLiveData

    private val _addOrderLiveData = MutableLiveData<ApiResponseState<String>>()
    val addOrderLiveData: LiveData<ApiResponseState<String>>
        get() = _addOrderLiveData

    val getOrderLiveData = MutableLiveData<Order?>()
    private val clientsLiveData = database.getAllObieqts()
    private lateinit var clients: List<Obieqti>

    val orderItemEditLiveData = MutableLiveData<TempBeerItemModel?>()
    val bottleOrderItemEditLiveData = MutableLiveData<TempBottleItemModel?>()
    private var editingOrderItemID = -1

    lateinit var availableRegions: List<WorkRegion>
    private val allMappedUsers = mutableListOf<MappedUser>()

    val eventsFlow = MutableSharedFlow<Event>()

    val realisationStateFlow = MutableStateFlow(RealisationType.BARREL)

    val realisationType: RealisationType
        get() {
            return realisationStateFlow.value
        }

    init {
        Log.d("addOrderVM", clientID.toString())

        clientsLiveData.observeForever { clients = it }
        getClient()
        _orderDayLiveData.value = dateFormatDash.format(orderDateCalendar.time)
        selectedDistributorRegionID = Session.get().region?.regionID?.toInt() ?: 0
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

    private fun getDebt() {
        sendRequest(
            ApeniApiService.getInstance().getDebt(clientID),
            successWithData = {
                availableRegions = it.availableRegions
                getDebtLiveData.value = ApiResponseState.Success(it)
                getOrder(editingOrderID)
            }
        )
    }

    private fun getClient() {
        ioScope.launch {
            val clientData = database.getCustomerWithPrices(clientID)
            uiScope.launch {
                clientLiveData.value = clientData
            }
        }
    }

    private fun addOrderItemsToList(order: Order) {
        order.items.forEach {
            orderItemsList.add(
                it.toTempBeerItemModel(
                    cansList,
                    onRemove = ::removeOrderItemFromList,
                    onEdit = ::editOrderItemFromList
                )
            )
        }
        order.bottleItems.forEach {
            bottleOrderItemsList.add(
                it.toTempBottleItemModel(
                    onRemove = ::removeOrderItemFromList,
                    onEdit = ::editOrderItemFromList
                )
            )
        }
        updateTempOrderItemList()
    }

    fun addOrderItemToList(item: TempBeerItemModel) {
        if (editingOrderItemID > 0)
            orderItemsList.removeAll {
                it.orderItemID == editingOrderItemID
            }

        if (orderItemsList.any {
                it.beer.id == item.beer.id && it.canType.id == item.canType.id
            })
            viewModelScope.launch {
                eventsFlow.emit(Event.DuplicateBarrelItem)
            }
        else {
            orderItemsList.add(item)
            updateTempOrderItemList()
        }
        editingOrderItemID = 0
    }

    fun addBottleOrderItem(item: TempBottleItemModel) {
        if (editingOrderItemID > 0)
            bottleOrderItemsList.removeAll {
                it.orderItemID == editingOrderItemID
            }

        if (bottleOrderItemsList.any {
                it.bottle.id == item.bottle.id
            })
            viewModelScope.launch {
                eventsFlow.emit(Event.DuplicateBottleItem)
            }
        else {
            bottleOrderItemsList.add(item)
            updateTempOrderItemList()
        }
        editingOrderItemID = 0
    }

    private fun updateTempOrderItemList() {
        tempOrderLiveData.value = TempRealisationModel(
            orderItemsList
                .sortedBy { it.canType.name }
                .sortedBy { it.beer.sortValue },
            bottleOrderItemsList
                .sortedBy { it.bottle.sortValue }
        )
    }

    fun removeOrderItemFromList(item: TempBeerItemModel) {
        orderItemsList.remove(item)
        updateTempOrderItemList()
    }

    fun removeOrderItemFromList(item: TempBottleItemModel) {
        bottleOrderItemsList.remove(item)
        updateTempOrderItemList()
    }

    private fun editOrderItemFromList(item: TempBeerItemModel) {
        editingOrderItemID = item.orderItemID
        orderItemEditLiveData.value = item
    }

    private fun editOrderItemFromList(item: TempBottleItemModel) {
        editingOrderItemID = item.orderItemID
        bottleOrderItemEditLiveData.value = item
    }

    fun addOrder(comment: String, isChecked: Boolean) {
        if (callIsBlocked) return
        callIsBlocked = true

        val orderRequestModel = OrderRequestModel(
            0,
            dateFormatDash.format(orderDateCalendar.time),
            ACTIVE.data,
            selectedDistributor.getIntID(),
            clientID,
            selectedDistributorRegionID,
            comment,
            Session.get().userID ?: "0",
            orderItemsList.map { it.toRequestOrderItem(isChecked) },
            bottleOrderItemsList.map {
                it.toRequestOrderItem(isChecked)
            }
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
        if (callIsBlocked) return
        callIsBlocked = true

        val orderRequestModel = OrderRequestModel(
            editingOrderID,
            dateFormatDash.format(orderDateCalendar.time),
            selectedStatus.data,
            selectedDistributor.getIntID(),
            clientID,
            selectedDistributorRegionID,
            comment,
            Session.get().userID ?: "0",
            orderItemsList.map { it.toRequestOrderItem(isChecked) },
            bottleOrderItemsList.map {
                it.toRequestOrderItem(isChecked)
            }
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

                        val order = it[0].toPm(clients, beerList, bottleList, {}, {})

                        getOrderLiveData.value = order
                        addOrderItemsToList(order)
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

    fun updateDistributorList(selectedRegionID: Int) {
        selectedDistributorRegionID = selectedRegionID
        usersList = allMappedUsers
            .filter { it.regionID == selectedRegionID && it.isActive }
            .map { it.toUser() }
            .sortedBy { it.username }

    }

    fun getDistributorNamesList(): List<String> {
        return visibleDistributors
            .map { "${it.username} (${it.name})" }
    }

    fun getDistributorIndex(distributorID: String): Int {
        val userIds = visibleDistributors
            .map { it.id }

        return if (userIds.indexOf(distributorID) >= 0)
            userIds.indexOf(distributorID)
        else
            userIds.indexOf(Session.get().userID ?: return 0)
    }

    fun switchToBarrel() {
        realisationStateFlow.value = RealisationType.BARREL
    }

    fun switchToBottle() {
        realisationStateFlow.value = RealisationType.BOTTLE
    }

    fun hasNoOrderItems(): Boolean = orderItemsList.isEmpty() && bottleOrderItemsList.isEmpty()
}
