package com.example.beerdistrkt.fragPages.statement.presentation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.beerdistrkt.BaseViewModel
import com.example.beerdistrkt.empty
import com.example.beerdistrkt.fragPages.statement.domain.model.FStatement
import com.example.beerdistrkt.fragPages.statement.domain.model.StatementRecordType
import com.example.beerdistrkt.fragPages.statement.domain.usecase.GetFinanceStatementUseCase
import com.example.beerdistrkt.fragPages.statement.presentation.mapper.FinanceStatementUiMapper
import com.example.beerdistrkt.fragPages.statement.presentation.model.FStatementUiItem
import com.example.beerdistrkt.network.api.ApiResponse
import com.example.beerdistrkt.network.model.ResultState
import com.example.beerdistrkt.network.model.asSuccessState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = FinanceStatementViewModel.Factory::class)
class FinanceStatementViewModel @AssistedInject constructor(
    private val getFinanceStatementUseCase: GetFinanceStatementUseCase,
    private val financeStatementUiMapper: FinanceStatementUiMapper,
    @Assisted val clientID: Int,
) : BaseViewModel() {

    private val _statementLiveData =
        MutableLiveData<ResultState<List<FStatementUiItem>>>()
    val statementLiveData: LiveData<ResultState<List<FStatementUiItem>>>
        get() = _statementLiveData

    var isGroupedLiveData = MutableLiveData(true)

    private val statement = mutableListOf<FStatement>()
    private val statementUiItems = mutableListOf<FStatementUiItem>()

    val needUpdateLiveData = MutableLiveData<String?>(null)
    private var totalCount = 1L
    private var oldestTime: String? = null
    private var firstOperationDate: String = String.empty()

    val isLastPage
        get() = firstOperationDate == oldestTime

    val isDataLoading
        get() = _statementLiveData.value is ResultState.Loading

    init {
        requestStatementList()
    }

    fun requestStatementList() {
        statement.clear()
        loadMoreData()
    }

    fun loadMoreData() {
        if (statement.size < totalCount)
            getFinanceStatement()
    }

    private fun getFinanceStatement() {
        viewModelScope.launch {
            _statementLiveData.value = ResultState.Loading
            when (val result = getFinanceStatementUseCase(clientID, oldestTime)) {

                is ApiResponse.Error -> {
                    Log.d(TAG, "getFinanceStatement: ${result.message}")
                    _statementLiveData.value = ResultState.Error(result.statusCode)
                }

                is ApiResponse.Success -> {
                    totalCount = result.data.totalCount
                    oldestTime = result.data.statements.lastOrNull()?.dateStr
                    firstOperationDate = result.data.firstOperationDate
                    statement.addAll(result.data.statements)
                    statementUiItems.addAll(
                        result.data.statements.map(financeStatementUiMapper::map)
                    )
                    _statementLiveData.value = statementUiItems.toList().asSuccessState()
                }
            }
        }
    }

    fun deleteRecord(recType: StatementRecordType, recordId: Long) {
        return
        /*sendRequest(
            ApeniApiService.getInstance().deleteRecord(
                DeleteRequest(
                    id.toString(),
                    table,
                    session.userID ?: return
                )
            ),
            success = {
                requestStatementList()
                needUpdateLiveData.value = pagePos.toString()
            }
        )*/
    }

    @AssistedFactory
    interface Factory {
        fun create(clientID: Int): FinanceStatementViewModel
    }

    companion object {
        private const val COMMENT_SEPARATOR = " | "
    }
}
