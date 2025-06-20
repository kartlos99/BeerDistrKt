package com.example.beerdistrkt

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.beerdistrkt.models.DataResponse
import com.example.beerdistrkt.utils.ApiResponseState
import com.example.beerdistrkt.utils.Session
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import java.text.SimpleDateFormat
import javax.inject.Inject

abstract class BaseViewModel : ViewModel() {

    private val _uiEventFlow: MutableSharedFlow<UiEvent> = MutableSharedFlow(1, 1)
    val uiEventFlow: SharedFlow<UiEvent> = _uiEventFlow.asSharedFlow()

    @Inject
    open lateinit var session: Session

    protected var loadingCounter = 0
    val isLoading get() = loadingCounter != 0

    var callIsBlocked = false
        set(value) {
            if (value)
                2000 waitFor {
                    field = false
                }
            field = value
        }

    private val _apiFailureMutableLiveData = MutableLiveData<ApiResponseState<Nothing>>()
    val apiFailureLiveData: LiveData<ApiResponseState<Nothing>>
        get() = _apiFailureMutableLiveData

    fun showNetworkFailMsgComplete() {
        _apiFailureMutableLiveData.value = ApiResponseState.Sleep
    }

    private fun showOnConnFailureDialog(t: Throwable) {
        _apiFailureMutableLiveData.value = ApiResponseState.NoInternetConnection
    }

    private fun showFailureDialog(throwable: Throwable) {
        _apiFailureMutableLiveData.value = ApiResponseState.ApiError(999, throwable.message ?: "")
        Log.d("response_Fail", throwable.message!!)
    }

    private fun onAuthFail() {
        _apiFailureMutableLiveData.value = ApiResponseState.ApiError(401, "No Auth!")
        Log.d("Auth_Fail", "should log out")
    }

    private fun onResponseFailure(code: Int, error: String) {
        _apiFailureMutableLiveData.value = ApiResponseState.ApiError(code, error)
        Log.d("onServer_response_Fail", "Code: $code - Text: $error")
    }

    fun forceLogout() {
        session.clearSession()
        session.loggedIn = false
        viewModelScope.launch {
            session.clearUserPreference()
            _uiEventFlow.emit(UiEvent.LogOut)
        }
    }

    fun checkToken() {
        if (!session.isAccessTokenValid())
            forceLogout()
    }

    protected fun <F : Any, T : DataResponse<F>, ApiResponse : Call<T>> sendRequest(
        apiRequest: ApiResponse,
        success: (() -> Unit)? = null,
        successWithData: ((data: F) -> Unit)? = null,
        onConnectionFailure: (Throwable) -> Unit = ::showOnConnFailureDialog,
        failure: (t: Throwable) -> Unit = ::showFailureDialog,
        authFailure: (() -> Unit)? = ::onAuthFail,
        responseFailure: (code: Int, error: String) -> Unit = ::onResponseFailure,
        finally: ((success: Boolean) -> Unit)? = null
    ) {
        apiRequest.sendRequest(
            success = success,
            successWithData = successWithData,
            onConnectionFailure = onConnectionFailure,
            failure = failure,
            authFailure = authFailure,
            responseFailure = responseFailure,
            finally = finally,
            notifyChanges = ::notifyOldDataChange
        )
    }

    val dateTimeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")
    val dateTimeFullFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    val dateFormatDash = SimpleDateFormat("yyyy-MM-dd")
    val dateFormat2Dots = SimpleDateFormat("yyyy:MM:dd")

    companion object {
        const val TAG = "TAG_VM"
    }

    sealed interface UiEvent {
        data object LogOut : UiEvent
    }
}