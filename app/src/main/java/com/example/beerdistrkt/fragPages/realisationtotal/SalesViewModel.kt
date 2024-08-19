package com.example.beerdistrkt.fragPages.realisationtotal

import android.os.Build
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.beerdistrkt.BaseViewModel
import com.example.beerdistrkt.fragPages.realisationtotal.models.PaymentType
import com.example.beerdistrkt.models.BarrelIO
import com.example.beerdistrkt.models.CanModel
import com.example.beerdistrkt.models.DeleteRequest
import com.example.beerdistrkt.models.MoneyInfo
import com.example.beerdistrkt.models.SaleInfo
import com.example.beerdistrkt.models.User
import com.example.beerdistrkt.models.Xarji
import com.example.beerdistrkt.network.ApeniApiService
import com.example.beerdistrkt.storage.ObjectCache
import com.example.beerdistrkt.storage.ObjectCache.Companion.BARREL_LIST_ID
import com.example.beerdistrkt.storage.ObjectCache.Companion.USERS_LIST_ID
import com.example.beerdistrkt.utils.ApiResponseState
import java.util.Calendar
import java.util.Date

class SalesViewModel : BaseViewModel() {

    private var takeMoney = mutableListOf<MoneyInfo>()
    var priceSum = 0.0
    val expenses: ArrayList<Xarji> = ArrayList()

    private val barrelsList = ObjectCache.getInstance().getList(CanModel::class, BARREL_LIST_ID)
        ?: listOf()
    val visibleDistributors = mutableListOf<User>()

    private val _deleteExpenseLiveData = MutableLiveData<ApiResponseState<String>>()
    val deleteExpenseLiveData: LiveData<ApiResponseState<String>>
        get() = _deleteExpenseLiveData

    private val _selectedDayLiveData = MutableLiveData<String>()
    val selectedDayLiveData: LiveData<String>
        get() = _selectedDayLiveData

    private val _salesLiveData = MutableLiveData<List<SaleInfo>>()
    val salesLiveData: LiveData<List<SaleInfo>>
        get() = _salesLiveData

    private val _barrelsLiveData = MutableLiveData<List<BarrelIO>>()
    val barrelsLiveData: LiveData<List<BarrelIO>>
        get() = _barrelsLiveData

    private val _expenseLiveData = MutableLiveData<List<Xarji>>()
    val expenseLiveData: LiveData<List<Xarji>>
        get() = _expenseLiveData

    val usersLiveData = database.getUsers()

    var selectedDistributorID = 0

    var calendar: Calendar = Calendar.getInstance()

    fun isToday(): Boolean = dateFormatDash.format(calendar.time) == dateFormatDash.format(Date())

    fun onDataSelected(year: Int, month: Int, day: Int) {
        calendar.set(year, month, day)
        prepareData()
    }

    fun changeDay(days: Int) {
        calendar.add(Calendar.DAY_OF_MONTH, days)
        prepareData()
    }

    fun prepareData() {
        _selectedDayLiveData.value = dateFormatDash.format(calendar.time)
        Log.d(TAG, selectedDayLiveData.value!!)
        getDayInfo(selectedDayLiveData.value!!, selectedDistributorID)
    }

    lateinit var userMap: Map<String, List<User>>
    fun formUserMap(userList: List<User>) {
        userMap = userList.groupBy { it.id }
        Log.d(TAG, userMap.toString())
    }

    init {
        _selectedDayLiveData.value = dateFormatDash.format(calendar.time)
        getDayInfo(selectedDayLiveData.value!!, selectedDistributorID)
    }

    fun formUsersList() {
        visibleDistributors.clear()
        visibleDistributors.add(User.getBaseUser())
        visibleDistributors.addAll(
            ObjectCache.getInstance().getList(User::class, USERS_LIST_ID)
                ?.filter { it.isActive }
                ?.sortedBy { it.username }
                ?: listOf()
        )
    }

    private fun getDayInfo(date: String, distributorID: Int) {
        sendRequest(
            ApeniApiService.getInstance().getDayInfo(date, distributorID),
            successWithData = {
                val sales: ArrayList<SaleInfo> = ArrayList()
                sales.addAll(it.sale)
                expenses.clear()
                expenses.addAll(it.xarji)
                _expenseLiveData.value = expenses

                priceSum = sales.sumOf { obj -> obj.price }

                takeMoney.clear()
                takeMoney.addAll(it.takenMoney)

                val barrelIOList = it.barrels
                barrelIOList.forEach { bIO ->
                    bIO.barrelName = barrelsList.find { can -> can.id == bIO.canTypeID }?.name
                }
                _barrelsLiveData.value = barrelIOList
//                _realizationDayLiveData.value = it
                _salesLiveData.value = it.sale
            },
            finally = {
                Log.d(TAG, "dayReq $it")
            }
        )
    }

    fun getCashAmount(): Double {
        return takeMoney.find { it.paymentType == PaymentType.Cash }?.amount ?: .0
    }

    fun getTransferAmount(): Double {
        return takeMoney.find { it.paymentType == PaymentType.Transfer }?.amount ?: .0
    }

    fun deleteExpense(request: DeleteRequest) {
        sendRequest(
            apiRequest = ApeniApiService.getInstance().deleteRecord(request),
            success = {
                Log.d(TAG, "successfully Deleted")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    expenses.removeIf { x -> x.id == request.recordID }
                } else {
                    for (expense in expenses) {
                        if (expense.id == request.recordID) {
                            expenses.remove(expense)
                        }
                    }
                }
                _expenseLiveData.value = expenses
                _deleteExpenseLiveData.value = ApiResponseState.Success("")
            },
            finally = {
                Log.d(TAG, "successfully Deleted = $it")
            }
        )
    }

    fun deleteExpenseCompleted() {
        if (_deleteExpenseLiveData.value !is ApiResponseState.Sleep)
            _deleteExpenseLiveData.value = ApiResponseState.Sleep
    }

    fun setCurrentDate() {
        calendar = Calendar.getInstance()
        prepareData()
    }

    fun getDistributorNamesList(): List<String> = visibleDistributors
        .map { "${it.username} (${it.name})" }


    companion object {
        const val TAG = "Sales_VM"
    }
}
