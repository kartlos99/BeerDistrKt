package com.example.beerdistrkt.fragPages.statement.presentation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.beerdistrkt.BaseViewModel
import com.example.beerdistrkt.fragPages.statement.domain.model.FinanceStatementItem
import com.example.beerdistrkt.fragPages.statement.domain.model.StatementRecordType
import com.example.beerdistrkt.fragPages.statement.domain.usecase.GetFinanceStatementUseCase
import com.example.beerdistrkt.fragPages.statement.model.StatementModel
import com.example.beerdistrkt.fragPages.statement.presentation.mapper.FinanceStatementUiMapper
import com.example.beerdistrkt.fragPages.statement.presentation.model.FinanceStatementUiModel
import com.example.beerdistrkt.network.api.ApiResponse
import com.example.beerdistrkt.network.model.ResultState
import com.example.beerdistrkt.network.model.asSuccessState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.text.ParseException
import java.util.Date

@HiltViewModel(assistedFactory = FinanceStatementViewModel.Factory::class)
class FinanceStatementViewModel @AssistedInject constructor(
    private val getFinanceStatementUseCase: GetFinanceStatementUseCase,
    private val financeStatementUiMapper: FinanceStatementUiMapper,
    @Assisted val clientID: Int,
) : BaseViewModel() {

    private val _statementLiveData =
        MutableLiveData<ResultState<List<FinanceStatementUiModel>>>()
    val statementLiveData: LiveData<ResultState<List<FinanceStatementUiModel>>>
        get() = _statementLiveData

    var isGroupedLiveData = MutableLiveData(true)

    private val statement = mutableListOf<FinanceStatementItem>()
    private val statementUiItems = mutableListOf<FinanceStatementUiModel>()

    val needUpdateLiveData = MutableLiveData<String?>(null)
    private var totalCount = 1L

    val isLastPage
        get() = statement.size >= totalCount

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
            when (val result = getFinanceStatementUseCase(clientID, statement.size)) {

                is ApiResponse.Error -> {
                    Log.d(TAG, "getFinanceStatement: ${result.message}")
                    _statementLiveData.value = ResultState.Error(result.statusCode)
                }

                is ApiResponse.Success -> {
                    totalCount = result.data.totalCount
                    statement.addAll(result.data.statements)
                    statementUiItems.addAll(
                        result.data.statements.map(financeStatementUiMapper::map)
                    )
//                    proceedData(statement)
                    _statementLiveData.value = statementUiItems.toList().asSuccessState()
                }
            }
        }
    }

    private fun proceedData(statementItems: List<FinanceStatementItem>) {
        _statementLiveData.value = ResultState.Success(
            statementItems.map(financeStatementUiMapper::map)

//            if (isGroupedLiveData.value == true)
//                groupStatementList(newPart)
//            else
//                newPart
        )
    }

    fun changeDataStructure(grouped: Boolean) {
        isGroupedLiveData.value = grouped
        proceedData(statement)
    }

    private fun groupStatementList(rowList: List<StatementModel>): ArrayList<StatementModel> {
        val groupedList = ArrayList<StatementModel>()
        var grDate = Date()
        var currRowDate = Date()

        if (rowList.isNotEmpty()) {
            try {
                grDate = dateFormatDash.parse(rowList[0].tarigi) ?: Date()
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            var pr = 0.0f
            var payed = 0.0f
            var bal: Float = rowList[0].balance
            var kIn = 0
            var kOut = 0
            var grGift = if (rowList[0].isGift) 1 else 0
            val totalComment = mutableListOf<String?>()
            totalComment.add(rowList[0].comment)

            for (i in rowList.indices) {
                try {
                    currRowDate = dateFormatDash.parse(rowList[i].tarigi) ?: Date()
                } catch (e: ParseException) {
                    e.printStackTrace()
                }
                if (currRowDate == grDate) {
                    pr += rowList[i].price
                    payed += rowList[i].pay
                    kIn += rowList[i].k_in
                    kOut += rowList[i].k_out
                    totalComment.add(rowList[i].comment)
                    if (rowList[i].isGift) grGift++
                } else {
                    val currGrRow = StatementModel().apply {
                        tarigi = dateFormatDash.format(grDate)
                        price = pr
                        pay = payed
                        balance = bal
                        k_in = kIn
                        k_out = kOut
                        comment = totalComment
                            .filter { !it.isNullOrEmpty() }
                            .distinct()
                            .joinToString(COMMENT_SEPARATOR)
                        groupGift = grGift > 0
                    }

                    groupedList.add(currGrRow)

                    grDate = currRowDate
                    pr = rowList[i].price
                    payed = rowList[i].pay
                    bal = rowList[i].balance
                    kIn = rowList[i].k_in
                    kOut = rowList[i].k_out
                    grGift = 0
                    if (rowList[i].isGift) grGift++

                    totalComment.removeAll { true }
                    if (!rowList[i].comment.isNullOrEmpty())
                        totalComment.add(rowList[i].comment)
                }
            }
            val currGrRow = StatementModel().apply {
                tarigi = dateFormatDash.format(grDate)
                price = pr
                pay = payed
                balance = bal
                k_in = kIn
                k_out = kOut
                comment = totalComment
                    .filter { !it.isNullOrEmpty() }
                    .distinct()
                    .joinToString(COMMENT_SEPARATOR)
                groupGift = grGift > 0
            }
            groupedList.add(currGrRow)
        }
        return groupedList
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
