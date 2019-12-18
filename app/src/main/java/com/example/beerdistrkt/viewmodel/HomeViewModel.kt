package com.example.beerdistrkt.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.beerdistrkt.db.ApeniDatabaseDao
import com.example.beerdistrkt.models.Obieqti
import com.example.beerdistrkt.network.ApeniApiService
import com.example.beerdistrkt.objList.ObjListViewModel
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Response

class HomeViewModel(
    val database: ApeniDatabaseDao,
    application: Application
) : ViewModel() {

    private val job = Job()
    private val ioScope = CoroutineScope(Dispatchers.IO + job)

    init {
        Log.d(TAG, "init")
        getObjects()
//        clearObieqtsList()
    }

    private fun clearObieqtsList(){
        ioScope.launch {
            database.clear()
        }
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
                        Log.d("____size___VM____", it.size.toString())
                        Log.d(TAG, it.firstOrNull().toString())
                    }
                }
            }
        })
    }

    private fun insertObiect(obieqti: Obieqti) {
        Log.d(TAG, obieqti.toString())
        database.insertObiecti(obieqti)
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }

    companion object {
        const val TAG = "homeVM"
    }
}
