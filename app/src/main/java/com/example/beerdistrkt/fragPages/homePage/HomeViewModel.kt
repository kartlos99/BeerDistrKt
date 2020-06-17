package com.example.beerdistrkt.fragPages.homePage

import android.util.Log
import com.example.beerdistrkt.BaseViewModel
import com.example.beerdistrkt.models.*
import com.example.beerdistrkt.network.ApeniApiService
import com.example.beerdistrkt.storage.ObjectCache
import kotlinx.coroutines.*

class HomeViewModel : BaseViewModel() {

    val usersLiveData = database.getUsers()
    val beerLiveData = database.getBeerList()
    val cansLiveData = database.getCansList()

    init {
        Log.d(TAG, "init")
//        getObjects()
//        getPrices()
//        getUsers()
//        getBeerList()
//        getCanTypes()
        beerLiveData.observeForever {
            ObjectCache.getInstance().putList(BeerModel::class, "beerList", it)
        }
        cansLiveData.observeForever {
            it.forEach {can ->
                Log.d("CanS", can.toString())
            }
        }
    }

    private fun clearObieqtsList() {
        ioScope.launch {
            database.clearObiectsTable()
        }
    }

    private fun getUsers() {
        sendRequest(
            ApeniApiService.getInstance().getUsersList(),
            successWithData = {
                Log.d(TAG, "Users_respOK")
                if (it.isNotEmpty()) {
                    ioScope.launch {
                        database.clearUserTable()
                        it.forEach { user ->
                            insertUserToDB(user)
                        }
                    }
                }
            }
        )
    }

    private fun getBeerList() {
        sendRequest(
            ApeniApiService.getInstance().getBeerList(),
            successWithData = {
                Log.d(TAG, "Users_respOK")
                if (it.isNotEmpty()) {
                    ioScope.launch {
                        database.clearBeerTable()
                        it.forEach { beer ->
                            insertBeerToDB(beer)
                        }
                    }
                }
            }
        )
    }

    private fun getPrices() {
        sendRequest(
            ApeniApiService.getInstance().getPrices(),
            successWithData = {
                clearPrices()
                if (it.isNotEmpty()) {
                    ioScope.launch {
                        it.forEach { bPrice ->
                            insertBeetPrice(bPrice)
                        }
                    }
                    Log.d("__Price__size___VM____", it.size.toString())
                    Log.d(TAG, it.firstOrNull().toString())
                }
            }
        )
    }

    private fun getObjects() {
        sendRequest(
            ApeniApiService.getInstance().getObieqts(),
            successWithData = {
                clearObieqtsList()
                if (it.isNotEmpty()) {
                    ioScope.launch {
                        it.forEach { obieqti ->
                            insertObiect(obieqti)
                        }
                    }
                }
            }
        )
    }

    private fun getCanTypes() {
        sendRequest(
            ApeniApiService.getInstance().getCanList(),
            successWithData = {
                Log.d(TAG, "Cans_respOK")
                if (it.isNotEmpty()) {
                    ioScope.launch {
                        database.clearCansTable()
                        it.forEach { can ->
                            insertCanToDB(can)
                        }
                    }
                }
            }
        )
    }

    private fun insertUserToDB(user: User) {
        Log.d(TAG, user.toString())
        database.insertUser(user)
    }

    private fun insertBeerToDB(beerModel: BeerModel) {
        Log.d(TAG, beerModel.toString())
        database.insertBeer(beerModel)
    }

    private fun insertObiect(obieqti: Obieqti) {
        Log.d(TAG, obieqti.toString())
        database.insertObiecti(obieqti)
    }

    private fun insertBeetPrice(bPrice: ObjToBeerPrice) {
        database.insertBeerPrice(bPrice)
    }

    private fun insertCanToDB(canModel: CanModel) {
        database.insertCan(canModel)
    }

    private fun clearPrices() {
        ioScope.launch {
            database.clearPricesTable()
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "onCleared job dacenselda")
        job.cancel()
    }

    companion object {
        const val TAG = "homeVM"
    }
}