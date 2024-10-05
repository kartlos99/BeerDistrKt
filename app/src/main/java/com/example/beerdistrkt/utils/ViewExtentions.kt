package com.example.beerdistrkt.utils

import android.animation.Animator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.CheckResult
import androidx.annotation.StringRes
import androidx.appcompat.widget.SearchView
import com.google.android.gms.common.internal.Preconditions.checkMainThread
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.onStart

fun ViewGroup.inflate(layoutRes: Int): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, false)
}

fun View.goAway() {
    this.visibility = View.GONE
}

fun View.hide() {
    this.visibility = View.INVISIBLE
}

fun View.show() {
    this.visibility = View.VISIBLE
}

@Deprecated("use native isVisible", ReplaceWith("isVisible"))
fun View.visibleIf(boolean: Boolean) {
    if (boolean)
        this.show()
    else
        this.goAway()
}

fun View.explodeAnim() {
    this.animate()
        .setDuration(400)
        .scaleX(1.2F)
        .scaleY(1.2F)
        .setListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(p0: Animator) {}

            override fun onAnimationEnd(p0: Animator) {
                this@explodeAnim.animate()
                    .setDuration(400)
                    .scaleX(1F)
                    .scaleY(1F)
                    .start()
            }

            override fun onAnimationCancel(p0: Animator) {}

            override fun onAnimationStart(p0: Animator) {}
        })
        .start()
}

inline fun SearchView.onTextChanged(crossinline listener: (String) -> Unit) {
    this.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?): Boolean {
            return true
        }

        override fun onQueryTextChange(newText: String?): Boolean {
            listener(newText.orEmpty())
            return true
        }
    })
}

fun SearchView.changesAsFlow(): Flow<CharSequence?> {
    return callbackFlow {
        checkMainThread("")

        val qListener = object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                trySend(newText.orEmpty())
                return true
            }
        }
        this@changesAsFlow.setOnQueryTextListener(qListener)

        awaitClose { }
    }.onStart {
        emit("")
    }
}

fun CharSequence?.orEmpty(): String = this?.toString() ?: ""

fun TextView.setVisibleWithText(text: String?) {
    if (text.isNullOrEmpty()) {
        visibility = View.GONE
    } else {
        visibility = View.VISIBLE
        this.text = text
    }
}

fun TextView.setVisibleWithText(@StringRes textResId: Int?) {
    if (textResId != null) {
        visibility = View.VISIBLE
        this.setText(textResId)
    } else {
        visibility = View.GONE
    }
}