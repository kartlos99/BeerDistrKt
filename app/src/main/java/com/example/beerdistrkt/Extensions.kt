package com.example.beerdistrkt

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.net.ConnectivityManager
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.AnimRes
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.MainThread
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.example.beerdistrkt.common.fragments.ClientDebtFragment
import com.example.beerdistrkt.models.DataResponse
import com.example.beerdistrkt.models.Order
import com.example.beerdistrkt.models.OrderStatus
import com.example.beerdistrkt.storage.SharedPreferenceDataSource
import com.example.beerdistrkt.utils.RELATIVE_FRICTION_SIZE
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.lifecycle.withCreationCallback
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.Date
import kotlin.coroutines.CoroutineContext

//learning commit update 22 just count . last bit ...fcce

//<Response : Any, ApiResponse : DataResponse<Response>>
fun <F : Any, T : DataResponse<F>> Call<T>.sendRequest(
    success: (() -> Unit)? = null,
    successWithData: ((data: F) -> Unit)? = null,
    failure: ((t: Throwable) -> Unit),
    authFailure: (() -> Unit)? = null,
    responseFailure: (code: Int, error: String) -> Unit = { _: Int, _: String -> },
    onConnectionFailure: (Throwable) -> Unit,
    finally: ((success: Boolean) -> Unit)? = null,
    notifyChanges: ((logID: String) -> Unit)? = null
) {
    enqueue(object : Callback<T> {
        override fun onFailure(call: Call<T>, t: Throwable) {
            Log.d("FailMSG", t.message ?: DataResponse.UNKNOWN_ERROR)
            finally?.invoke(false)
            if (t is IOException) {
                onConnectionFailure(t)
            } else {
                failure.invoke(t)
            }
        }

        override fun onResponse(call: Call<T>, response: Response<T>) {
            finally?.invoke(true)
            if (response.isSuccessful) {
                val body = response.body()
                if (body?.success == true) {
                    success?.invoke()
                    if (successWithData != null) {
                        if (body.data == null)
                            responseFailure(DataResponse.ErrorCodeDataIsNull, "Data expected")
                        else {
                            successWithData(body.data)
                            body.logRecordId?.let {
                                if (it >= 0) notifyChanges?.invoke(it.toString())
                            }
                        }
                    }
                } else {
                    if (body?.errorCode == 401 && authFailure != null)
                        authFailure.invoke()
                    else
                        responseFailure(
                            body?.errorCode ?: DataResponse.UnknownError,
                            body?.errorText ?: DataResponse.UNKNOWN_ERROR
                        )
                }

            } else {

                if (response.code() == 401 && authFailure != null) {
                    authFailure.invoke()
                } else {
                    responseFailure(response.code(), response.message())
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

@MainThread
inline fun <reified VM : ViewModel, VMF> Fragment.paramViewModels(
    noinline callback: (factory: VMF) -> ViewModel
): Lazy<VM> {
    return viewModels(
        extrasProducer = {
            defaultViewModelCreationExtras.withCreationCallback<VMF> {
                callback(it)
            }
        }
    )
}

/*
class BaseViewModelFactory<T>(val creator: () -> T) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return creator() as T
    }
}

inline fun <reified T : ViewModel> Fragment.getViewModel(noinline creator: (() -> T)? = null): T {
    return if (creator == null)
        ViewModelProvider(this)[T::class.java]
    else
        ViewModelProvider(this, BaseViewModelFactory(creator))[T::class.java]
}

inline fun <reified T : ViewModel> FragmentActivity.getViewModel(noinline creator: (() -> T)? = null): T {
    return if (creator == null)
        ViewModelProvider(this)[T::class.java]
    else
        ViewModelProvider(this, BaseViewModelFactory(creator))[T::class.java]
}

inline fun <reified T : ViewModel> Fragment.getActCtxViewModel(noinline creator: (() -> T)? = null): T {
    return if (creator == null)
        ViewModelProvider(requireActivity())[T::class.java]
    else
        ViewModelProvider(requireActivity(), BaseViewModelFactory(creator))[T::class.java]
}
*/

fun Context.showAskingDialog(
    title: Int?,
    text: Int,
    positiveText: Int,
    negativeText: Int,
    theme: Int? = null,
    onClick: () -> Unit?
) =
    showAskingDialog(
        title?.let { getString(it) } ?: "",
        getString(text),
        positiveText,
        negativeText,
        theme,
        onClick
    )

fun Context.showAskingDialog(
    title: String,
    text: String,
    positiveText: Int,
    negativeText: Int,
    theme: Int? = null,
    onClick: () -> Unit?
) {
    val builder = if (theme == null)
        AlertDialog.Builder(this)
    else
        AlertDialog.Builder(this, theme)
    if (title.isNotEmpty())
        builder.setTitle(title)
    builder
        .setMessage(text)
        .setPositiveButton(positiveText) { dialog, _ ->
            onClick.invoke()
            dialog?.dismiss()
        }.setNegativeButton(negativeText) { dialog, _ ->
            dialog?.dismiss()
        }.show()
}

fun Context.showInfoDialog(
    title: Int?,
    text: Int,
    buttonText: Int,
    theme: Int? = null,
    cancelable: Boolean = true,
    onClick: (() -> Unit)? = null
) {
    val builder = if (theme == null)
        AlertDialog.Builder(this)
    else
        AlertDialog.Builder(this, theme)
    if (title != null)
        builder.setTitle(title)
    builder
        .setCancelable(cancelable)
        .setMessage(text)
        .setNeutralButton(buttonText) { dialog, _ ->
            onClick?.invoke()
            dialog?.dismiss()
        }.show()
}

fun Context.showInfoDialog(
    title: Int?,
    text: CharSequence,
    buttonText: Int,
    theme: Int? = null,
    onClick: (() -> Unit)? = null
) {
    val builder = if (theme == null)
        AlertDialog.Builder(this)
    else
        AlertDialog.Builder(this, theme)
    if (title != null)
        builder.setTitle(title)
    builder
        .setMessage(text)
        .setNeutralButton(buttonText) { dialog, _ ->
            onClick?.invoke()
            dialog?.dismiss()
        }.show()
}

fun Context.showListDialog(title: Int?, dataList: Array<String>, onClick: (index: Int) -> Unit?) {
    val builder = AlertDialog.Builder(this)
    if (title != null)
        builder.setTitle(title)
    builder.setItems(dataList) { _, which ->
        onClick.invoke(which)
    }
    builder.create().show()
}

fun Context.showTextInputDialog(title: Int?, theme: Int? = null, callBack: (text: String) -> Unit) {
    val builder = if (theme == null)
        AlertDialog.Builder(this)
    else
        AlertDialog.Builder(this, theme)
    if (title != null)
        builder.setTitle(title)
    val view: View = LayoutInflater.from(this).inflate(R.layout.text_input_layout, null)
    builder
        .setView(view)
        .setPositiveButton(R.string.text_record) { dialog, _ /*which*/ ->
            callBack(view.findViewById<EditText>(R.id.inputTextET).text.toString())
            dialog.dismiss()
        }.show()
}

fun Context.showSoftKeyboard(view: View) {
    if (view.requestFocus()) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }
}

fun Context.hideKeyboard(view: View) {
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

fun MutableList<Order>.getSummedBottleOrders(): List<Order.BottleItem> {
    return this.asSequence()
        .filter {
            it.orderStatus == OrderStatus.ACTIVE
        }
        .map {
            it.bottleItems
        }
        .flatten()
        .groupBy {
            it.bottle.id
        }.map { entry ->
            Order.BottleItem(
                entry.key,
                0,
                entry.value[0].bottle,
                entry.value.sumOf { it.count }
            )
        }
        .toList()
}

fun MutableList<Order>.getSummedRemainingOrder(): List<Order.Item> {
    val sumOrderItems = mutableListOf<Order.Item>()
    val resultList = mutableListOf<Order.Item>()

    this
        .filter {
            it.orderStatus == OrderStatus.ACTIVE
        }
        .forEach {
            sumOrderItems.addAll(it.getRemainingOrderItems())
        }

    sumOrderItems
        .groupBy {
            it.beerID
        }.values.forEach { orderItemList ->
            orderItemList.groupBy { it.canTypeID }.values.forEach { singleList ->
                val summedCount = singleList.sumOf { it.count }
                resultList.add(
                    singleList[0].copy(count = summedCount)
                )
            }
        }
    return resultList
}

val Fragment.baseActivity: MainActivity?
    get() {
        return activity as? MainActivity
    }

fun Fragment.notifyNewComment(text: String) {
    val refToFB = FirebaseDatabase.getInstance().getReference(BuildConfig.FLAVOR)
    val fullText = "${Date()}|$text"
    refToFB.setValue(fullText)
    SharedPreferenceDataSource.initialize(this.requireContext())
    SharedPreferenceDataSource.getInstance().saveLastMsgDate(fullText)
}

fun notifyOldDataChange(logID: String) {
    val refToFB = FirebaseDatabase.getInstance().getReference(BuildConfig.FLAVOR + "_tracker")
    refToFB.setValue(logID)
    SharedPreferenceDataSource.getInstance().saveLastLogID(logID)
}

fun Context.showToast(text: String) {
    Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
}

fun Context.showToast(strRes: Int) {
    showToast(getString(strRes))
}

fun Double.round(decSize: Int = 2): Double {
    return BigDecimal(this).setScale(decSize, RoundingMode.HALF_EVEN).toDouble()
}

fun Context.isNetworkAvailable(): Boolean {
    val connectivityManager =
        this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
    val activeNetworkInfo = connectivityManager?.activeNetworkInfo
    return activeNetworkInfo != null && activeNetworkInfo.isConnected
}

fun Context.getDimenPixelOffset(@DimenRes dimenRes: Int): Int {
    return resources.getDimensionPixelOffset(dimenRes)
}

fun EditText.simpleTextChangeListener(onChange: (value: CharSequence) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(p0: Editable?) {}

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            onChange(p0 ?: "")
        }
    })
}

fun View.getColor(@ColorRes resId: Int): Int {
    return ContextCompat.getColor(context, resId)
}

fun ImageView.setTint(@ColorRes resId: Int) {
    setColorFilter(getColor(resId))
}

infix fun Number.waitFor(block: (() -> Unit)) {
    Handler(Looper.getMainLooper()).postDelayed({
        block()
    }, this.toLong())
}

fun String.setFrictionSize(fontSize: Int, fontColor: Int? = null): SpannableString {
    val sp = SpannableString(this)
    if (this.contains(".")) {
        val startIndex = this.indexOf(ClientDebtFragment.DOT)
        var endIndex = this.indexOf(" ", startIndex)
        if (endIndex == -1) endIndex = this.length
        sp.setSpan(
            AbsoluteSizeSpan(fontSize),
            startIndex,
            endIndex,
            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
        )
        if (fontColor != null)
            sp.setSpan(
                ForegroundColorSpan(fontColor),
                startIndex,
                endIndex,
                Spannable.SPAN_INCLUSIVE_EXCLUSIVE
            )
    }
    if (length > 1 && indexOf("-") == 0)
        sp.setSpan(
            ForegroundColorSpan(Color.RED),
            0,
            length,
            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
        )
    return sp
}

fun TextView.setAmount(value: Double) {
    val frictionSize = (this.textSize * RELATIVE_FRICTION_SIZE).toInt()
    this.text = resources.getString(R.string.format_gel, value)
        .setFrictionSize(frictionSize)
}

fun TextInputLayout.text(): String =
    this.editText?.text.toString()

fun TextInputLayout.setText(text: CharSequence) =
    this.editText?.setText(text)

fun TextInputLayout.setText(resID: Int) =
    this.editText?.setText(resID)


fun <T> SharedFlow<T>.collectLatest(
    lifecycleOwner: LifecycleOwner,
    lifecycleState: Lifecycle.State = Lifecycle.State.RESUMED,
    observeOn: CoroutineContext = Dispatchers.Default,
    collectOn: CoroutineContext = Dispatchers.Main,
    action: suspend (value: T) -> Unit
) {
    lifecycleOwner.apply {
        lifecycleScope.launch(observeOn) {
            repeatOnLifecycle(lifecycleState) {
                this@collectLatest.collectLatest {
                    if (observeOn != collectOn)
                        withContext(collectOn) {
                            action(it)
                        }
                    else
                        action(it)
                }
            }
        }
    }
}

fun <T> StateFlow<T>.collectLatest(
    lifecycleOwner: LifecycleOwner,
    lifecycleState: Lifecycle.State = Lifecycle.State.RESUMED,
    observeOn: CoroutineContext = Dispatchers.Default,
    collectOn: CoroutineContext = Dispatchers.Main,
    action: suspend (value: T) -> Unit
) {
    lifecycleOwner.apply {
        lifecycleScope.launch(observeOn) {
            repeatOnLifecycle(lifecycleState) {
                this@collectLatest.collectLatest {
                    if (observeOn != collectOn)
                        withContext(collectOn) {
                            action(it)
                        }
                    else
                        action(it)
                }
            }
        }
    }
}

fun String?.ifNullOrEmpty(alternative: String): String = if (isNullOrBlank())
    alternative
else
    this

fun Int.asHexColor() = String.format(
    "#%02X%02X%02X",
    Color.red(this),
    Color.green(this),
    Color.blue(this)
)

fun EditText.setDifferText(text: String) {
    if (this.text.toString() != text)
        this.setText(text)
}

fun Double?.mapToString(): String {
    return if (this == null || this < .0)
        String.empty()
    else
        this.toBigDecimal().toPlainString()
}

fun Double?.orZero(): Double = this ?: .0

fun String.Companion.empty() = ""

fun areNotNull(vararg objects: Any?): Boolean =
    objects.all { it != null }

fun String.parseDouble(defaultValue: Double = -1.0): Double = try {
    this.toDouble()
} catch (e: NumberFormatException) {
    defaultValue
}
