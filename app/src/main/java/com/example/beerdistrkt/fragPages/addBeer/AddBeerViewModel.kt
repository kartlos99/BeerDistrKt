package com.example.beerdistrkt.fragPages.addBeer

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.beerdistrkt.BaseViewModel
import com.example.beerdistrkt.fragPages.mitana.AddDeliveryViewModel
import com.example.beerdistrkt.models.BeerModelBase
import com.example.beerdistrkt.network.ApeniApiService
import com.example.beerdistrkt.storage.ObjectCache
import com.example.beerdistrkt.utils.ApiResponseState

class AddBeerViewModel : BaseViewModel() {

    val beerList = ObjectCache.getInstance().getList(BeerModelBase::class, ObjectCache.BEER_LIST_ID)
        ?: mutableListOf()

    private val _addBeerLiveData = MutableLiveData<ApiResponseState<String>>()
    val addBeerLiveData: LiveData<ApiResponseState<String>>
        get() = _addBeerLiveData

    fun sendDataToDB(beer: BeerModelBase) {

        _addBeerLiveData.value = ApiResponseState.Loading(true)
        sendRequest(
            ApeniApiService.getInstance().addBeer(beer),
            successWithData = {
                _addBeerLiveData.value = ApiResponseState.Success(it)
            },
            responseFailure = { code, error ->
                _addBeerLiveData.value = ApiResponseState.ApiError(code, error)
            },
            finally = {
                _addBeerLiveData.value = ApiResponseState.Loading(false)
            }
        )

        /*val queue: RequestQueue =
            Volley.newRequestQueue(ApplicationProvider.getApplicationContext())
        val request: StringRequest = object : StringRequest(
            Request.Method.POST,
            Constantebi.URL_INS_BEER,
            object : Listener<String?>() {
                fun onResponse(response: String) {
                    Toast.makeText(
                        ApplicationProvider.getApplicationContext<Context>(),
                        "$response +", Toast.LENGTH_SHORT
                    ).show()
                    if (response == "ჩაწერილია!" || response == "განახლებულია!") {
                        val globalServise =
                            GlobalServise(ApplicationProvider.getApplicationContext())
                        globalServise.get_Prises()
                        globalServise.get_BeerList()
                        onBackPressed()
                    }
                    btn_beerDone.setEnabled(true)
                }
            },
            object : ErrorListener() {
                fun onErrorResponse(error: VolleyError) {
                    Toast.makeText(
                        ApplicationProvider.getApplicationContext(),
                        error.getMessage(),
                        Toast.LENGTH_SHORT
                    ).show()
                    btn_beerDone.setEnabled(true)
                }
            }) {
            protected val params: Map<String, String>?
                protected get() {
                    val params: MutableMap<String, String> = HashMap()
                    params["dasaxeleba"] = dasaxeleba
                    params["price"] = price
                    params["beerId"] = beerID.toString()
                    params["color"] = java.lang.String.format(
                        "#%02X%02X%02X",
                        Color.red(beerColor),
                        Color.green(beerColor),
                        Color.blue(beerColor)
                    )
                    return params
                }
        }
        request.setRetryPolicy(mRetryPolicy)
        queue.add(request)*/
    }

    fun removeBeer(beerId: Int) {
        /*val queue: RequestQueue =
            Volley.newRequestQueue(ApplicationProvider.getApplicationContext())
        val request_DelBeer: StringRequest = object : StringRequest(
            Request.Method.POST,
            Constantebi.URL_DEL_BEER,
            object : Listener<String?>() {
                fun onResponse(response: String) {
                    Toast.makeText(
                        ApplicationProvider.getApplicationContext<Context>(),
                        "$response +", Toast.LENGTH_SHORT
                    ).show()
                    if (response == "Removed!") {
                        for (i in 0 until Constantebi.ludiList.size()) {
                            if (Constantebi.ludiList.get(i).getId() === beerId) {
                                Constantebi.ludiList.remove(i)
                            }
                        }
                        beerListAdapter.notifyDataSetChanged()
                    }
                }
            },
            object : ErrorListener() {
                fun onErrorResponse(error: VolleyError) {
                    Toast.makeText(
                        ApplicationProvider.getApplicationContext<Context>(),
                        error.toString().toString() + " -",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }) {
            protected val params: Map<String, String>?
                protected get() {
                    val params: MutableMap<String, String> = HashMap()
                    params["beerId"] = beerId.toString()
                    return params
                }
        }
        queue.add(request_DelBeer)*/
    }
}
