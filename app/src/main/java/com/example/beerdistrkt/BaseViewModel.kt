package com.example.beerdistrkt

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.beerdistrkt.db.ApeniDataBase
import com.example.beerdistrkt.db.ApeniDatabaseDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import retrofit2.Call
import java.text.SimpleDateFormat

abstract class BaseViewModel : ViewModel() {
    protected val database: ApeniDatabaseDao = ApeniDataBase.getInstance().apeniDataBaseDao
    protected val job = Job()
    protected val ioScope = CoroutineScope(Dispatchers.IO + job)

    private val _apiFailureMutableLiveData = MutableLiveData<String>()
    val apiFailureLiveData: LiveData<String>
        get() = _apiFailureMutableLiveData

    private fun showOnConnFailureDialog(t: Throwable){
        _apiFailureMutableLiveData.value = "check Net. Connection: ${t.message}"
    }

    private fun showFailureDialog(throwable: Throwable) {
        _apiFailureMutableLiveData.value = throwable.message
        Log.d("respFail", throwable.message!!)
    }

    protected fun <T : Any, ApiResponse : Call<T>> sendRequest(
        apiRequest: ApiResponse,
        success: ((data: T) -> Unit),
        onConnectionFailure: (Throwable) -> Unit = ::showOnConnFailureDialog,
        failure: (t: Throwable) -> Unit = ::showFailureDialog,
        finally: ((success: Boolean) -> Unit)? = null
    ) {
        apiRequest.sendRequest(
            success = success,
            onConnectionFailure = onConnectionFailure,
            failure = failure,
            finally = finally
        )
    }

    val dateTimeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")
    val dateFormat_desh = SimpleDateFormat("yyyy-MM-dd")
    val dateFormat_2dots = SimpleDateFormat("yyyy:MM:dd")
}