package com.example.beerdistrkt.fragPages.reporting

import androidx.lifecycle.MutableLiveData
import com.example.beerdistrkt.BaseViewModel
import com.example.beerdistrkt.fragPages.reporting.model.DbTableName
import com.example.beerdistrkt.fragPages.reporting.model.HistoryUnitModel
import com.example.beerdistrkt.fragPages.reporting.repo.ChangesRepository
import com.example.beerdistrkt.utils.ApiResponseState
import kotlinx.coroutines.flow.MutableStateFlow

class DetailedChangeHistoryViewModel : BaseViewModel() {

    private val changesRepository = ChangesRepository()

    val historyLiveData: MutableLiveData<ApiResponseState<List<HistoryUnitModel>>> =
        changesRepository.historyLiveData

    val fullScreenState: MutableStateFlow<Boolean> = MutableStateFlow(false)

    fun getHistory(recordID: String, table: DbTableName) {
        changesRepository.getChangeHistory(recordID, table)
    }

    fun toggleFullScreen() {
        fullScreenState.value = fullScreenState.value.not()
    }
}