package com.example.beerdistrkt.objList

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.beerdistrkt.models.Obieqti
import com.example.beerdistrkt.network.ApeniApiService
import retrofit2.Call
import retrofit2.Response

class ObjListViewModel : ViewModel() {

    val isUpdating = MutableLiveData<Boolean>()

    private val _objList = MutableLiveData<MutableList<Obieqti>>()
    val objList: LiveData<MutableList<Obieqti>>
        get() = _objList

    init {
        Log.d(TAG, "init_Obj_List")
        getObjects()
    }

    fun delOneObj() {
        _objList.value?.removeAt(2)
        _objList.value = _objList.value
    }

    private fun getObjects() {
        isUpdating.value = true
        ApeniApiService.get().getObieqts().enqueue(object : retrofit2.Callback<List<Obieqti>> {
            override fun onFailure(call: Call<List<Obieqti>>, t: Throwable) {
                isUpdating.value = false
                Log.d(TAG, "fail ${t.message}")
            }

            override fun onResponse(call: Call<List<Obieqti>>, response: Response<List<Obieqti>>) {
                Log.d(TAG, "respOK")
                isUpdating.value = false
                response.body()?.let {
                    if (it.isNotEmpty()) {
                        Log.d("____size___VM____", it.size.toString())
                        _objList.value = it as MutableList<Obieqti>
                        Log.d(TAG, it.firstOrNull().toString())
                    }
                }
            }
        })
    }

    companion object {
        const val TAG = "O_L_VM"
    }
}
