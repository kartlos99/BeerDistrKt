package com.example.beerdistrkt.fragPages.orders.view

import android.content.Context
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.beerdistrkt.R
import kotlinx.android.synthetic.main.view_counter_linear_progress.view.*
import kotlin.math.sign

class CounterLinearProgressView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    var boltStyle = BOLD_STYLE_ALL

    init {
        View.inflate(context, R.layout.view_counter_linear_progress, this)

        viewProgress.progress = 0
        countTv.text = ""
    }

    fun setCountAndProgress(count: Int, progress: Int = 0) {
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

    fun setCount(count: Int) {
        countTv.text = count.toString()
        countTv.setTextColor(resources.getColor(R.color.colorForText))
        updateBoldStyle(count)
    }

    private fun updateBoldStyle(number: Int) {
        if (boltStyle.sign == number.sign) countTv.setTypeface(null, Typeface.BOLD)
        when (boltStyle) {
            BOLD_STYLE_ALL -> countTv.setTypeface(null, Typeface.BOLD)
            BOLD_STYLE_NON_NEGATIVE,
            BOLD_STYLE_NON_POSITIVE -> {
                if (number == 0) countTv.setTypeface(null, Typeface.BOLD)
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