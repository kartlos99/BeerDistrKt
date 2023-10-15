package com.example.beerdistrkt.fragPages.reporting.repo

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.beerdistrkt.fragPages.reporting.model.ChangesShortDto
import com.example.beerdistrkt.fragPages.reporting.model.DbTableName
import com.example.beerdistrkt.fragPages.reporting.model.HistoryUnitModel
import com.example.beerdistrkt.network.ApeniApiService
import com.example.beerdistrkt.sendRequest
import com.example.beerdistrkt.utils.ApiResponseState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChangesRepository {

    private val TAG = "KD_Repo"

    val changesListLiveData = MutableLiveData<ApiResponseState<List<ChangesShortDto>>>()
    val historyLiveData = MutableLiveData<ApiResponseState<List<HistoryUnitModel>>>()

    fun getChangesList() {
        Log.d(TAG, "getChangesList: start")
        changesListLiveData.value = ApiResponseState.Loading(true)
        ApeniApiService.getInstance().getChangesList().sendRequest(
            successWithData = {
                Log.d(TAG, "getChangesList: Suss")
                changesListLiveData.value = ApiResponseState.Success(it)
            },
            failure = {
                changesListLiveData.value =
                    ApiResponseState.ApiError(it.hashCode(), it.message ?: "")
            },
            onConnectionFailure = {
                changesListLiveData.value =
                    (ApiResponseState.ApiError(it.hashCode(), it.message ?: ""))
            },
            finally = {
                Log.d(TAG, "getChangesList: finaly")
                changesListLiveData.value = (ApiResponseState.Loading(false))
            })
    }

    fun getChangeHistory(recordID: String, table: DbTableName) {

        val mapper = HistoryItemMapper()
        historyLiveData.value = ApiResponseState.Loading(true)
        ApeniApiService.getInstance().getRecordHistory(recordID, table.tableName).sendRequest(
            successWithData = { historyDto ->
                historyLiveData.value = ApiResponseState.Success(mapper.map(historyDto, table))
            },
            onConnectionFailure = {
                historyLiveData.value = (ApiResponseState.ApiError(it.hashCode(), it.message ?: ""))
            },
            failure = {
                historyLiveData.value = (ApiResponseState.ApiError(it.hashCode(), it.message ?: ""))
            },
            finally = {
                historyLiveData.value = ApiResponseState.Loading(false)
            },
        )
    }
}