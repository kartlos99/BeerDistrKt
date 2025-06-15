package com.example.beerdistrkt.fragPages.realisationtotal

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.beerdistrkt.BaseViewModel
import com.example.beerdistrkt.common.model.Barrel
import com.example.beerdistrkt.fragPages.expense.domain.usecase.GetExpenseCategoriesUseCase
import com.example.beerdistrkt.fragPages.homePage.domain.usecase.GetBarrelsUseCase
import com.example.beerdistrkt.fragPages.login.domain.model.Permission
import com.example.beerdistrkt.fragPages.realisationtotal.domain.model.RealizationDay
import com.example.beerdistrkt.fragPages.realisationtotal.domain.usecase.GetRealizationDayUseCase
import com.example.beerdistrkt.fragPages.realisationtotal.presentation.model.SpinnerStateModel
import com.example.beerdistrkt.fragPages.realisationtotal.presentation.model.UserSpinner
import com.example.beerdistrkt.fragPages.user.domain.usecase.GetUsersUseCase
import com.example.beerdistrkt.network.model.ResultState
import com.example.beerdistrkt.network.model.onError
import com.example.beerdistrkt.network.model.onSuccess
import com.example.beerdistrkt.utils.Session
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class SalesViewModel @Inject constructor(
    private val getExpenseCategoriesUseCase: GetExpenseCategoriesUseCase,
    private val getRealizationDayUseCase: GetRealizationDayUseCase,
    private val getBarrelsUseCase: GetBarrelsUseCase,
    private val getUsersUseCase: GetUsersUseCase,
    override var session: Session,
) : BaseViewModel() {

    private var barrelsList: List<Barrel> = emptyList()

    private val _selectedDayLiveData = MutableLiveData<String>()
    val selectedDayLiveData: LiveData<String>
        get() = _selectedDayLiveData

    private val _dayStateFlow: MutableStateFlow<ResultState<RealizationDay>> =
        MutableStateFlow(ResultState.Loading)
    val dayStateFlow = _dayStateFlow.asStateFlow()

    private val _distributorSelectorState = MutableStateFlow(SpinnerStateModel())
    val distributorSelectorState = _distributorSelectorState.asStateFlow()

    private var selectedDistributorID = 0

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

    private fun prepareData() {
        _selectedDayLiveData.value = dateFormatDash.format(calendar.time)
        Log.d(TAG, selectedDayLiveData.value!!)
        getDayInfo(selectedDayLiveData.value!!, selectedDistributorID)
    }

    init {
        _selectedDayLiveData.value = dateFormatDash.format(calendar.time)
        viewModelScope.launch {
            refreshCategories()
            barrelsList = getBarrelsUseCase()
            initDistributorsList()
        }
    }

    private suspend fun initDistributorsList() {
        val distributors = getUsersUseCase(session.regionId)
            .filter { it.isActive }
            .sortedBy { it.username }
            .map { UserSpinner(it.id, "${it.username} (${it.name})") }
            .toMutableList()
        distributors.add(0, UserSpinner.baseItem)

        val shouldBlockDistributors = !session.hasPermission(Permission.SeeOthersRealization)
        val position: Int = if (shouldBlockDistributors) {
            val currentUserIndexInSpinner = distributors.map { it.id }.indexOf(session.userID)
            if (currentUserIndexInSpinner >= 0) {
                selectedDistributorID = currentUserIndexInSpinner
                currentUserIndexInSpinner
            } else {
                forceLogout()
                0
            }
        } else 0

        val spinnerModel = SpinnerStateModel(
            items = distributors,
            selectedPosition = position,
            isBlocked = shouldBlockDistributors
        )
        _distributorSelectorState.emit(spinnerModel)
    }

    private suspend fun refreshCategories() {
        getExpenseCategoriesUseCase.refresh()
    }

    private fun getDayInfo(date: String, distributorID: Int) = viewModelScope.launch {
        _dayStateFlow.emit(ResultState.Loading)
        getRealizationDayUseCase.invoke(date, distributorID)
            .onSuccess { dayData ->
                dayData.barrels.forEach { bIO ->
                    bIO.barrelName = barrelsList.find { it.id == bIO.canTypeID }?.name
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

    fun onDistributorSelected(position: Int) {
        selectedDistributorID = _distributorSelectorState.value.items[position].id.toInt()
        prepareData()
    }

    fun saveDistributorSpinnerPosition(position: Int) {
        _distributorSelectorState.update {
            it.copy(selectedPosition = position)
        }
    }


    companion object {
        const val TAG = "Sales_VM"
    }
}
