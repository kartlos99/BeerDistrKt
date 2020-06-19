package com.example.beerdistrkt.fragPages.amonaweri

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.beerdistrkt.BaseViewModel
import com.example.beerdistrkt.models.Amonaweri
import com.example.beerdistrkt.network.ApeniApiService
import com.example.beerdistrkt.utils.K_PAGE
import com.example.beerdistrkt.utils.M_PAGE
import java.text.ParseException
import java.util.*
import kotlin.collections.ArrayList

class AmonaweriSubPageViewModel : BaseViewModel() {

    private val _amonaweriLiveData = MutableLiveData<List<Amonaweri>>()
    val amonaweriLiveData: LiveData<List<Amonaweri>>
        get() = _amonaweriLiveData

    val TAG = "subPageVM"
    var amonaweriDataList = ArrayList<Amonaweri>()
    var isGrouped = true

    fun requestAmonaweriList(date: String, objID: Int, pagePos: Int){
        when(pagePos){
            M_PAGE -> getAmonaweriM(date, objID)
            K_PAGE -> getAmonaweriK(date, objID)
        }
    }

    fun getAmonaweriM(date: String, id: Int) {
        sendRequest(
            ApeniApiService.getInstance().getAmonaweriM(date, id),
            successWithData = {
                amonaweriDataList.addAll(it)
                changeDataStructure(isGrouped)
            }
        )
    }

    fun getAmonaweriK(date: String, id: Int) {
        sendRequest(
            ApeniApiService.getInstance().getAmonaweriK(date, id),
            successWithData = {
                amonaweriDataList.addAll(it)
                changeDataStructure(isGrouped)
            }
        )
    }

    fun changeDataStructure(grouped: Boolean) {
        if (grouped){
            _amonaweriLiveData.value = groupAmonaweriList(amonaweriDataList)
        }else{
            _amonaweriLiveData.value = amonaweriDataList
        }
        isGrouped = grouped
    }

    fun groupAmonaweriList(rowList: ArrayList<Amonaweri>): ArrayList<Amonaweri>{
        val groupedList = ArrayList<Amonaweri>()
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
                } else {
                    val currGrRow = Amonaweri()
                    currGrRow.tarigi = dateFormatDash.format(grDate)
                    currGrRow.price = pr
                    currGrRow.pay = pay
                    currGrRow.balance = bal
                    currGrRow.k_in = k_in
                    currGrRow.k_out = k_out
                    groupedList.add(currGrRow)

                    grDate = currRowDate
                    pr = rowList[i].price
                    pay = rowList[i].pay
                    bal = rowList[i].balance
                    k_in = rowList[i].k_in
                    k_out = rowList[i].k_out
                }
            }
            val currGrRow = Amonaweri()
            currGrRow.tarigi = dateFormatDash.format(grDate)
            currGrRow.price = pr
            currGrRow.pay = pay
            currGrRow.balance = bal
            currGrRow.k_in = k_in
            currGrRow.k_out = k_out
            groupedList.add(currGrRow)
        }
        return groupedList
    }
}
