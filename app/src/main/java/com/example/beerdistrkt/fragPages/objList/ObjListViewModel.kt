package com.example.beerdistrkt.fragPages.objList

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.beerdistrkt.db.ApeniDataBase
import com.example.beerdistrkt.db.ApeniDatabaseDao
import com.example.beerdistrkt.models.Obieqti
import com.example.beerdistrkt.network.ApeniApiService
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Response

class ObjListViewModel : ViewModel() {

    private val database: ApeniDatabaseDao = ApeniDataBase.getInstance().apeniDataBaseDao
    val obieqtsList = database.getAllObieqts()

    private val job = Job()
    private val ioScope = CoroutineScope(Dispatchers.IO + job)
    private val uiScope = CoroutineScope(Dispatchers.Main + job)

    init {
        Log.d(TAG, "init_Obj_List")
    }

    fun delOneObj() {
        // just test
//        CoroutineScope(Dispatchers.IO).launch {
//            database.insertObiecti(Obieqti("NEW_TEST_OBJ_KT"))
//        }
        ioScope.launch {
            val objectWithPrices = database.getObiectsWithPrices(27)
            Log.d("--------------", objectWithPrices.toString())
        }
//        _objList.value?.removeAt(2)
//        _objList.value = _objList.value
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }

    companion object {
        const val TAG = "O_L_VM"
    }
}
