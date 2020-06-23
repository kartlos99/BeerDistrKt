package com.example.beerdistrkt.fragPages.orders.view

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.example.beerdistrkt.R
import com.example.beerdistrkt.models.Order
import kotlinx.android.synthetic.main.view_order_item.view.*

class OrderItemView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    init {
        View.inflate(context, R.layout.view_order_item, this)
        layoutParams =
            LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    fun fillData(
        orderItems: List<Order.Item>,
        salesOfThisBeer: MutableList<Order.Sales>? = null
    ) {

        orderItemNameTv.text = orderItems[0].beer.dasaxeleba ?: ""
        orderItemNameUnderline.setBackgroundColor(
            Color.parseColor(
                orderItems[0].beer.displayColor ?: "#444"
            )
        )
        orderItems.forEach {
            val saleItem = salesOfThisBeer?.find { sItem ->
                sItem.canTypeID == it.canTypeID
            }

            when (it.canTypeID) {
                1 -> orderItemCan50.setCountAndProgress(it.count, saleItem?.count ?: 0)
                2 -> orderItemCan30.setCountAndProgress(it.count, saleItem?.count ?: 0)
                3 -> orderItemCan20.setCountAndProgress(it.count, saleItem?.count ?: 0)
                4 -> orderItemCan10.setCountAndProgress(it.count, saleItem?.count ?: 0)
            }

            if (saleItem != null)
                salesOfThisBeer.remove(saleItem)
        }

        salesOfThisBeer?.forEach {
            when (it.canTypeID) {
                1 -> orderItemCan50.setCountAndProgress(0, it.count)
                2 -> orderItemCan30.setCountAndProgress(0, it.count)
                3 -> orderItemCan20.setCountAndProgress(0, it.count)
                4 -> orderItemCan10.setCountAndProgress(0, it.count)
            }
        }
    }

}