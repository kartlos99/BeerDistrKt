package com.example.beerdistrkt.fragPages.objList

import android.util.Log
import com.example.beerdistrkt.BaseViewModel
import kotlinx.coroutines.launch

class ObjListViewModel : BaseViewModel() {

    val clientsList = database.getAllObieqts()

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
