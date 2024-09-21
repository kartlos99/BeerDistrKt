package com.example.beerdistrkt.fragPages.realisationtotal

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.beerdistrkt.BaseViewModel
import com.example.beerdistrkt.fragPages.expense.domain.usecase.GetExpenseCategoriesUseCase
import com.example.beerdistrkt.fragPages.realisationtotal.domain.model.RealizationDay
import com.example.beerdistrkt.fragPages.realisationtotal.domain.usecase.GetRealizationDayUseCase
import com.example.beerdistrkt.models.CanModel
import com.example.beerdistrkt.models.User
import com.example.beerdistrkt.network.model.ResultState
import com.example.beerdistrkt.network.model.onError
import com.example.beerdistrkt.network.model.onSuccess
import com.example.beerdistrkt.storage.ObjectCache
import com.example.beerdistrkt.storage.ObjectCache.Companion.BARREL_LIST_ID
import com.example.beerdistrkt.storage.ObjectCache.Companion.USERS_LIST_ID
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class SalesViewModel @Inject constructor(
    private val getExpenseCategoriesUseCase: GetExpenseCategoriesUseCase,
    private val getRealizationDayUseCase: GetRealizationDayUseCase,
) : BaseViewModel() {

    private val barrelsList = ObjectCache.getInstance().getList(CanModel::class, BARREL_LIST_ID)
        ?: listOf()
    val visibleDistributors = mutableListOf<User>()

    private val _selectedDayLiveData = MutableLiveData<String>()
    val selectedDayLiveData: LiveData<String>
        get() = _selectedDayLiveData

    private val _dayStateFlow: MutableStateFlow<ResultState<RealizationDay>> =
        MutableStateFlow(ResultState.Loading)
    val dayStateFlow = _dayStateFlow.asStateFlow()

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
        refreshCategories()
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

    private fun refreshCategories() = viewModelScope.launch {
        getExpenseCategoriesUseCase.refresh()
    }

    private fun getDayInfo(date: String, distributorID: Int) = viewModelScope.launch {
        _dayStateFlow.emit(ResultState.Loading)
        getRealizationDayUseCase.invoke(date, distributorID)
            .onSuccess { dayData ->
                dayData.barrels.forEach { bIO ->
                    bIO.barrelName = barrelsList.find { can -> can.id == bIO.canTypeID }?.name
                }
                _dayStateFlow.value = ResultState.Success(dayData)
            }.onError { error ->
                _dayStateFlow.value = error
            }
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
