package com.example.beerdistrkt.fragPages.orders.view

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.beerdistrkt.R
import kotlinx.android.synthetic.main.view_counter_linear_progress.view.*

class CounterLinearProgressView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    init {
        View.inflate(context, R.layout.view_counter_linear_progress, this)

        viewProgress.progress = 0
        countTv.text = ""
    }

    fun setCountAndProgress(count: Int, progress: Int = 0) {
        val combText = "$count/$progress"
        val spanText = SpannableString(combText)
        spanText.setSpan(
            ForegroundColorSpan(resources.getColor(R.color.colorForText)),
            0, combText.indexOf("/"), Spannable.SPAN_INCLUSIVE_EXCLUSIVE
        )
        countTv.text = spanText
        viewProgress.max = count
        viewProgress.progress = progress
        if (count > 0)
            viewProgress.setBackgroundResource(R.color.in_complete_order)
        else
            viewProgress.setBackgroundResource(R.color.warning)
    }

    fun setCount(count: Int){
        countTv.text = count.toString()
        countTv.setTextColor(resources.getColor(R.color.colorForText))
    }
}