package com.example.beerdistrkt.fragPages.showHistory

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.beerdistrkt.BaseViewModel
import com.example.beerdistrkt.fragPages.beer.domain.model.Beer
import com.example.beerdistrkt.fragPages.beer.domain.usecase.GetBeerUseCase
import com.example.beerdistrkt.models.Obieqti
import com.example.beerdistrkt.models.User
import com.example.beerdistrkt.models.bottle.BaseBottleModel
import com.example.beerdistrkt.network.ApeniApiService
import com.example.beerdistrkt.storage.ObjectCache
import com.example.beerdistrkt.utils.ApiResponseState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SalesHistoryViewModel @Inject constructor(
    private val getBeerUseCase: GetBeerUseCase,
) : BaseViewModel() {
    private val userLiveData = database.getUsers()

    private var clients: List<Obieqti> = listOf()
    private lateinit var usersList: List<User>
    private lateinit var beerList: List<Beer>

    private val bottleList = ObjectCache.getInstance()
        .getList(BaseBottleModel::class, ObjectCache.BOTTLE_LIST_ID) ?: listOf()

    private val _saleHistoryLiveData = MutableLiveData<ApiResponseState<List<SaleHistory>>>()
    val saleHistoryLiveData: LiveData<ApiResponseState<List<SaleHistory>>>
        get() = _saleHistoryLiveData

    private val _bottleSaleHistoryLiveData = MutableLiveData<ApiResponseState<List<BottleSaleHistory>>>()
    val bottleSaleHistoryLiveData: LiveData<ApiResponseState<List<BottleSaleHistory>>>
        get() = _bottleSaleHistoryLiveData

    private val _moneyHistoryLiveData = MutableLiveData<ApiResponseState<List<MoneyHistory>>>()
    val moneyHistoryLiveData: LiveData<ApiResponseState<List<MoneyHistory>>>
        get() = _moneyHistoryLiveData

    init {
        userLiveData.observeForever { usersList = it }
        viewModelScope.launch {
            beerList = getBeerUseCase()
            database.getCustomers().collectLatest {
                clients = it
            }
        }
    }

    fun getData(saleID: Int) {
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

    fun requestBottleSaleHistory(saleID: Int) {
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

    fun getMoneyData(recordID: Int) {
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
}