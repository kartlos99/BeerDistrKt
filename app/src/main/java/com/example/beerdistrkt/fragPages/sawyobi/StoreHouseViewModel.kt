package com.example.beerdistrkt.fragPages.sawyobi

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.beerdistrkt.BaseViewModel
import com.example.beerdistrkt.fragPages.sawyobi.models.SimpleBeerRowModel
import com.example.beerdistrkt.fragPages.sawyobi.models.StoreHouseResponse
import com.example.beerdistrkt.models.BeerModel
import com.example.beerdistrkt.network.ApeniApiService
import com.example.beerdistrkt.storage.ObjectCache
import java.util.*

class StoreHouseViewModel : BaseViewModel() {

    var selectedDate = Calendar.getInstance()

    var isChecked = false
        set(value) {
            field = value
            getStoreBalance()
        }

    private val _setDayLiveData = MutableLiveData<String>()
    val setDayLiveData: LiveData<String>
        get() = _setDayLiveData

    private val _fullBarrelsListLiveData = MutableLiveData<List<SimpleBeerRowModel>>()
    val fullBarrelsListLiveData: LiveData<List<SimpleBeerRowModel>>
        get() = _fullBarrelsListLiveData

    private val _emptyBarrelsListLiveData = MutableLiveData<List<SimpleBeerRowModel>>()
    val emptyBarrelsListLiveData: LiveData<List<SimpleBeerRowModel>>
        get() = _emptyBarrelsListLiveData

    val beerList = ObjectCache.getInstance().getList(BeerModel::class, "beerList")
        ?: mutableListOf()


    init {
        getStoreBalance()
        _setDayLiveData.value = dateFormatDash.format(selectedDate.time)
    }

    private fun getStoreBalance() {
        sendRequest(
            ApeniApiService.getInstance().getStoreHouseBalance(
                dateFormatDash.format(selectedDate.time),
                if (isChecked) 1 else 0
            ),
            successWithData = {
                Log.d("store", it.empty.toString())
                formFullList(it.full)
                if (it.empty != null)
                    formEmptyList(it.empty, it.full)
            },
            finally = {
                Log.d("storeFinaly", it.toString())
            }
        )
    }

    private fun formEmptyList(
        empty: List<StoreHouseResponse.EmptyBarrelModel>,
        fList: List<StoreHouseResponse.FullBarrelModel>
    ) {
        val result = mutableListOf<SimpleBeerRowModel>()
        val valueOfDiff = mutableMapOf<Int, Int>()
        empty.forEach { ebm ->
            valueOfDiff[ebm.barrelID] = ebm.inputEmptyToStore - ebm.outputEmptyFromStoreCount
        }
        result.add(SimpleBeerRowModel("საწყობში:", valueOfDiff))

        val barrelsAtClient = mutableMapOf<Int, Int>()

        val fGr = fList.groupBy { it.barrelID }
        fGr.keys.forEach {
            val sumOfSale = fGr[it]?.sumBy { fItem -> fItem.saleCount} ?: 0
            val sumOfReceived = empty.find { e -> e.barrelID == it }?.inputEmptyToStore ?: 0
            barrelsAtClient[it] = sumOfSale - sumOfReceived
        }
        result.add(SimpleBeerRowModel("დასაბრუნებელი:", barrelsAtClient))

        _emptyBarrelsListLiveData.value = result
    }

    fun formFullList(fList: List<StoreHouseResponse.FullBarrelModel>) {
        val result = mutableListOf<SimpleBeerRowModel>()
        val a = fList.groupBy { it.beerID }
        a.values.forEach {
            val valueOfDiff = mutableMapOf<Int, Int>()
            it.forEach { fbm ->
                valueOfDiff[fbm.barrelID] = fbm.inputToStore - fbm.saleCount
            }
            val title = beerList.first { b -> b.id == it[0].beerID }.dasaxeleba ?: "_"
            result.add(SimpleBeerRowModel(title, valueOfDiff))
        }
        _fullBarrelsListLiveData.value = result
    }

    fun onDateSelected(year: Int, month: Int, day: Int) {
        selectedDate.set(year, month, day)
        _setDayLiveData.value = dateFormatDash.format(selectedDate.time)
        getStoreBalance()
    }
}
