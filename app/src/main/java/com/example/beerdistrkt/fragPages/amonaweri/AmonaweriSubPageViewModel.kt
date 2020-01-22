package com.example.beerdistrkt.fragPages.amonaweri

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.beerdistrkt.models.Amonaweri
import com.example.beerdistrkt.network.ApeniApiService
import com.example.beerdistrkt.utils.K_PAGE
import com.example.beerdistrkt.utils.M_PAGE
import retrofit2.Call
import retrofit2.Response

class AmonaweriSubPageViewModel : ViewModel() {

    private val _amonaweriLiveData = MutableLiveData<List<Amonaweri>>()
    val amonaweriLiveData: LiveData<List<Amonaweri>>
        get() = _amonaweriLiveData

    val TAG = "subPageVM"

    fun requestAmonaweriList(date: String, objID: Int, pagePos: Int){
        when(pagePos){
            M_PAGE -> getAmonaweriM(date, objID)
            K_PAGE -> getAmonaweriK(date, objID)
        }
    }

    fun getAmonaweriM(date: String, id: Int) {
        ApeniApiService.get().getAmonaweriM(date, id)
            .enqueue(object : retrofit2.Callback<List<Amonaweri>> {
                override fun onFailure(call: Call<List<Amonaweri>>, t: Throwable) {
                    Log.d(TAG, "fail ${t.message}")
                }
                override fun onResponse(
                    call: Call<List<Amonaweri>>,
                    response: Response<List<Amonaweri>>
                ) {
                    response.let {
                        val resp = response.body()
                        _amonaweriLiveData.value = resp
                        Log.d(TAG, "size ${resp?.size} - " + resp!![1].toString())
                    }
                }
            })
    }

    fun getAmonaweriK(date: String, id: Int) {
        ApeniApiService.get().getAmonaweriK(date, id)
            .enqueue(object : retrofit2.Callback<List<Amonaweri>> {
                override fun onFailure(call: Call<List<Amonaweri>>, t: Throwable) {
                    Log.d(TAG, "fail ${t.message}")
                }
                override fun onResponse(
                    call: Call<List<Amonaweri>>,
                    response: Response<List<Amonaweri>>
                ) {
                    response.let {
                        val resp = response.body()
                        _amonaweriLiveData.value = resp
                        Log.d(TAG, "size ${resp?.size} - " + resp!![1].toString())
                    }
                }
            })
    }
}
