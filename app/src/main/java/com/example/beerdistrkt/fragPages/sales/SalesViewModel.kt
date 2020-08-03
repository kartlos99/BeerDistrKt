package com.example.beerdistrkt.fragPages.sales

import android.os.Build
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.beerdistrkt.BaseViewModel
import com.example.beerdistrkt.fragPages.sales.models.AddXarjiRequestModel
import com.example.beerdistrkt.models.*
import com.example.beerdistrkt.network.ApeniApiService
import com.example.beerdistrkt.storage.ObjectCache
import com.example.beerdistrkt.utils.ApiResponseState
import com.example.beerdistrkt.utils.Session
import java.util.*
import kotlin.collections.ArrayList

class SalesViewModel : BaseViewModel() {

    val TAG = "Sales_VM"
    var takeMoney = 0.0
    var priceSum = 0.0
    val xarjebi: ArrayList<Xarji> = ArrayList()

    val cansList = ObjectCache.getInstance().getList(CanModel::class, "canList")
        ?: listOf()
    val usersList = mutableListOf(User.getBaseUser())
    private val realUsersList = ObjectCache.getInstance().getList(User::class, "userList")
        ?: listOf()

    private val _deleteXarjiLiveData = MutableLiveData<ApiResponseState<String>>()
    val deleteXarjiLiveData: LiveData<ApiResponseState<String>>
        get() = _deleteXarjiLiveData

    private val _addXarjiLiveData = MutableLiveData<ApiResponseState<String>>()
    val addXarjiLiveData: LiveData<ApiResponseState<String>>
        get() = _addXarjiLiveData

    private val _xarjiListExpandedLiveData = MutableLiveData<Boolean>()
    val xarjiListExpandedLiveData: LiveData<Boolean>
        get() = _xarjiListExpandedLiveData

    private val _selectedDayLiveData = MutableLiveData<String>()
    val selectedDayLiveData: LiveData<String>
        get() = _selectedDayLiveData

    private val _realizationDayLiveData = MutableLiveData<RealizationDay>()
    val realizationDayLiveData: LiveData<RealizationDay>
        get() = _realizationDayLiveData

    private val _salesLiveData = MutableLiveData<List<SaleInfo>>()
    val salesLiveData: LiveData<List<SaleInfo>>
        get() = _salesLiveData

    private val _barrelsLiveData = MutableLiveData<List<BarrelIO>>()
    val barrelsLiveData: LiveData<List<BarrelIO>>
        get() = _barrelsLiveData

    val usersLiveData = database.getUsers()

    var selectedDistributorID = 0

    var calendar: Calendar = Calendar.getInstance()

    fun isToday(): Boolean = dateFormatDash.format(calendar.time) == dateFormatDash.format(Date())

    fun onDataSelected(year: Int, month: Int, day: Int) {
        calendar.set(year, month, day)
        prepareData()
    }

    fun btnXarjExpandClick() {
        _xarjiListExpandedLiveData.value = !_xarjiListExpandedLiveData.value!!
    }

    fun changeDay(days: Int) {
        calendar.add(Calendar.DAY_OF_MONTH, days)
        _xarjiListExpandedLiveData.value = false
        prepareData()
    }

    fun prepareData() {
        _selectedDayLiveData.value = dateFormatDash.format(calendar.time)
        Log.d(TAG, selectedDayLiveData.value!!)
        getDayInfo(selectedDayLiveData.value!!, selectedDistributorID)
        _xarjiListExpandedLiveData.value = false
    }

    lateinit var userMap: Map<String, List<User>>
    fun formUserMap(userList: List<User>) {
        userMap = userList.groupBy { it.id }
        Log.d(TAG, userMap.toString())
    }

    init {
        usersList.addAll(realUsersList)
        _selectedDayLiveData.value = dateFormatDash.format(calendar.time)
        _xarjiListExpandedLiveData.value = false

        getDayInfo(selectedDayLiveData.value!!, selectedDistributorID)
    }

    fun getDayInfo(date: String, distributorID: Int) {
        sendRequest(
            ApeniApiService.getInstance().getDayInfo(date, distributorID),
            successWithData = {
                val sales: ArrayList<SaleInfo> = ArrayList()
                sales.addAll(it.sale)
                xarjebi.clear()
                xarjebi.addAll(it.xarji)

                priceSum = sales.sumByDouble { obj -> obj.price }

                Log.d(TAG, it.toString())
                takeMoney = it.takenMoney
                Log.d(TAG, takeMoney.toString())

                val barrelIOList = it.barrels
                barrelIOList.forEach { bIO ->
                    bIO.barrelName = cansList.find { can -> can.id == bIO.canTypeID }?.name
                }
                _barrelsLiveData.value = barrelIOList
                _realizationDayLiveData.value = it
                _salesLiveData.value = it.sale
            },
            finally = {
                Log.d(TAG, "dayReq $it")
            }
        )
    }

    fun deleteXarji(request: DeleteRequest) {
        sendRequest(
            apiRequest = ApeniApiService.getInstance().deleteRecord(request),
            success = {
                Log.d(TAG, "saccsesfuli Deleted")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    xarjebi.removeIf { x -> x.id == request.recordID }
                } else {
                    for (xarji in xarjebi) {
                        if (xarji.id == request.recordID) {
                            xarjebi.remove(xarji)
                        }
                    }
                }
                _deleteXarjiLiveData.value = ApiResponseState.Success("")
            },
            finally = {
                Log.d(TAG, "saccsesfuli Deleted = $it")
            }
        )
    }

    fun addXarji(comment: String, amount: String) {
        Log.d(TAG, " comm $amount")
        sendRequest(
            ApeniApiService.getInstance().addXarji(
                AddXarjiRequestModel(
                    Session.get().userID!!,
                    amount.toDouble(),
                    comment,
                    dateTimeFormat.format(calendar.time)
                )

            ),
            successWithData = {
                xarjebi.add(
                    Xarji(
                        comment,
                        Session.get().userID!!,
                        it.toString(),
                        amount = amount.toFloat()
                    )
                )
                _addXarjiLiveData.value = ApiResponseState.Success("")
            },
            responseFailure = { i: Int, s: String ->
                _addXarjiLiveData.value = ApiResponseState.ApiError(i, s)
            }
        )
    }

    fun addXarjiComplited() {
        if (_addXarjiLiveData.value !is ApiResponseState.Sleep)
            _addXarjiLiveData.value = ApiResponseState.Sleep
    }

    fun deleteXarjiComplited() {
        if (_deleteXarjiLiveData.value !is ApiResponseState.Sleep)
            _deleteXarjiLiveData.value = ApiResponseState.Sleep
    }
}
