package com.example.beerdistrkt

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.beerdistrkt.models.ChangePassRequestModel
import com.example.beerdistrkt.network.ApeniApiService
import com.example.beerdistrkt.storage.SharedPreferenceDataSource
import com.example.beerdistrkt.utils.Session
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActViewModel @Inject constructor(
    override var session: Session
) : BaseViewModel() {

    val headerUpdateLiveData = MutableLiveData<Int>()

    private val _eventsFlow: MutableSharedFlow<ActUiEvent> = MutableSharedFlow()
    val eventsFlow: SharedFlow<ActUiEvent> = _eventsFlow.asSharedFlow()

    fun changePassword(oldPass: String, newPass: String, callback: (text: String?) -> Unit) {
        sendRequest(
            ApeniApiService.getInstance().changePassword(
                ChangePassRequestModel(
                    session.userID.orEmpty(),
                    oldPass,
                    newPass
                )
            ),
            success = {
                callback(null)
            },
            responseFailure = { _, error ->
                callback(error)
            }
        )
    }

    fun updateNavHeader() {
        headerUpdateLiveData.value = 1
    }

    fun performUserLogout() {
        viewModelScope.launch {
            session.clearSession()
            session.clearUserPreference()
            session.loggedIn = false
            SharedPreferenceDataSource.getInstance().saveUserName("")
            SharedPreferenceDataSource.getInstance().savePassword("")
            _eventsFlow.emit(ActUiEvent.GoToLoginPage)
        }
    }

    sealed interface ActUiEvent {
        data object GoToLoginPage : ActUiEvent
    }
}