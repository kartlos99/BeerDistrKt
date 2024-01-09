package com.example.beerdistrkt.fragPages.orders.view

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.beerdistrkt.R
import com.example.beerdistrkt.databinding.ViewOrderBottleItemBinding
import com.example.beerdistrkt.models.Order

class OrderBottleItemView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding = ViewOrderBottleItemBinding.bind(
        inflate(context, R.layout.view_order_bottle_item, this)
    )

    init {
        layoutParams =
            LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    fun fillData(
        bottleItem: Order.BottleItem,
        bottleSaleItem: Order.BottleSaleItem? = null
    ) = with(binding) {
        bottleName.text = bottleItem.bottle.name
        bottleCount.setCountAndProgress(bottleItem.count, bottleSaleItem?.count ?: 0)
    }

}