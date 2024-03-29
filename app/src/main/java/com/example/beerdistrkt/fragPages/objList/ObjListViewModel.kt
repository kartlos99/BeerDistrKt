package com.example.beerdistrkt.fragPages.objList

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.example.beerdistrkt.BaseViewModel
import com.example.beerdistrkt.models.ClientDeactivateModel
import com.example.beerdistrkt.models.Obieqti
import com.example.beerdistrkt.network.ApeniApiService
import com.example.beerdistrkt.storage.ObjectCache
import com.example.beerdistrkt.storage.ObjectCache.Companion.CLIENTS_LIST_ID
import kotlinx.coroutines.launch

class ObjListViewModel : BaseViewModel() {

    val clientsList = database.getAllObieqts()
    val clients = MutableLiveData<List<Obieqti>>()

    private val state = SavedStateHandle()

    val searchQuery = state.getLiveData("searchQuery", "")

    init {
        Log.d(TAG, "init_Obj_List")
        clientsList.observeForever {
            ObjectCache.getInstance().putList(Obieqti::class, CLIENTS_LIST_ID, it)
        }
    }

    fun delOneObj() {
        // just test
//        CoroutineScope(Dispatchers.IO).launch {
//            database.insertObiecti(Obieqti("NEW_TEST_OBJ_KT"))
//        }
        ioScope.launch {
            val objectWithPrices = database.getCustomerWithPrices(27)
            Log.d("--------------", objectWithPrices.toString())
        }
//        _objList.value?.removeAt(2)
//        _objList.value = _objList.value
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }

    fun deactivateClient(clientID: Int?) {
        if (clientID == null)
            return
        sendRequest(
            ApeniApiService.getInstance().deactivateClient(ClientDeactivateModel(clientID)),
            success = {
                ioScope.launch {
                    database.deleteClient(clientID)
                }
            }
        )
    }

    fun onNewQuery(query: String) {
        clients.value = clientsList.value?.filter {
            it.dasaxeleba.contains(query)
        } ?: listOf()
    }

    companion object {
        const val TAG = "O_L_VM"
    }
}
