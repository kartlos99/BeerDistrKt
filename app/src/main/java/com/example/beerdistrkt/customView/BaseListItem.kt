package com.example.beerdistrkt.customView

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes

abstract class BaseListItem : FrameLayout {

    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        @AttrRes defStyleAttr: Int = 0
    ) : super(context, attrs, defStyleAttr) {
//        initLayout(context, attrs)
    }

    constructor(
        context: Context,
        attrs: AttributeSet?,
        @AttrRes defStyleAttr: Int,
        @StyleRes defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
//        initLayout(context, attrs)
    }


    abstract fun setLabel(label: String)

    abstract fun setRowItemClickListener(listener: View.OnClickListener?)

    open fun enable() {
        isEnabled = true
    }

    open fun disable() {
        isEnabled = false
    }
}