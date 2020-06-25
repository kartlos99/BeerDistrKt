package com.example.beerdistrkt.fragPages.orders.view

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.beerdistrkt.R
import com.example.beerdistrkt.fragPages.orders.adapter.OrderItemAdapter
import com.example.beerdistrkt.models.Order
import com.example.beerdistrkt.models.OrderStatus
import com.example.beerdistrkt.utils.visibleIf
import kotlinx.android.synthetic.main.view_order.view.*

class OrderView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), View.OnClickListener {

    var commentIsVisible = false
        set(value) {
            orderComment.visibleIf(value)
            field = value
        }

    init {
        View.inflate(context, R.layout.view_order, this)
        layoutParams =
            LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        Log.d("newViewCreated", "init")
    }

    private fun resetForm(){
        layoutParams =
            LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        orderComment.visibleIf(false)
        orderStatusTv.text = ""
    }

    fun fillData(order: Order) {
        resetForm()
        orderUnitClientNameTv.text = order.client.dasaxeleba
        orderUnitCommentImg.visibleIf(!order.comment.isNullOrEmpty())
        orderUnitCheckImg.visibleIf(order.items.any { it.check == 1 })
        if (order.orderStatus != OrderStatus.ACTIVE)
            orderStatusTv.text = resources.getString(order.orderStatus.textRes)

        val itemList = order.items.groupBy {
            it.beerID
        }.toMutableMap()
        val salesList = order.sales.groupBy {
            it.beerID
        }
        salesList.forEach {
            if (!itemList.contains(it.key))
                itemList[it.key] = emptyList()
        }

        orderItemList.layoutManager = LinearLayoutManager(context)
        orderItemList.adapter = OrderItemAdapter(itemList.toSortedMap(), salesList)

        orderComment.text = order.comment ?: ""

        orderUnitCommentImg.setOnClickListener {
            if (!order.comment.isNullOrEmpty())
                commentIsVisible = !commentIsVisible
        }
        orderUnitEditBtn.setOnClickListener {
            order.onEditClick()
        }
        orderUnitDeleteBtn.setOnClickListener {
            order.onDeleteClick()
        }
        layoutParams =
            LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun onClick(v: View?) {

    }
}