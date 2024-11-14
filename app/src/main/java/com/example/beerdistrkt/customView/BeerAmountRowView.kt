package com.example.beerdistrkt.customView

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.core.widget.ImageViewCompat
import com.example.beerdistrkt.R
import com.example.beerdistrkt.databinding.ViewOrderItemBinding
import com.example.beerdistrkt.fragPages.homePage.presentation.HomeFragment
import com.example.beerdistrkt.fragPages.orders.view.CounterLinearProgressView.Companion.BOLD_STYLE_NEGATIVE
import com.example.beerdistrkt.fragPages.sawyobi.models.SimpleBeerRowModel
import com.example.beerdistrkt.utils.visibleIf

class BeerAmountRowView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    var showLiters = false
    var isHomePage = false

    private var binding: ViewOrderItemBinding =
        ViewOrderItemBinding.bind(inflate(context, R.layout.view_order_item, this))

    init {
        layoutParams =
            LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    fun setBoldStyle(style: Int) = with(binding) {
        orderItemCan50.boltStyle = style
        orderItemCan30.boltStyle = style
        orderItemCan20.boltStyle = style
        orderItemCan10.boltStyle = style
    }

    fun setData(data: SimpleBeerRowModel) = with(binding) {
        orderItemCan50.clearData()
        orderItemCan30.clearData()
        orderItemCan20.clearData()
        orderItemCan10.clearData()

        orderItemNameTv.text = data.title

        if (data.title != HomeFragment.emptyBarrelTitle && isHomePage) {
            setBoldStyle(BOLD_STYLE_NEGATIVE)
        }
        if (data.title != HomeFragment.emptyBarrelTitle && showLiters) {
            totalAmountLT.text = resources.getString(R.string.lt, calculateTotalLT(data.values))
            totalAmountLT.isVisible = true
            binding.root.setBackgroundResource(R.drawable.bkg_underline_dots)
        }
        if (data.title == HomeFragment.emptyBarrelTitle) {
            binding.orderItemNameTv.setTypeface(null, Typeface.BOLD)
        }

        data.values.keys.forEach { barrelIndex ->
            when (barrelIndex) {
                1 -> orderItemCan50.setCount(data.values[barrelIndex] ?: 0)
                2 -> orderItemCan30.setCount(data.values[barrelIndex] ?: 0)
                3 -> orderItemCan20.setCount(data.values[barrelIndex] ?: 0)
                4 -> orderItemCan10.setCount(data.values[barrelIndex] ?: 0)
            }
        }

        data.underlineColor?.let {
            orderItemNameUnderline.setBackgroundColor(it)
        }
        orderMiddleIcon.visibleIf(data.middleIconRes != null)
        data.middleIconRes?.let {
            orderMiddleIcon.setImageResource(it)
        }
        data.iconColor?.let {
            ImageViewCompat.setImageTintList(
                orderMiddleIcon,
                ColorStateList.valueOf(resources.getColor(it))
            )
        }
    }

    private fun calculateTotalLT(values: Map<Int, Int>): Int =
        values.entries.sumOf { entry ->
            when (entry.key) {
                1 -> entry.value * 50
                2 -> entry.value * 30
                3 -> entry.value * 20
                4 -> entry.value * 10
                else -> 0
            }
        }
}