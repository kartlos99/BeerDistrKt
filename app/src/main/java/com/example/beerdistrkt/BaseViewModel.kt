package com.example.beerdistrkt

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.beerdistrkt.db.ApeniDataBase
import com.example.beerdistrkt.db.ApeniDatabaseDao
import com.example.beerdistrkt.models.DataResponse
import com.example.beerdistrkt.utils.ApiResponseState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import retrofit2.Call
import java.text.SimpleDateFormat

abstract class BaseViewModel : ViewModel() {
    protected val database: ApeniDatabaseDao = ApeniDataBase.getInstance().apeniDataBaseDao
    protected val job = Job()
    protected val ioScope = CoroutineScope(Dispatchers.IO + job)
    protected val uiScope = CoroutineScope(Dispatchers.Main + job)

    protected var loadingCounter = 0
    val isLoading get() = loadingCounter != 0

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

    private fun onResponseFailure(code: Int, error: String) {
        _apiFailureMutableLiveData.value = ApiResponseState.ApiError(code, error)
        Log.d("onServer_response_Fail", "Code: $code - Text: $error")
    }

    protected fun <F : Any, T : DataResponse<F>, ApiResponse : Call<T>> sendRequest(
        apiRequest: ApiResponse,
        success: (() -> Unit)? = null,
        successWithData: ((data: F) -> Unit)? = null,
        onConnectionFailure: (Throwable) -> Unit = ::showOnConnFailureDialog,
        failure: (t: Throwable) -> Unit = ::showFailureDialog,
        authFailure: (() -> Unit)? = null,
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
            finally = finally
        )
    }

    val dateTimeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")
    val dateTimeFullFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    val dateFormatDash = SimpleDateFormat("yyyy-MM-dd")
    val dateFormat2Dots = SimpleDateFormat("yyyy:MM:dd")
}