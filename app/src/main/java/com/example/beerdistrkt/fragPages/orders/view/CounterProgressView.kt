package com.example.beerdistrkt.fragPages.orders.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.beerdistrkt.R
import com.example.beerdistrkt.databinding.ViewCounterProgressBinding

class CounterProgressView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding = ViewCounterProgressBinding.bind(
        View.inflate(context, R.layout.view_counter_progress, this)
    )

    init {

        with(binding) {
            viewProgress.progress = 0
            countTv.text = ""
        }
    }

    fun setCountAndProgress(count: Int, progress: Int = 0) {
        with(binding) {
            countTv.text = count.toString()
            viewProgress.max = count
            viewProgress.progress = progress
            viewProgress.visibility = View.VISIBLE
        }
    }

}