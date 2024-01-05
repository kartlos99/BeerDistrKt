package com.example.beerdistrkt.fragPages.objList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.beerdistrkt.BaseViewModel
import com.example.beerdistrkt.fragPages.objList.model.Customer
import com.example.beerdistrkt.models.ClientDeactivateModel
import com.example.beerdistrkt.models.CustomerIdlInfo
import com.example.beerdistrkt.models.Obieqti
import com.example.beerdistrkt.network.ApeniApiService
import com.example.beerdistrkt.repos.ApeniRepo
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ObjListViewModel : BaseViewModel() {

    private var customers: List<Customer> = listOf()
    private val _customersLiveData = MutableLiveData<List<Customer>>()
    val customersLiveData: LiveData<List<Customer>>
        get() = _customersLiveData

    private val state = SavedStateHandle()

    val searchQuery = state.getLiveData("searchQuery", "")

    private val repository = ApeniRepo()

    init {
        viewModelScope.launch {
            repository.allData.collectLatest {
                updateList(it.first, it.second)
            }
        }
        repository.getCustomers()
        repository.getCustomersIdleInfo()
        repository.getBaseData()
    }

    private fun updateList(
        customerObjects: List<Obieqti>,
        idles: List<CustomerIdlInfo>
    ) {
        customers = customerObjects.map { obj ->
            obj.toCustomer().apply {
                warnInfo = idles.firstOrNull { idleInfo ->
                    obj.id == idleInfo.clientID
                }
            }
        }
        _customersLiveData.value = customers
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
        _customersLiveData.value = customers.filter {
            it.dasaxeleba.contains(query)
        }
    }

    fun filterNotableItems(filtering: Boolean) {
        _customersLiveData.value =
            if (filtering) customers.filter { it.warnInfo != null }
            else customers
    }

    companion object {
        const val TAG = "O_L_VM"
    }
}
