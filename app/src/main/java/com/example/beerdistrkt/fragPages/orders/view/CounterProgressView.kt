package com.example.beerdistrkt.fragPages.orders.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.beerdistrkt.R
import kotlinx.android.synthetic.main.view_counter_progress.view.*

class CounterProgressView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    init {
        View.inflate(context, R.layout.view_counter_progress, this)

        viewProgress.progress = 0
        countTv.text = ""
    }

    fun setCountAndProgress(count: Int, progress: Int = 0){
        countTv.text = count.toString()
        viewProgress.max = count
        viewProgress.progress = progress
        viewProgress.visibility = View.VISIBLE
    }

}