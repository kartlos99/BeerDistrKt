package com.example.beerdistrkt.fragPages.showHistory

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.beerdistrkt.BaseViewModel
import com.example.beerdistrkt.fragPages.beer.domain.model.Beer
import com.example.beerdistrkt.fragPages.beer.domain.usecase.GetBeerUseCase
import com.example.beerdistrkt.fragPages.bottlemanagement.domain.usecase.GetBottlesUseCase
import com.example.beerdistrkt.fragPages.customer.domain.model.Customer
import com.example.beerdistrkt.fragPages.customer.domain.usecase.GetCustomersUseCase
import com.example.beerdistrkt.fragPages.showHistory.SalesHistoryFragment.Companion.BARREL_DELIVERY
import com.example.beerdistrkt.fragPages.showHistory.SalesHistoryFragment.Companion.BOTTLE_DELIVERY
import com.example.beerdistrkt.fragPages.showHistory.SalesHistoryFragment.Companion.MONEY
import com.example.beerdistrkt.fragPages.user.domain.model.User
import com.example.beerdistrkt.fragPages.user.domain.usecase.GetUsersUseCase
import com.example.beerdistrkt.models.bottle.BaseBottleModel
import com.example.beerdistrkt.network.ApeniApiService
import com.example.beerdistrkt.utils.ApiResponseState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = SalesHistoryViewModel.Factory::class)
class SalesHistoryViewModel @AssistedInject constructor(
    private val getBeerUseCase: GetBeerUseCase,
    private val getBottlesUseCase: GetBottlesUseCase,
    private val getCustomersUseCase: GetCustomersUseCase,
    private val getUsersUseCase: GetUsersUseCase,
    @Assisted private val recordID: Int,
    @Assisted val historyOf: String,
) : BaseViewModel() {

    private lateinit var clients: List<Customer>
    private lateinit var usersList: List<User>
    private lateinit var beerList: List<Beer>
    private lateinit var bottleList: List<BaseBottleModel>

    private val _saleHistoryLiveData = MutableLiveData<ApiResponseState<List<SaleHistory>>>()
    val saleHistoryLiveData: LiveData<ApiResponseState<List<SaleHistory>>>
        get() = _saleHistoryLiveData

    private val _bottleSaleHistoryLiveData =
        MutableLiveData<ApiResponseState<List<BottleSaleHistory>>>()
    val bottleSaleHistoryLiveData: LiveData<ApiResponseState<List<BottleSaleHistory>>>
        get() = _bottleSaleHistoryLiveData

    private val _moneyHistoryLiveData = MutableLiveData<ApiResponseState<List<MoneyHistory>>>()
    val moneyHistoryLiveData: LiveData<ApiResponseState<List<MoneyHistory>>>
        get() = _moneyHistoryLiveData

    init {
        viewModelScope.launch {
            beerList = getBeerUseCase()
            bottleList = getBottlesUseCase()
            clients = getCustomersUseCase()
            usersList = getUsersUseCase()
            switchHistory()
        }
    }

    private fun switchHistory() = when (historyOf) {
        BARREL_DELIVERY -> getData(recordID)
        BOTTLE_DELIVERY -> requestBottleSaleHistory(recordID)
        MONEY -> getMoneyData(recordID)
        else -> _saleHistoryLiveData.value = ApiResponseState.ApiError(1, "unknown history object")
    }

    private fun getData(saleID: Int) {
        sendRequest(
            ApeniApiService.getInstance().getSalesHistory(saleID),
            successWithData = { historyData ->
                val pmModel = historyData
                    .mapNotNull {
                        it.toPm(
                            clients,
                            usersList,
                            beerList
                        )
                    }
                _saleHistoryLiveData.value = ApiResponseState.Success(pmModel)
            }
        )
    }

    private fun requestBottleSaleHistory(saleID: Int) {
        sendRequest(
            ApeniApiService.getInstance().getBottleSalesHistory(saleID),
            successWithData = { historyData ->
                val pmModel = historyData
                    .mapNotNull {
                        it.toPm(
                            clients,
                            usersList,
                            bottleList
                        )
                    }
                _bottleSaleHistoryLiveData.value = ApiResponseState.Success(pmModel)
            }
        )
    }

    private fun getMoneyData(recordID: Int) {
        sendRequest(
            ApeniApiService.getInstance().getMoneyHistory(recordID),
            successWithData = { historyData ->
                val pmModel = historyData
                    .mapNotNull {
                        it.toPm(
                            clients,
                            usersList
                        )
                    }

                _moneyHistoryLiveData.value = ApiResponseState.Success(pmModel)
            }
        )
    }

    @AssistedFactory
    interface Factory {
        fun create(
            recordID: Int,
            historyOf: String
        ): SalesHistoryViewModel
    }
}