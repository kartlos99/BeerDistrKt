package com.example.beerdistrkt.fragPages.orders.view

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.beerdistrkt.R
import com.example.beerdistrkt.databinding.ViewOrderItemBinding
import com.example.beerdistrkt.models.Order

class OrderItemView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding = ViewOrderItemBinding.bind(
        inflate(context, R.layout.view_order_item, this)
    )

    init {
        layoutParams =
            LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    fun fillData(
        orderItems: List<Order.Item>,
        salesOfThisBeer: MutableList<Order.Sales>? = null
    ) {
        val thisBeer = if (orderItems.isNotEmpty())
            orderItems[0].beer
        else
            salesOfThisBeer!![0].beer

        with(binding) {
            orderItemNameTv.text = thisBeer.name
            orderItemNameUnderline.setBackgroundColor(thisBeer.displayColor)
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

}