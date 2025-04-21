package com.example.beerdistrkt.customView

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.LinearLayout
import com.example.beerdistrkt.R
import com.example.beerdistrkt.databinding.NumericEdittextViewBinding

class NumericEditText
@JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val maxValue = 9999
    private val minValue = 0
    private var binding = NumericEdittextViewBinding.bind(
        inflate(context, R.layout.numeric_edittext_view, this)
    )

    var amount: Int = 0
        set(value) {
            binding.editCount.setText(value.toString())
            field = value
        }
        get() {
            val etValue = binding.editCount.text.toString()
            return if (etValue.isEmpty())
                0
            else
                etValue.toInt()
        }

    init {
        initView()
    }

    private fun initView() {


        binding.btnInc.setOnClickListener {
            if (amount < maxValue)
                amount++
        }
        binding.btnDec.setOnClickListener {
            if (amount > minValue)
                amount--
        }
    }

    fun getEditTextView(): EditText = binding.editCount

}