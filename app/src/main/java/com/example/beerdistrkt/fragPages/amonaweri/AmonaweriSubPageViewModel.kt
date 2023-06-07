package com.example.beerdistrkt.fragPages.amonaweri

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.beerdistrkt.BaseViewModel
import com.example.beerdistrkt.models.StatementModel
import com.example.beerdistrkt.models.DeleteRequest
import com.example.beerdistrkt.network.ApeniApiService
import com.example.beerdistrkt.utils.ApiResponseState
import com.example.beerdistrkt.utils.K_PAGE
import com.example.beerdistrkt.utils.M_PAGE
import com.example.beerdistrkt.utils.Session
import java.text.ParseException
import java.util.*
import kotlin.collections.ArrayList

class AmonaweriSubPageViewModel : BaseViewModel() {

    private val _amonaweriLiveData = MutableLiveData<ApiResponseState<List<StatementModel>>>()
    val amonaweriLiveData: LiveData<ApiResponseState<List<StatementModel>>>
        get() = _amonaweriLiveData

    val TAG = "subPageVM"
    var amonaweriDataList = ArrayList<StatementModel>()
    var isGroupedLiveData = MutableLiveData<Boolean>(true)
    var clientID = 0
    var pagePos = 0
    val needUpdateLiveData = MutableLiveData<String?>(null)
    private var totalCount = 1

    val isLastPage
        get() = amonaweriDataList.size >= totalCount

    fun requestAmonaweriList() {
        amonaweriDataList.clear()
        loadMoreData()
    }

    fun loadMoreData() {
        if (amonaweriDataList.size < totalCount)
            when (pagePos) {
                M_PAGE -> getAmonaweriM()
                K_PAGE -> getAmonaweriK()
            }
    }

    private fun getAmonaweriM() {
        _amonaweriLiveData.value = ApiResponseState.Loading(true)
        loadingCounter++
        sendRequest(
            ApeniApiService.getInstance().getFinancialStatement(amonaweriDataList.size, clientID),
            successWithData = {
                totalCount = it.totalCount
                amonaweriDataList.addAll(it.list)
                proceedData(it.list)
            },
            finally = {
                loadingCounter--
                _amonaweriLiveData.value = ApiResponseState.Loading(false)
            }
        )
    }

    private fun getAmonaweriK() {
        _amonaweriLiveData.value = ApiResponseState.Loading(true)
        loadingCounter++
        sendRequest(
            ApeniApiService.getInstance().getBarrelStatement(amonaweriDataList.size, clientID),
            successWithData = {
                totalCount = it.totalCount
                amonaweriDataList.addAll(it.list)
                proceedData(it.list)
            },
            finally = {
                loadingCounter--
                _amonaweriLiveData.value = ApiResponseState.Loading(false)
            }
        )
    }

    private fun proceedData(newPart: List<StatementModel>) {
        _amonaweriLiveData.value = ApiResponseState.Success(
            if (isGroupedLiveData.value == true)
                groupStatementList(newPart)
            else
                newPart
        )
    }

    fun changeDataStructure(grouped: Boolean) {
        isGroupedLiveData.value = grouped
        proceedData(amonaweriDataList)
    }

    private fun groupStatementList(rowList: List<StatementModel>): ArrayList<StatementModel> {
        val groupedList = ArrayList<StatementModel>()
        var grDate = Date()
        var currRowDate = Date()

        if (rowList.size > 0) {
            try {
                grDate = dateFormatDash.parse(rowList[0].tarigi) ?: Date()
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            var pr = 0.0f
            var pay = 0.0f
            var bal: Float = rowList[0].balance
            var k_in = 0
            var k_out = 0
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
                    pay += rowList[i].pay
                    k_in += rowList[i].k_in
                    k_out += rowList[i].k_out
                    totalComment.add(rowList[i].comment)
                } else {
                    val currGrRow = StatementModel()
                    currGrRow.tarigi = dateFormatDash.format(grDate)
                    currGrRow.price = pr
                    currGrRow.pay = pay
                    currGrRow.balance = bal
                    currGrRow.k_in = k_in
                    currGrRow.k_out = k_out
                    currGrRow.comment =
                        totalComment.filter { !it.isNullOrEmpty() }.distinct().joinToString(" | ")
                    groupedList.add(currGrRow)

                    grDate = currRowDate
                    pr = rowList[i].price
                    pay = rowList[i].pay
                    bal = rowList[i].balance
                    k_in = rowList[i].k_in
                    k_out = rowList[i].k_out
                    totalComment.removeAll { true }
                    if (!rowList[i].comment.isNullOrEmpty())
                        totalComment.add(rowList[i].comment)
                }
            }
            val currGrRow = StatementModel()
            currGrRow.tarigi = dateFormatDash.format(grDate)
            currGrRow.price = pr
            currGrRow.pay = pay
            currGrRow.balance = bal
            currGrRow.k_in = k_in
            currGrRow.k_out = k_out
            currGrRow.comment =
                totalComment.filter { !it.isNullOrEmpty() }.distinct().joinToString(" | ")
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
                    Session.get().userID ?: return
                )
            ),
            success = {
                requestAmonaweriList()
                needUpdateLiveData.value = pagePos.toString()
            }
        )
    }
}
