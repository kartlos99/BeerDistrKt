package com.example.beerdistrkt.fragPages.statement

import androidx.lifecycle.MutableLiveData
import com.example.beerdistrkt.BaseViewModel
import com.example.beerdistrkt.models.ObiectWithPrices
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = StatementViewModel.Factory::class)
class StatementViewModel @AssistedInject constructor(
    @Assisted val clientID: Int
) : BaseViewModel() {

    val clientLiveData = MutableLiveData<ObiectWithPrices>()

    init {
        getClient()
    }

    private fun getClient() {
        ioScope.launch {
            val clientData = database.getCustomerWithPrices(clientID)
            uiScope.launch {
                clientLiveData.value = clientData
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(clientID: Int): StatementViewModel
    }
}
