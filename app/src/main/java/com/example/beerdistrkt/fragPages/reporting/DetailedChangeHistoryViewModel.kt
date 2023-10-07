package com.example.beerdistrkt.fragPages.reporting

import androidx.lifecycle.LiveData
import com.example.beerdistrkt.BaseViewModel
import com.example.beerdistrkt.fragPages.reporting.model.DbTableName
import com.example.beerdistrkt.fragPages.reporting.model.HistoryUnitModel
import com.example.beerdistrkt.fragPages.reporting.repo.ChangesRepository
import com.example.beerdistrkt.utils.ApiResponseState

class DetailedChangeHistoryViewModel : BaseViewModel() {

    private val changesRepository = ChangesRepository()

    val historyLiveData: LiveData<ApiResponseState<List<HistoryUnitModel>>> =
        changesRepository.historyLiveData

    fun getHistory(recordID: String, table: DbTableName) {
        changesRepository.getChangeHistory(recordID, table)
    }
}