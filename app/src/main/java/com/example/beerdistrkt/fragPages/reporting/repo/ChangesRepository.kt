package com.example.beerdistrkt.fragPages.reporting.repo

import com.example.beerdistrkt.fragPages.reporting.model.ChangesShortDto
import com.example.beerdistrkt.fragPages.reporting.model.DbTableName
import com.example.beerdistrkt.fragPages.reporting.model.HistoryUnitModel
import com.example.beerdistrkt.network.ApeniApiService
import com.example.beerdistrkt.sendRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class ChangesRepository {

    private val TAG = "KD_Repo"
    private val ioScope = CoroutineScope(Dispatchers.IO)
    private val workScope = CoroutineScope(Dispatchers.Default)

    val changesListFlow = MutableStateFlow<List<ChangesShortDto>>(listOf())
    val historyFlow = MutableStateFlow<List<HistoryUnitModel>>(listOf())

    fun getChangesList() {

        ApeniApiService.getInstance().getChangesList().sendRequest(
            successWithData = {
                changesListFlow.tryEmit(it)
            },
            failure = {
                changesListFlow.tryEmit(listOf())
            },
            onConnectionFailure = {
                changesListFlow.tryEmit(listOf())
            }
        )
    }

    fun getChangeHistory(recordID: String, table: DbTableName) {

        val mapper = HistoryItemMapper()

        ApeniApiService.getInstance().getRecordHistory(recordID, table.tableName).sendRequest(
            successWithData = { list ->
                workScope.launch {
                    historyFlow.tryEmit(mapper.map(list, table))
                }
            },
            failure = {
                historyFlow.tryEmit(listOf())
            },
            onConnectionFailure = {
                historyFlow.tryEmit(listOf())
            }
        )
    }
}