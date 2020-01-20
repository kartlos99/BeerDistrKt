package com.example.beerdistrkt.fragPages.homePage

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.beerdistrkt.db.ApeniDataBase
import com.example.beerdistrkt.db.ApeniDatabaseDao
import com.example.beerdistrkt.models.Amonaweri
import com.example.beerdistrkt.models.Obieqti
import com.example.beerdistrkt.models.ObjToBeerPrice
import com.example.beerdistrkt.network.ApeniApiService
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Response

class HomeViewModel : ViewModel() {

    private val database: ApeniDatabaseDao = ApeniDataBase.getInstance().apeniDataBaseDao
    private val job = Job()
    private val ioScope = CoroutineScope(Dispatchers.IO + job)

    init {
        Log.d(TAG, "init")
//        getObjects()
//        getPrices()
//        clearObieqtsList()
//        clearPrices()
    }

    private fun clearObieqtsList() {
        ioScope.launch {
            database.clearObiectsTable()
        }
    }

    private fun getPrices() {

        ApeniApiService.get().getPrices()
            .enqueue(object : retrofit2.Callback<List<ObjToBeerPrice>> {
                override fun onFailure(call: Call<List<ObjToBeerPrice>>, t: Throwable) {
                    Log.d(TAG, "fail prices ${t.message}")
                }

                override fun onResponse(
                    call: Call<List<ObjToBeerPrice>>,
                    response: Response<List<ObjToBeerPrice>>
                ) {
                    Log.d(TAG, "Prices_respOK")
                    response.body()?.let {
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
                }

            })
    }

    private fun getObjects() {
        ApeniApiService.get().getObieqts().enqueue(object : retrofit2.Callback<List<Obieqti>> {
            override fun onFailure(call: Call<List<Obieqti>>, t: Throwable) {
                Log.d(TAG, "fail ${t.message}")
            }

            override fun onResponse(call: Call<List<Obieqti>>, response: Response<List<Obieqti>>) {
                Log.d(TAG, "respOK")
                response.body()?.let {
                    if (it.isNotEmpty()) {
                        ioScope.launch {
                            it.forEach { obieqti ->
                                insertObiect(obieqti)
                            }
                        }
//                        Log.d("____size___VM____", it.size.toString())
//                        Log.d(TAG, it.firstOrNull().toString())
                    }
                }
            }
        })
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
