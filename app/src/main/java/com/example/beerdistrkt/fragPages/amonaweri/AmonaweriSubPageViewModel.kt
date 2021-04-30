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

    val calendar = Calendar.getInstance()

    init {
        calendar.add(Calendar.HOUR, 24 + 4) // es dge rom bolomde chaitvalos
    }

    fun requestAmonaweriList() {
        amonaweriDataList.clear()
        when (pagePos) {
            M_PAGE -> getAmonaweriM(dateFormatDash.format(calendar.time))
            K_PAGE -> getAmonaweriK(dateFormatDash.format(calendar.time))
        }
    }

    private fun getAmonaweriM(date: String) {
        _amonaweriLiveData.value = ApiResponseState.Loading(true)
        sendRequest(
            ApeniApiService.getInstance().getAmonaweriM(date, clientID),
            successWithData = {
                amonaweriDataList.addAll(it)
                changeDataStructure(isGroupedLiveData.value)
            },
            finally = {
                _amonaweriLiveData.value = ApiResponseState.Loading(false)
            }
        )
    }

    private fun getAmonaweriK(date: String) {
        _amonaweriLiveData.value = ApiResponseState.Loading(true)
        sendRequest(
            ApeniApiService.getInstance().getAmonaweriK(date, clientID),
            successWithData = {
                amonaweriDataList.addAll(it)
                changeDataStructure(isGroupedLiveData.value)
            },
            finally = {
                _amonaweriLiveData.value = ApiResponseState.Loading(false)
            }
        )
    }

    fun changeDataStructure(grouped: Boolean?) {
        isGroupedLiveData.value = grouped
        if (grouped == true) {
            _amonaweriLiveData.value =
                ApiResponseState.Success(groupStatementList(amonaweriDataList))
        } else {
            _amonaweriLiveData.value = ApiResponseState.Success(amonaweriDataList)
        }
    }

    private fun groupStatementList(rowList: ArrayList<StatementModel>): ArrayList<StatementModel> {
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
            var k_in = 0.0f
            var k_out = 0.0f
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
