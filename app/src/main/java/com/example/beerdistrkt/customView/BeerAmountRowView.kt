package com.example.beerdistrkt.customView

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.widget.ImageViewCompat
import com.example.beerdistrkt.R
import com.example.beerdistrkt.fragPages.sawyobi.models.SimpleBeerRowModel
import com.example.beerdistrkt.utils.visibleIf
import kotlinx.android.synthetic.main.view_order_item.view.*

class BeerAmountRowView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    init {
        View.inflate(context, R.layout.view_order_item, this)
        layoutParams =
            LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    fun setBoldStyle(style: Int) {
        orderItemCan50.boltStyle = style
        orderItemCan30.boltStyle = style
        orderItemCan20.boltStyle = style
        orderItemCan10.boltStyle = style
    }

    fun setData(data: SimpleBeerRowModel) {

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