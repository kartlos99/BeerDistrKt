package com.example.beerdistrkt.customView

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.example.beerdistrkt.R
import kotlinx.android.synthetic.main.numeric_edittext_view.view.*

class NumericEditText
@JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val maxValue = 999
    private val minValue = 0

    var amount: Int = 0
        set(value) {
            editCount.setText(value.toString())
            field = value
        }
        get() {
            val etValue = editCount.text.toString()
            return if (etValue.isEmpty())
                0
            else
                etValue.toInt()
        }

    init {
        initView()
    }

    private fun initView() {
        View.inflate(context, R.layout.numeric_edittext_view, this)

        btnInc.setOnClickListener {
            if (amount < maxValue)
                amount++
        }
        btnDec.setOnClickListener {
            if (amount > minValue)
                amount--
        }
    }

}