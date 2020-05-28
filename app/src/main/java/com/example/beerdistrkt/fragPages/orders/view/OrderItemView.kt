package com.example.beerdistrkt.fragPages.orders.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.beerdistrkt.R
import com.example.beerdistrkt.models.Order
import kotlinx.android.synthetic.main.view_order_item.view.*

class OrderItemView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    init {
        View.inflate(context, R.layout.view_order_item, this)
        layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    fun fillData(orderItems: List<Order.Item>) {

        orderItemNameTv.text = orderItems[0].beerID.toString()
        orderItems.forEach {
            when (it.canTypeID) {
                1 -> orderItemCan50.setCountAndProgress(it.count, it.check)
                2 -> orderItemCan30.setCountAndProgress(it.count, it.check)
                3 -> orderItemCan20.setCountAndProgress(it.count, it.check)
                4 -> orderItemCan10.setCountAndProgress(it.count, it.check)
            }
        }

    }

}