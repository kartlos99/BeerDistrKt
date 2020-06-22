package com.example.beerdistrkt.fragPages.mitana

import android.text.Editable
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.beerdistrkt.BaseViewModel
import com.example.beerdistrkt.fragPages.sales.models.SaleRequestModel
import com.example.beerdistrkt.models.*
import com.example.beerdistrkt.network.ApeniApiService
import com.example.beerdistrkt.storage.ObjectCache
import com.example.beerdistrkt.utils.Session
import kotlinx.coroutines.launch
import java.util.*

class AddDeliveryViewModel(
    private val clientID: Int,
    private val orderID: Int = 0
) : BaseViewModel() {

    val clientLiveData = MutableLiveData<ObiectWithPrices>()

    val beerListLiveData = MutableLiveData<List<BeerModel>>()
    val beerList = ObjectCache.getInstance().getList(BeerModel::class, "beerList")?.toMutableList()
        ?: mutableListOf()

    val cansList = ObjectCache.getInstance().getList(CanModel::class, "canList")
        ?: listOf()

    var selectedCan: CanModel? = null
    var saleDateCalendar: Calendar = Calendar.getInstance()
    private val _saleDayLiveData = MutableLiveData<String>()
    val saleDayLiveData: LiveData<String>
        get() = _saleDayLiveData

    val saleItemsList = mutableListOf<TempBeerItemModel>()
    val saleItemsLiveData = MutableLiveData<List<TempBeerItemModel>>()

    val barrelOutItems = mutableListOf<SaleRequestModel.BarrelOutItem>()
    var moneyOut: SaleRequestModel.MoneyOutItem? = null
    var isGift = false

    init {
        // send getDebt request: TODO

        getClient()

        clientLiveData.observeForever {
            attachPrices(it.prices)
        }
        _saleDayLiveData.value = dateFormatDash.format(saleDateCalendar.time)
    }

    private fun attachPrices(pricesForClient: List<ObjToBeerPrice>) {
        beerListLiveData.value = beerList.map {
            val price = pricesForClient.find { bp ->
                bp.beerID == it.id
            }?.fasi?.toDouble() ?: 0.0

            BeerModel(
                it.id,
                it.dasaxeleba,
                it.displayColor,
                "%.2f".format(price).toDouble(),
                it.sortValue
            )
        }
    }

    private fun getClient() {
        ioScope.launch {
            val clientData = database.getObiectsWithPrices(clientID)
            uiScope.launch {
                clientLiveData.value = clientData
            }
        }
    }

    fun setCan(pos: Int) {
        selectedCan = if (pos >= 0)
            cansList[pos]
        else
            null
    }

    fun addDelivery(deliveryDataComment: String) {

        val saleRequestModel = SaleRequestModel(
            clientID,
            Session.get().getUserID(),
            deliveryDataComment,
            Session.get().getUserID(),
            sales = saleItemsList.map {
                it.toRequestSaleItem(
                    dateFormatDash.format(saleDateCalendar.time),
                    orderID,
                    isGift
                )
            },
            barrels = getEmptyBarrelsList(),
            money = moneyOut
        )

        Log.d(TAG, deliveryDataComment)

        sendRequest(
            ApeniApiService.getInstance().addSales(saleRequestModel),
            successWithData = {
                Log.d(TAG, it)
            },
            finally = {
                Log.d(TAG, "finaly" + it.toString())
                moneyOut = null
                barrelOutItems.clear()
            }
        )
    }

    fun onSaleDateSelected(year: Int, month: Int, day: Int) {
        saleDateCalendar.set(year, month, day)
        _saleDayLiveData.value = dateFormatDash.format(saleDateCalendar.time)
    }

    fun removeSaleItemFromList(saleItem: TempBeerItemModel) {
        saleItemsList.remove(saleItem)
        saleItemsLiveData.value = saleItemsList
    }

    fun addSaleItemToList(saleItem: TempBeerItemModel) {
        saleItemsList.add(saleItem)
        saleItemsLiveData.value = saleItemsList
    }

    fun addBarrelToList(barrelType: Int, count: Int) {
        barrelOutItems.add(
            SaleRequestModel.BarrelOutItem(
                0,
                dateFormatDash.format(saleDateCalendar.time),
                barrelType,
                count
            )
        )
    }

    fun getEmptyBarrelsList(): List<SaleRequestModel.BarrelOutItem>? {
        return if (barrelOutItems.size > 0)
            barrelOutItems
        else
            null
    }

    fun setMoney(text: Editable?) {
        if (!text.isNullOrEmpty())
            moneyOut = SaleRequestModel.MoneyOutItem(
                0,
                dateFormatDash.format(saleDateCalendar.time),
                text.toString().toDouble()
            )
    }

    companion object {
        const val TAG = "AddDelivery-----"
    }
}
