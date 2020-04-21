package com.example.beerdistrkt

import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.annotation.AnimRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

//<Response : Any, ApiResponse : DataResponse<Response>>
fun <T: Any>  Call<T>.sendRequest(
    success: ((data: T) -> Unit),
    failure: ((t: Throwable) -> Unit),
    onConnectionFailure: (Throwable) -> Unit,
    finally: ((success: Boolean) -> Unit)? = null
){
    enqueue(object :Callback<T> {
        override fun onFailure(call: Call<T>, t: Throwable) {
            Log.d("FailMSG", t.message!!)
            finally?.invoke(false)
            if (t is IOException) {
                onConnectionFailure(t)
            } else {
                failure.invoke(t)
            }
        }

        override fun onResponse(call: Call<T>, response: Response<T>) {
            finally?.invoke(true)
            if (response.isSuccessful){
                val body = response.body()
                body?.let {
                    success(it)
                }
            }
            Log.d("Resp_Code__", response.code().toString())
        }
    })
}

inline fun View.animateThis(@AnimRes resId: Int, crossinline onComplete: (() -> Unit)) {
    val animation = AnimationUtils.loadAnimation(context, resId)
    animation.setAnimationListener(object : Animation.AnimationListener {
        override fun onAnimationRepeat(animation: Animation?) {

        }

        override fun onAnimationEnd(animation: Animation?) {
            onComplete.invoke()
        }

        override fun onAnimationStart(animation: Animation?) {

        }
    })
    this.startAnimation(animation)
}

fun SnapHelper.getSnapPosition(recyclerView: RecyclerView): Int {
    val layoutManager = recyclerView.layoutManager ?: return RecyclerView.NO_POSITION
    val snapView = findSnapView(layoutManager) ?: return RecyclerView.NO_POSITION
    return layoutManager.getPosition(snapView)
}


class BaseViewModelFactory<T>(val creator: () -> T) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return creator() as T
    }
}

inline fun <reified T : ViewModel> Fragment.getViewModel(noinline creator: (() -> T)? = null): T {
    return if (creator == null)
        ViewModelProviders.of(this).get(T::class.java)
    else
        ViewModelProviders.of(this, BaseViewModelFactory(creator)).get(T::class.java)
}

inline fun <reified T : ViewModel> FragmentActivity.getViewModel(noinline creator: (() -> T)? = null): T {
    return if (creator == null)
        ViewModelProviders.of(this).get(T::class.java)
    else
        ViewModelProviders.of(this, BaseViewModelFactory(creator)).get(T::class.java)
}


fun Context.isNetworkAvailable(): Boolean {
    val connectivityManager = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
    val activeNetworkInfo = connectivityManager?.activeNetworkInfo
    return activeNetworkInfo != null && activeNetworkInfo.isConnected
}