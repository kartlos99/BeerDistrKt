package com.example.beerdistrkt.customView

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.example.beerdistrkt.R
import kotlinx.android.synthetic.main.view_order_item.view.*

class BeerAmountRowView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    init {
        View.inflate(context, R.layout.view_order_item, this)
        layoutParams =
            LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    fun setData(rowTitle: String, amounts: Map<Int, Int>){

        orderItemNameTv.text = rowTitle

        amounts.keys.forEach {
            when (it) {
                1 -> orderItemCan50.setCount(amounts[it] ?: 0)
                2 -> orderItemCan30.setCount(amounts[it] ?: 0)
                3 -> orderItemCan20.setCount(amounts[it] ?: 0)
                4 -> orderItemCan10.setCount(amounts[it] ?: 0)
            }
        }
//        orderItemCan50.setCount(amounts[0])
//        orderItemCan30.setCount(amounts[1])
//        orderItemCan20.setCount(amounts[2])
//        orderItemCan10.setCount(amounts[3])
    }

}