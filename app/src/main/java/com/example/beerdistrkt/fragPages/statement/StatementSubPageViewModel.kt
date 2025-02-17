package com.example.beerdistrkt.fragPages.statement

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.beerdistrkt.BaseViewModel
import com.example.beerdistrkt.fragPages.statement.model.StatementModel
import com.example.beerdistrkt.models.DeleteRequest
import com.example.beerdistrkt.network.ApeniApiService
import com.example.beerdistrkt.utils.ApiResponseState
import com.example.beerdistrkt.utils.K_PAGE
import com.example.beerdistrkt.utils.M_PAGE
import com.example.beerdistrkt.utils.Session
import dagger.hilt.android.lifecycle.HiltViewModel
import java.text.ParseException
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class StatementSubPageViewModel @Inject constructor() : BaseViewModel() {

    private val _statementLiveData = MutableLiveData<ApiResponseState<List<StatementModel>>>()
    val statementLiveData: LiveData<ApiResponseState<List<StatementModel>>>
        get() = _statementLiveData


    private var statementDataList = ArrayList<StatementModel>()
    var isGroupedLiveData = MutableLiveData(true)
    var clientID = 0
    var pagePos = 0
    val needUpdateLiveData = MutableLiveData<String?>(null)
    private var totalCount = 1

    val isLastPage
        get() = statementDataList.size >= totalCount

    fun requestStatementList() {
        statementDataList.clear()
        loadMoreData()
    }

    fun loadMoreData() {
        if (statementDataList.size < totalCount)
            when (pagePos) {
                M_PAGE -> getMoneyStatement()
                K_PAGE -> getBarrelStatement()
            }
    }

    private fun getMoneyStatement() {
        _statementLiveData.value = ApiResponseState.Loading(true)
        loadingCounter++
        sendRequest(
            ApeniApiService.getInstance().getFinancialStatement(statementDataList.size, clientID),
            successWithData = {
                totalCount = it.totalCount
                statementDataList.addAll(it.list)
                proceedData(it.list)
            },
            finally = {
                loadingCounter--
                _statementLiveData.value = ApiResponseState.Loading(false)
            }
        )
    }

    private fun getBarrelStatement() {
        _statementLiveData.value = ApiResponseState.Loading(true)
        loadingCounter++
        sendRequest(
            ApeniApiService.getInstance().getBarrelStatement(statementDataList.size, clientID),
            successWithData = {
                totalCount = it.totalCount
                statementDataList.addAll(it.list)
                proceedData(it.list)
            },
            finally = {
                loadingCounter--
                _statementLiveData.value = ApiResponseState.Loading(false)
            }
        )
    }

    private fun proceedData(newPart: List<StatementModel>) {
        _statementLiveData.value = ApiResponseState.Success(
            if (isGroupedLiveData.value == true)
                groupStatementList(newPart)
            else
                newPart
        )
    }

    fun changeDataStructure(grouped: Boolean) {
        isGroupedLiveData.value = grouped
        proceedData(statementDataList)
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
                    }

                    groupedList.add(currGrRow)

                    grDate = currRowDate
                    pr = rowList[i].price
                    payed = rowList[i].pay
                    bal = rowList[i].balance
                    kIn = rowList[i].k_in
                    kOut = rowList[i].k_out
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
            }
            groupedList.add(currGrRow)
        }
        return groupedList
    }

    fun deleteRecord(table: String, id: Int) {
        sendRequest(
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
        )
    }

    companion object {
        private const val COMMENT_SEPARATOR = " | "
    }
}
