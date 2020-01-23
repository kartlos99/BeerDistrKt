package com.example.beerdistrkt

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.beerdistrkt.db.ApeniDataBase
import com.example.beerdistrkt.db.ApeniDatabaseDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import retrofit2.Call

abstract class BaseViewModel : ViewModel() {
    protected val database: ApeniDatabaseDao = ApeniDataBase.getInstance().apeniDataBaseDao
    protected val job = Job()
    protected val ioScope = CoroutineScope(Dispatchers.IO + job)

    private val _apiFailureMutableLiveData = MutableLiveData<String>()
    val apiFailureMutableLiveData: LiveData<String>
        get() = _apiFailureMutableLiveData



    private fun showConnFailureDialog(throwable: Throwable) {
        _apiFailureMutableLiveData.value = throwable.message
    }

    protected fun <T : Any, ApiResponse : Call<T>> sendRequest(
        apiRequest: ApiResponse,
        success: ((data: T) -> Unit),
        onConnectionFailure: (Throwable) -> Unit = ::showConnFailureDialog,
        failure: (t: Throwable) -> Unit = ::showConnFailureDialog,
        finally: ((success: Boolean) -> Unit)? = null
    ) {
        apiRequest.sendRequest(
            success = success,
            onConnectionFailure = onConnectionFailure,
            failure = failure,
            finally = finally
        )
    }
}