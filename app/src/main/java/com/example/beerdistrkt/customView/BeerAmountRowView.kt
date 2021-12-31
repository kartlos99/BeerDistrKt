package com.example.beerdistrkt.customView

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.ImageViewCompat
import com.example.beerdistrkt.R
import com.example.beerdistrkt.databinding.ViewOrderItemBinding
import com.example.beerdistrkt.fragPages.sawyobi.models.SimpleBeerRowModel
import com.example.beerdistrkt.utils.visibleIf

class BeerAmountRowView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var binding: ViewOrderItemBinding =
        ViewOrderItemBinding.bind(inflate(context, R.layout.view_order_item, this))

    init {
        layoutParams =
            LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    fun setBoldStyle(style: Int) {
        with(binding) {
            orderItemCan50.boltStyle = style
            orderItemCan30.boltStyle = style
            orderItemCan20.boltStyle = style
            orderItemCan10.boltStyle = style
        }
    }

    fun setData(data: SimpleBeerRowModel) {
        with(binding) {
            orderItemCan50.clearData()
            orderItemCan30.clearData()
            orderItemCan20.clearData()
            orderItemCan10.clearData()

            orderItemNameTv.text = data.title

            data.values.keys.forEach {
                when (it) {
                    1 -> orderItemCan50.setCount(data.values[it] ?: 0)
                    2 -> orderItemCan30.setCount(data.values[it] ?: 0)
                    3 -> orderItemCan20.setCount(data.values[it] ?: 0)
                    4 -> orderItemCan10.setCount(data.values[it] ?: 0)
                }
            }

            data.underlineColor?.let {
                orderItemNameUnderline.setBackgroundColor(Color.parseColor(it))
            }
            orderMiddleIcon.visibleIf(data.middleIconRes != null)
            data.middleIconRes?.let {
                orderMiddleIcon.setImageResource(it)
            }
            data.iconColor?.let {
                ImageViewCompat.setImageTintList(
                    orderMiddleIcon,
                    ColorStateList.valueOf(resources.getColor(it))
                );
            }
        }
    }

}