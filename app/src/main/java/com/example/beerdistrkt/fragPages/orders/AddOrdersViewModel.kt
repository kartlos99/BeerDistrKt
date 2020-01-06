package com.example.beerdistrkt.fragPages.orders

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.beerdistrkt.BaseViewModel
import com.example.beerdistrkt.db.ApeniDataBase
import com.example.beerdistrkt.db.ApeniDatabaseDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class AddOrdersViewModel : ViewModel() {

    private val database: ApeniDatabaseDao = ApeniDataBase.getInstance().apeniDataBaseDao
    private val job = Job()
    private val ioScope = CoroutineScope(Dispatchers.IO + job)


    fun logObjPr(obieqtisID: Int) {
        ioScope.launch {
            val objectWithPrices = database.getObiectsWithPrices(obieqtisID)
            Log.d("--------------", objectWithPrices.toString())
        }
    }

}
