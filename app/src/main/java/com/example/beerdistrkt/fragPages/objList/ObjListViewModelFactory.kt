package com.example.beerdistrkt.fragPages.objList

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.beerdistrkt.db.ApeniDatabaseDao

class ObjListViewModelFactory(
    private val dataSource: ApeniDatabaseDao,
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ObjListViewModel::class.java)) {
            return ObjListViewModel(dataSource, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}