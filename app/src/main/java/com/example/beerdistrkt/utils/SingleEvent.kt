package com.example.beerdistrkt.utils

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

class SingleEvent<out T>(private val content: T) {

    private var hasBeenHandled = false

    /**
     * Returns the content and prevents its use again.
     */
    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    /**
     * Returns the content, even if it's already been handled.
     */
    fun peekContent(): T = content
}

typealias SingleUnitEvent = SingleEvent<Unit>

typealias SingleMutableLiveDataEvent<E> = MutableLiveData<SingleEvent<E>>
typealias SingleLiveDataEvent<E> = LiveData<SingleEvent<E>>


fun <T : Any> T.asSingleEvent(): SingleEvent<T> {
    return SingleEvent(this)
}

var <T : Any> MutableLiveData<SingleEvent<T>>.eventValue: T
    get() = throw Exception("This property doesn't have getter")
    set(v) {
        value = v.asSingleEvent()
        return Unit
    }


inline fun <T> LiveData<SingleEvent<T>>.observeSingleEvent(
    lifecycleOwner: LifecycleOwner,
    crossinline onChanged: (T) -> Unit
) {
    observe(lifecycleOwner, Observer<SingleEvent<T>> {
        if (it.getContentIfNotHandled() != null)
            onChanged(it.peekContent())
    })
}

inline fun <T> LiveData<SingleEvent<T>>.observeSingleEventForever(
    lifecycleOwner: LifecycleOwner,
    crossinline onChanged: (T) -> Unit
) {
    observe(lifecycleOwner, Observer<SingleEvent<T>> {
        if (it.peekContent() != null)
            onChanged(it.peekContent())
    })
}