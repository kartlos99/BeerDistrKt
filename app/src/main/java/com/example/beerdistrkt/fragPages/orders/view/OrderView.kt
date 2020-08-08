package com.example.beerdistrkt.fragPages.orders.view

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.beerdistrkt.R
import com.example.beerdistrkt.fragPages.orders.adapter.OrderItemAdapter
import com.example.beerdistrkt.models.Order
import com.example.beerdistrkt.models.OrderStatus
import com.example.beerdistrkt.utils.visibleIf
import kotlinx.android.synthetic.main.view_order.view.*

class OrderView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr), View.OnClickListener {

    var commentIsVisible = false
        set(value) {
            orderComment.visibleIf(value)
            field = value
        }

    init {
        View.inflate(context, R.layout.view_order, this)
        layoutParams =
            LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    private fun resetForm(){
        layoutParams =
            LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        orderComment.visibleIf(false)
        orderStatusTv.text = ""
        orderUnitRootSwipe.close(false)
        orderMainConstraint.backgroundTintList = null
    }

    fun lockSwipe(lock: Boolean) {
        orderUnitRootSwipe.close(false)
        orderUnitRootSwipe.setLockDrag(lock)
    }

    fun fillData(order: Order) {
        resetForm()
        orderUnitClientNameTv.text = order.client.dasaxeleba
        orderUnitCommentImg.visibleIf(!order.comment.isNullOrEmpty())
        orderUnitCheckImg.visibleIf(order.items.any { it.check == 1 })
        if (order.orderStatus != OrderStatus.ACTIVE) {
            orderStatusTv.text = resources.getString(order.orderStatus.textRes)
            orderMainConstraint.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.red_01))
        }

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
        orderUnitChangeDistributorBtn.setOnClickListener {
            order.onChangeDistributorClick()
        }
        orderMainConstraint.setOnClickListener {
            order.onItemClick()
        }
        orderItemList.setOnClickListener {
            order.onItemClick()
        }
        orderUnitFrontRoot.postDelayed({
            val lp = orderUnitBackRoot.layoutParams
            lp.height = orderUnitFrontRoot.measuredHeight
            orderUnitBackRoot.layoutParams = lp
        }, 50)
    }

    override fun onClick(v: View?) {

    }
}