package com.example.beerdistrkt.fragPages.orders

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.beerdistrkt.BaseViewModel
import com.example.beerdistrkt.fragPages.orders.models.OrderDeleteRequestModel
import com.example.beerdistrkt.fragPages.orders.models.OrderGroupModel
import com.example.beerdistrkt.fragPages.orders.models.OrderReSortModel
import com.example.beerdistrkt.fragPages.orders.models.OrderUpdateDistributorRequestModel
import com.example.beerdistrkt.models.*
import com.example.beerdistrkt.network.ApeniApiService
import com.example.beerdistrkt.storage.ObjectCache
import com.example.beerdistrkt.utils.ApiResponseState
import com.example.beerdistrkt.utils.Session
import java.util.*

class OrdersViewModel : BaseViewModel() {

    private val _response = MutableLiveData<String>()
    val response: LiveData<String>
        get() = _response

    private val clientsLiveData = database.getAllObieqts()
    private val beersLiveData = database.getBeerList()
    private val userLiveData = database.getUsers()
    private val barrelsLiveData = database.getCansList()
    val ordersLiveData = MutableLiveData<ApiResponseState<MutableList<OrderGroupModel>>>()

    private lateinit var clients: List<Obieqti>
    private lateinit var beers: List<BeerModel>
    private lateinit var usersList: List<User>
    lateinit var barrelsList: List<CanModel>

    var orderDateCalendar: Calendar = Calendar.getInstance()

    private val _orderDayLiveData = MutableLiveData<String>()
    val orderDayLiveData: LiveData<String>
        get() = _orderDayLiveData

    val askForOrderDeleteLiveData = MutableLiveData<Order?>(null)
    val orderDeleteLiveData = MutableLiveData<ApiResponseState<Pair<Int, Int>>>()
    val editOrderLiveData = MutableLiveData<Order?>(null)
    val changeDistributorLiveData = MutableLiveData<Order?>(null)
    val onItemClickLiveData = MutableLiveData<Order?>(null)

    var deliveryMode = false

    private val listOfGroupedOrders: MutableList<OrderGroupModel> = mutableListOf()

    private val distributorsExpandedStateMap: MutableMap<Int, Boolean> = mutableMapOf()

    init {
        val recoverState = ObjectCache.getInstance().get(MutableMap::class, SAVED_EXP_STATE)
            ?: mutableMapOf<Int, Boolean>()
        recoverState.forEach {
            distributorsExpandedStateMap[it.key as Int] = (it.value as Boolean)
        }
        clientsLiveData.observeForever { clients = it }
        beersLiveData.observeForever { beers = it }
        userLiveData.observeForever { usersList = it }
        barrelsLiveData.observeForever { barrelsList = it }
        _orderDayLiveData.value = dateFormatDash.format(orderDateCalendar.time)
    }

    fun saveDistributorGroupState(distributorID: Int, state: Boolean) {
        distributorsExpandedStateMap[distributorID] = state
    }

    fun getOrders() {

        ordersLiveData.value = ApiResponseState.Loading(true)
        sendRequest(
            ApeniApiService.getInstance().getOrders(dateFormatDash.format(orderDateCalendar.time)),
            successWithData = {
                listOfGroupedOrders.clear()
                listOfGroupedOrders.addAll(groupOrderByDistributor(it.map { orderDTO ->
                    orderDTO.toPm(
                        clients,
                        beers,
                        onDeleteClick = { order ->
                            askForOrderDeleteLiveData.value = order
                        },
                        onEditClick = { order ->
                            editOrderLiveData.value = order
                        },
                        onChangeDistributorClick = { order ->
                            changeDistributorLiveData.value = order
                        },
                        onItemClick = { order ->
                            if (deliveryMode) onItemClickLiveData.value = order
                        }
                    )
                }))
                listOfGroupedOrders.sortBy { gr -> gr.distributorID }

                ordersLiveData.value = ApiResponseState.Success(listOfGroupedOrders)
            },
            failure = {
                Log.d("getOrder", "failed: ${it.message}")
                ordersLiveData.value = ApiResponseState.ApiError(999, it.message ?: "")
            },
            finally = {
                ordersLiveData.value = ApiResponseState.Loading(false)
            }
        )
    }

    private fun groupOrderByDistributor(orders: List<Order>): MutableList<OrderGroupModel> {
        val groupedOrders = mutableListOf<OrderGroupModel>()
        val ordersMap = orders.groupBy { it.distributorID }
        ordersMap.forEach {
            val distributorName = usersList.firstOrNull { user ->
                user.id == it.key.toString()
            }?.name ?: "_"
            groupedOrders.add(
                OrderGroupModel(
                    distributorID = it.key,
                    distributorName = distributorName,
                    ordersList = it.value
                        .sortedBy { order -> order.sortValue }
                        .sortedBy { order -> order.orderStatus }
                        .toMutableList(),
                    isExpanded = distributorsExpandedStateMap[it.key] ?: true
                )
            )
        }

        return groupedOrders
    }

    fun onDateSelected(year: Int, month: Int, day: Int) {
        orderDateCalendar.set(year, month, day)
        _orderDayLiveData.value = dateFormatDash.format(orderDateCalendar.time)
        getOrders()
    }

    fun deleteOrder(order: Order) {
        Log.d("onDelete", order.toString())
        val userID = Session.get().userID ?: return
        sendRequest(
            ApeniApiService.getInstance().deleteOrder(OrderDeleteRequestModel(order.ID, userID)),
            success = {
                listOfGroupedOrders.forEachIndexed { index1, orderGroupModel ->
                    val index = orderGroupModel.ordersList.indexOf(order)
                    if (index != -1) {
                        orderGroupModel.ordersList[index] = orderGroupModel.ordersList[index].copy(orderStatus = OrderStatus.DELETED)
                        orderDeleteLiveData.value = ApiResponseState.Success(Pair(index1, index))
                        return@sendRequest
                    }
                }
            }
        )
    }

    fun onOrderDrag(orderID: Int, newSortValue: Double) {
        val orderReSort = OrderReSortModel(orderID, newSortValue)
        sendRequest(
            ApeniApiService.getInstance().updateOrderSortValue(orderReSort),
            finally = {
                Log.d("reOrderResult", "$it")
            }
        )
    }

    fun getDistributorsArray(): Array<String> {
        return usersList.map {
            it.username
        }.toTypedArray()
    }

    fun changeDistributor(orderID: Int, distributorPos: Int) {
        val distributorID = usersList[distributorPos].id
        val userID = Session.get().userID ?: return
        val orderChangeDistributor =
            OrderUpdateDistributorRequestModel(orderID, distributorID, userID)
        sendRequest(
            ApeniApiService.getInstance().updateOrderDistributor(orderChangeDistributor),
            finally = {
                Log.d("reOrderResult", "$it")
                if (it) getOrders()
            }
        )
    }

    override fun onCleared() {
        super.onCleared()
        ObjectCache.getInstance()
            .put(MutableMap::class, SAVED_EXP_STATE, distributorsExpandedStateMap)
    }

    companion object {
        const val TAG = "ordersVM"
        const val SAVED_EXP_STATE = "savedState"
    }
}
