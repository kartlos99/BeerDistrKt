package com.example.beerdistrkt.fragPages.amonaweri

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.beerdistrkt.fragPages.homePage.HomeViewModel
import com.example.beerdistrkt.models.Amonaweri
import com.example.beerdistrkt.network.ApeniApiService
import retrofit2.Call
import retrofit2.Response

class AmonaweriSubPageViewModel : ViewModel() {

    private val _amonaweriLiveData = MutableLiveData<List<Amonaweri>>()
    val amonaweriLiveData: LiveData<List<Amonaweri>>
        get() = _amonaweriLiveData

    fun requestAmonaweriList(date: String, id: Int){
        getAmonaweri(date, id)
        Log.d("TAG VM_initPage", "size $date - $id " )
    }

    fun getAmonaweri(date: String, id: Int) {
        ApeniApiService.get().getAmonaweriM(date, id)
            .enqueue(object : retrofit2.Callback<List<Amonaweri>> {
                override fun onFailure(call: Call<List<Amonaweri>>, t: Throwable) {
                    Log.d(HomeViewModel.TAG, "fail ${t.message}")
                }
                override fun onResponse(
                    call: Call<List<Amonaweri>>,
                    response: Response<List<Amonaweri>>
                ) {
                    response.let {
                        val resp = response.body()
                        _amonaweriLiveData.value = resp
                        Log.d("TAG VM", "size ${resp?.size} - " + resp!![1].toString())
                    }
                }
            })
    }
}
