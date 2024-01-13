package com.example.beerdistrkt.fragPages.orders.view

import android.content.Context
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.beerdistrkt.R
import com.example.beerdistrkt.databinding.ViewCounterLinearProgressBinding
import kotlin.math.sign

class CounterLinearProgressWideView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var binding: ViewCounterLinearProgressBinding = ViewCounterLinearProgressBinding.bind(
        inflate(context, R.layout.view_counter_linear_progress_wide, this)
    )
    var boltStyle = BOLD_STYLE_ALL

    init {
        binding.viewProgress.progress = 0
        binding.countTv.text = ""
    }

    fun setCountAndProgress(count: Int, progress: Int = 0) {
        with(binding) {
            if (progress > 0) {
                val combText = "$count/$progress"
                val spanText = SpannableString(combText)
                spanText.setSpan(
                    ForegroundColorSpan(resources.getColor(R.color.colorForText)),
                    0, combText.indexOf("/"), Spannable.SPAN_INCLUSIVE_EXCLUSIVE
                )
                countTv.text = spanText
            } else {
                val spanText = SpannableString("$count")
                spanText.setSpan(
                    ForegroundColorSpan(resources.getColor(R.color.colorForText)),
                    0, spanText.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE
                )
                countTv.text = spanText
            }
            viewProgress.max = count
            viewProgress.progress = progress
            if (count > 0)
                viewProgress.setBackgroundResource(R.color.in_complete_order)
            else
                viewProgress.setBackgroundResource(R.color.warning)
            updateBoldStyle(count)
        }
    }

    fun setCount(count: Int) = with(binding) {
        countTv.text = count.toString()
        countTv.setTextColor(resources.getColor(R.color.colorForText))
        updateBoldStyle(count)
    }

    fun clearData() = with(binding) {
        countTv.text = ""
        viewProgress.progress = 0
        viewProgress.max = 1
        viewProgress.setBackgroundResource(R.color.gray_light)
    }

    private fun updateBoldStyle(number: Int) {
        if (boltStyle.sign == number.sign) binding.countTv.setTypeface(null, Typeface.BOLD)
        when (boltStyle) {
            BOLD_STYLE_ALL -> binding.countTv.setTypeface(null, Typeface.BOLD)
            BOLD_STYLE_NON_NEGATIVE,
            BOLD_STYLE_NON_POSITIVE -> {
                if (number == 0) binding.countTv.setTypeface(null, Typeface.BOLD)
            }
        }
    }

    companion object {
        const val BOLD_STYLE_ALL = 0
        const val BOLD_STYLE_POSITIVE = 1
        const val BOLD_STYLE_NEGATIVE = -1
        const val BOLD_STYLE_NON_POSITIVE = -2
        const val BOLD_STYLE_NON_NEGATIVE = 2
    }
}