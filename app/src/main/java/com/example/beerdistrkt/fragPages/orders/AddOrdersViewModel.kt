package com.example.beerdistrkt.fragPages.orders

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.beerdistrkt.BaseViewModel
import com.example.beerdistrkt.models.BeerModel
import com.example.beerdistrkt.models.CanModel
import com.example.beerdistrkt.models.ObiectWithPrices
import com.example.beerdistrkt.models.TempOrderItemModel
import com.example.beerdistrkt.storage.ObjectCache
import kotlinx.coroutines.launch

class AddOrdersViewModel(private val clientID: Int) : BaseViewModel() {

    val clientLiveData = MutableLiveData<ObiectWithPrices>()

    val beerList = ObjectCache.getInstance().getList(BeerModel::class, "beerList")
        ?: mutableListOf()
    val cansList = ObjectCache.getInstance().getList(CanModel::class, "canList")
        ?: listOf()

    var selectedCan: CanModel? = null

    val orderItemsList = mutableListOf<TempOrderItemModel>()
    val orderItemsLiveData = MutableLiveData<List<TempOrderItemModel>>()

    init {
        Log.d("addOrderVM", clientID.toString())
        getClient()
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

    fun addOrderItemToList(item: TempOrderItemModel){
        orderItemsList.add(item)
        orderItemsLiveData.value = orderItemsList
    }

    fun removeOrderItemFromList(item: TempOrderItemModel){
        orderItemsList.remove(item)
        orderItemsLiveData.value = orderItemsList
    }

}
