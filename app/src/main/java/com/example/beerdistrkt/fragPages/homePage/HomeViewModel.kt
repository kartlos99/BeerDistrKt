package com.example.beerdistrkt.fragPages.homePage

import android.util.Log
import com.example.beerdistrkt.BaseViewModel
import com.example.beerdistrkt.models.*
import com.example.beerdistrkt.network.ApeniApiService
import com.example.beerdistrkt.sendRequest
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Response

class HomeViewModel : BaseViewModel() {

    val usersLiveData = database.getUsers()
    val beerLiveData = database.getBeerList()

    init {
        Log.d(TAG, "init")
//        getObjects()
//        getPrices()
//        getUsers()
//        getBeerList()
    }

    private fun clearObieqtsList() {
        ioScope.launch {
            database.clearObiectsTable()
        }
    }

    private fun getUsers() {
        sendRequest(
            ApeniApiService.getInstance().getUsersList(),
            success = {
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
            success = {
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
            success = {
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
            success = {
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

    private fun insertUserToDB(user: User) {
        Log.d(TAG, user.toString())
        database.insertUser(user)
    }

    private fun insertBeerToDB(beerModel: BeerModel) {
        Log.d(TAG, beerModel.toString())
        database.insertbeer(beerModel)
    }

    private fun insertObiect(obieqti: Obieqti) {
        Log.d(TAG, obieqti.toString())
        database.insertObiecti(obieqti)
    }

    private fun insertBeetPrice(bPrice: ObjToBeerPrice) {
        database.insertBeerPrice(bPrice)
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