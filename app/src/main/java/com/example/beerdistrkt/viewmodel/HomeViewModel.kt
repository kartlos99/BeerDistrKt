package com.example.beerdistrkt.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {
    // TODO: Implement the ViewModel
    init {
        Log.d(TAG, "init")
    }

    companion object {
        const val TAG = "homeVM"
    }
}
