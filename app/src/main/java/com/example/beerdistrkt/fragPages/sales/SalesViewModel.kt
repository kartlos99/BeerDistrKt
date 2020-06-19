package com.example.beerdistrkt.fragPages.sales

import android.os.Build
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.beerdistrkt.BaseViewModel
import com.example.beerdistrkt.models.*
import com.example.beerdistrkt.network.ApeniApiService
import com.example.beerdistrkt.utils.ApiResponseState
import com.example.beerdistrkt.utils.Session
import java.util.*
import kotlin.collections.ArrayList

class SalesViewModel : BaseViewModel() {

    val TAG = "Sales_VM"
    var k30empty = 0.0f
    var k50empty = 0.0f
    var takeMoney = 0.0f
    var k3r = 0.0
    var k5r = 0.0
    var priceSum = 0.0
    val xarjebi: ArrayList<Xarji> = ArrayList()

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

    val usersLiveData = database.getUsers()

    val distr_id = 0

    var calendar: Calendar = Calendar.getInstance()

    fun onDataSelected(year: Int, month: Int, day: Int) {
        calendar.set(year, month, day)
        prepearData()
    }

    fun btnXarjExpandClick() {
        _xarjiListExpandedLiveData.value = !_xarjiListExpandedLiveData.value!!
    }

    fun changeDay(days: Int) {
        calendar.add(Calendar.DAY_OF_MONTH, days)
        _xarjiListExpandedLiveData.value = false
        prepearData()
    }

    fun prepearData() {
        _selectedDayLiveData.value = dateFormatDash.format(calendar.time)
        Log.d(TAG, selectedDayLiveData.value!!)
        getDayInfo(selectedDayLiveData.value!!, distr_id)
    }

    lateinit var userMap: Map<String, List<User>>
    fun formUserMap(userList: List<User>) {
        userMap = userList.groupBy { it.id }
        Log.d(TAG, userMap.toString())
    }

    init {
        _selectedDayLiveData.value = dateFormatDash.format(calendar.time)
        _xarjiListExpandedLiveData.value = false

        getDayInfo(selectedDayLiveData.value!!, distr_id)
    }

    fun getDayInfo(date: String, distributorID: Int) {
        sendRequest(
            ApeniApiService.getInstance().getDayInfo(date, distributorID),
            successWithData = {
                val sales: ArrayList<SaleInfo> = ArrayList()
                sales.addAll(it.realizebuli)
                xarjebi.clear()
                xarjebi.addAll(it.xarji)

                priceSum = sales.sumByDouble { obj -> obj.price }
                k3r = sales.sumByDouble { obj -> obj.k30 }
                k5r = sales.sumByDouble { obj -> obj.k50 }

                Log.d(TAG, it.toString())
                takeMoney = it.output.money
                k30empty = it.output.k30
                k50empty = it.output.k50
                Log.d(TAG, takeMoney.toString())

                _realizationDayLiveData.value = it
                _salesLiveData.value = it.realizebuli
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
            apiRequest = ApeniApiService.getInstance().addXarji(
                Session.get().userID!!,
                amount,
                comment
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
