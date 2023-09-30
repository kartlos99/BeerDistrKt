package com.example.beerdistrkt.fragPages.reporting.repo

import com.example.beerdistrkt.fragPages.reporting.model.BaseTableRecord
import com.example.beerdistrkt.fragPages.reporting.model.ChangesShortDto
import com.example.beerdistrkt.fragPages.reporting.model.DbTableName
import com.example.beerdistrkt.network.ApeniApiService
import com.example.beerdistrkt.sendRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow

class ChangesRepository {

    private val TAG = "KD_Repo"
    private val ioScope = CoroutineScope(Dispatchers.IO)

    val changesListFlow = MutableStateFlow<List<ChangesShortDto>>(listOf())
    val historyFlow = MutableStateFlow<List<BaseTableRecord>>(listOf())

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
                historyFlow.tryEmit(list.mapNotNull {
                    mapper.map(it, table)
                })
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