package com.example.beerdistrkt.fragPages.reporting

import com.example.beerdistrkt.BaseViewModel
import com.example.beerdistrkt.fragPages.reporting.model.DbTableName
import com.example.beerdistrkt.fragPages.reporting.repo.ChangesRepository
import kotlinx.coroutines.flow.asStateFlow

class DetailedChangeHistoryViewModel: BaseViewModel() {

    private val changesRepository = ChangesRepository()

    val historyFlow = changesRepository.historyFlow.asStateFlow()

    fun getHistory(recordID: String, table: DbTableName) {
        changesRepository.getChangeHistory(recordID, table)
    }
}