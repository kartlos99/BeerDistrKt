package com.example.beerdistrkt.fragPages.orders

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.beerdistrkt.BaseViewModel
import com.example.beerdistrkt.fragPages.beer.domain.model.Beer
import com.example.beerdistrkt.fragPages.beer.domain.usecase.GetBeerUseCase
import com.example.beerdistrkt.fragPages.bottlemanagement.domain.usecase.GetBottleUseCase
import com.example.beerdistrkt.fragPages.customer.domain.model.Customer
import com.example.beerdistrkt.fragPages.customer.domain.usecase.GetCustomersUseCase
import com.example.beerdistrkt.fragPages.orders.models.OrderDeleteRequestModel
import com.example.beerdistrkt.fragPages.orders.models.OrderGroupModel
import com.example.beerdistrkt.fragPages.orders.models.OrderReSortModel
import com.example.beerdistrkt.fragPages.orders.models.OrderUpdateDistributorRequestModel
import com.example.beerdistrkt.fragPages.orders.repository.UserPreferencesRepository
import com.example.beerdistrkt.models.CanModel
import com.example.beerdistrkt.models.MappedUser
import com.example.beerdistrkt.models.Order
import com.example.beerdistrkt.models.OrderStatus
import com.example.beerdistrkt.models.User
import com.example.beerdistrkt.models.UserStatus
import com.example.beerdistrkt.models.bottle.BaseBottleModel
import com.example.beerdistrkt.network.ApeniApiService
import com.example.beerdistrkt.utils.ApiResponseState
import com.example.beerdistrkt.utils.Session
import com.example.beerdistrkt.utils.SingleMutableLiveDataEvent
import com.example.beerdistrkt.utils.eventValue
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject
import kotlin.math.sign

@HiltViewModel
class OrdersViewModel @Inject constructor(
    private val getBeerUseCase: GetBeerUseCase,
    private val getBottleUseCase: GetBottleUseCase,
    private val getCustomersUseCase: GetCustomersUseCase,
    private val userPreferencesRepository: UserPreferencesRepository
) : BaseViewModel() {

    private val state = SavedStateHandle()

    val searchQuery = state.getLiveData("searchQuery", "")

    private val userLiveData = database.getUsers()
    private val barrelsLiveData = database.getCansList()
    val ordersLiveData = MutableLiveData<ApiResponseState<MutableList<OrderGroupModel>>>()

    private lateinit var customers: List<Customer>
    private lateinit var beers: List<Beer>
    private lateinit var bottleList: List<BaseBottleModel>
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
    val onShowHistoryLiveData = SingleMutableLiveDataEvent<String>()
    private val allMappedUsers = mutableListOf<MappedUser>()
    private val allOrders = mutableListOf<Order>()
    private lateinit var clientRegionMap: Map<Int, List<Int>>

    var deliveryMode = false

    private val listOfGroupedOrders: MutableList<OrderGroupModel> = mutableListOf()

    private val distributorsExpandedStateMap: MutableMap<Int, Boolean> = mutableMapOf()

    private val foldsLiveData = userPreferencesRepository.userPreferencesFlow.asLiveData()

    init {
        initializeData()
        userLiveData.observeForever { usersList = it }
        barrelsLiveData.observeForever { barrelsList = it }
        _orderDayLiveData.value = dateFormatDash.format(orderDateCalendar.time)
        foldsLiveData.observeForever {
            it?.let { foldsStateString ->
                extractFoldData(foldsStateString)
            }
        }
        getAllUsers()
    }

    private fun initializeData() = viewModelScope.launch {
        beers = getBeerUseCase()
        bottleList = getBottleUseCase()
        customers = getCustomersUseCase()
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

    fun saveDistributorGroupState(distributorID: Int, state: Boolean) {
        distributorsExpandedStateMap[distributorID] = state
    }

    fun getOrders() {

        ordersLiveData.value = ApiResponseState.Loading(true)
        sendRequest(
            ApeniApiService.getInstance().getOrders(dateFormatDash.format(orderDateCalendar.time)),
            successWithData = { ordersDto ->
                clientRegionMap = ordersDto.groupBy {
                    it.clientID
                }.mapValues {
                    it.value.first().availableRegions
                }
                allOrders.clear()
                allOrders.addAll(ordersDto.map { orderDTO ->
                    orderDTO.toPm(
                        customers,
                        beers,
                        bottleList,
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
                        },
                        onHistoryClick = { orderID ->
                            onShowHistoryLiveData.eventValue = orderID
                        }
                    )
                })
                proceedOrders()
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

    private fun proceedOrders() {
        listOfGroupedOrders.clear()
        listOfGroupedOrders.addAll(
            groupOrderByDistributor(
                allOrders.filter {
                    it.customer?.name?.contains(searchQuery.value.orEmpty()) ?: false
                }
            )
        )
        listOfGroupedOrders.sortBy { gr -> gr.distributorID }

        ordersLiveData.value = ApiResponseState.Success(listOfGroupedOrders)
    }

    private fun groupOrderByDistributor(orders: List<Order>): MutableList<OrderGroupModel> {
        val groupedOrders = mutableListOf<OrderGroupModel>()
        val ordersMap = orders
            .filter { it.hasAnyItemToShow }
            .groupBy { it.distributorID }
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
                allOrders.firstOrNull {
                    it.ID == order.ID
                }?.orderStatus = OrderStatus.DELETED
                proceedOrders()
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

    private var distributorsList = listOf<MappedUser>()

    fun getDistributorsArray(clientID: Int): Array<String> {
        distributorsList = allMappedUsers
            .filter {
                it.userStatus == UserStatus.ACTIVE &&
                        clientRegionMap[clientID]?.contains(it.regionID) ?: false
            }
            .sortedBy {
                it.username
            }
        return distributorsList.map {
            if (clientRegionMap[clientID]?.size == 1)
                it.username
            else
                "${it.username} / ${it.regionName}"
        }.toTypedArray()
    }

    fun changeDistributor(orderID: Int, selectedPos: Int) {
        val selectedUser = distributorsList[selectedPos]
        val distributorID = selectedUser.userID
        val regionID = selectedUser.regionID

        val userID = Session.get().userID ?: return
        val orderChangeDistributor =
            OrderUpdateDistributorRequestModel(orderID, distributorID, regionID, userID)
        sendRequest(
            ApeniApiService.getInstance().updateOrderDistributor(orderChangeDistributor),
            finally = {
                Log.d("reOrderResult", "$it")
                if (it) getOrders()
            }
        )
    }

    private fun extractFoldData(foldsStateString: String) = foldsStateString
        .split(SEPARATOR)
        .forEach { item ->
            if (item.trim().isNotEmpty()) {
                val intData = item.trim().toInt()
                val expanded = intData.sign > 0
                val key = if (intData.sign > 0) intData else -intData
                distributorsExpandedStateMap[key] = expanded
            }
        }

    fun saveFoldsState() {
        viewModelScope.launch {
            userPreferencesRepository.saveFoldsState(
                distributorsExpandedStateMap.map { entry ->
                    if (entry.value) entry.key else -entry.key
                }.joinToString(SEPARATOR)
            )
        }
    }

    fun filterOrders(query: String) {
        proceedOrders()
    }

    companion object {
        const val TAG = "ordersVM"
        const val SEPARATOR = ","
    }
}
