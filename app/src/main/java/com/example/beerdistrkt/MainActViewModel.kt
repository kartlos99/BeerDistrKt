package com.example.beerdistrkt

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.beerdistrkt.fragPages.homePage.domain.usecase.RefreshBaseDataUseCase
import com.example.beerdistrkt.models.ChangePassRequestModel
import com.example.beerdistrkt.network.ApeniApiService
import com.example.beerdistrkt.utils.Session
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActViewModel @Inject constructor(
    private val refreshBaseDataUseCase: RefreshBaseDataUseCase,
): BaseViewModel() {

    val headerUpdateLiveData = MutableLiveData<Int>()

    val showContentFlow: MutableStateFlow<Boolean> = MutableStateFlow(true)

    init {
        updateInitialData()
    }

    private fun updateInitialData() = viewModelScope.launch {
        showContentFlow.emit(false)
        refreshBaseDataUseCase()
        showContentFlow.emit(true)
    }

    fun changePassword(oldPass: String, newPass: String, callback: (text: String?) -> Unit) {
        sendRequest(
            ApeniApiService.getInstance().changePassword(
                ChangePassRequestModel(
                    Session.get().userID ?: "",
                    oldPass,
                    newPass
                )
            ),
            success = {
                callback(null)
            },
            responseFailure = {_, error ->
                callback(error)
            }
        )
    }

    fun updateNavHeader() {
        headerUpdateLiveData.value = 1
    }

}