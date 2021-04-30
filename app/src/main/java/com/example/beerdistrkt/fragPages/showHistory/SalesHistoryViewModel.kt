package com.example.beerdistrkt.fragPages.showHistory

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.beerdistrkt.BaseViewModel
import com.example.beerdistrkt.models.BeerModel
import com.example.beerdistrkt.models.Obieqti
import com.example.beerdistrkt.models.User
import com.example.beerdistrkt.network.ApeniApiService
import com.example.beerdistrkt.storage.ObjectCache
import com.example.beerdistrkt.storage.ObjectCache.Companion.CLIENTS_LIST_ID
import com.example.beerdistrkt.utils.ApiResponseState

class SalesHistoryViewModel : BaseViewModel() {
    private val userLiveData = database.getUsers()
    private val beerLiveData = database.getBeerList()

    var clients: List<Obieqti> = ObjectCache.getInstance().getList(Obieqti::class, CLIENTS_LIST_ID) ?: listOf()
    private lateinit var usersList: List<User>
    private lateinit var beerList: List<BeerModel>

    private val _saleHistoryLiveData = MutableLiveData<ApiResponseState<List<SaleHistory>>>()
    val saleHistoryLiveData: LiveData<ApiResponseState<List<SaleHistory>>>
        get() = _saleHistoryLiveData

    private val _moneyHistoryLiveData = MutableLiveData<ApiResponseState<List<MoneyHistory>>>()
    val moneyHistoryLiveData: LiveData<ApiResponseState<List<MoneyHistory>>>
        get() = _moneyHistoryLiveData

    init {
        beerLiveData.observeForever { beerList = it }
        userLiveData.observeForever { usersList = it }
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