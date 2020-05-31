package com.example.beerdistrkt.fragPages.orders.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.beerdistrkt.R
import com.example.beerdistrkt.fragPages.orders.adapter.OrderItemAdapter
import com.example.beerdistrkt.models.Order
import com.example.beerdistrkt.utils.visibleIf
import kotlinx.android.synthetic.main.view_order.view.*

class OrderView  @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), View.OnClickListener {

    init {
        View.inflate(context, R.layout.view_order, this)
        layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    fun fillData(order: Order){
        orderUnitClientNameTv.text = order.client.dasaxeleba
        orderUnitCommentImg.visibleIf(!order.comment.isNullOrEmpty())
        orderUnitCheckImg.visibleIf(order.items.any { it.check == 1 })

        val itemList = order.items.groupBy {
            it.beerID
        }

        orderItemList.layoutManager = LinearLayoutManager(context)
        orderItemList.adapter = OrderItemAdapter(itemList.toSortedMap())
    }

    override fun onClick(v: View?) {

    }
}