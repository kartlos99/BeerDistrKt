package com.example.beerdistrkt.fragPages.orders.view

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.chauthai.swipereveallayout.SwipeRevealLayout
import com.example.beerdistrkt.R
import com.example.beerdistrkt.databinding.ViewOrderBinding
import com.example.beerdistrkt.fragPages.orders.adapter.OrderItemAdapter
import com.example.beerdistrkt.models.Order
import com.example.beerdistrkt.models.OrderStatus
import com.example.beerdistrkt.showToast
import com.example.beerdistrkt.utils.visibleIf

class OrderView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr), View.OnClickListener {

    private var binding: ViewOrderBinding

    var commentIsVisible = false
        set(value) {
            binding.orderComment.visibleIf(value)
            field = value
        }

    var order: Order? = null
        set(value) {
            if (value != null)
                fillData(value)
            field = value
        }

    init {
        binding = ViewOrderBinding.bind(View.inflate(context, R.layout.view_order, this))
        layoutParams =
            LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        with(binding) {
            orderUnitHistoryImg.setOnClickListener(this@OrderView)
            orderUnitCommentImg.setOnClickListener(this@OrderView)
            orderUnitChangeDistributorBtn.setOnClickListener(this@OrderView)
            orderUnitEditBtn.setOnClickListener(this@OrderView)
            orderUnitDeleteBtn.setOnClickListener(this@OrderView)
            orderMainConstraint.setOnClickListener(this@OrderView)
            orderItemList.setOnClickListener(this@OrderView)
        }
    }

    private fun resetForm() {

        with(binding) {
            layoutParams =
                LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            orderComment.visibleIf(false)
            orderStatusTv.text = ""
            orderUnitRootSwipe.close(false)
            orderMainConstraint.backgroundTintList = null
        }
    }

    fun lockSwipe(lock: Boolean) {
        with(binding) {
            orderUnitRootSwipe.close(false)
            orderUnitRootSwipe.setLockDrag(lock)
        }
    }

    private fun fillData(order: Order) {
        resetForm()
        with(binding) {
            orderUnitClientNameTv.text = order.client.dasaxeleba.uppercase()
            orderUnitHistoryImg.visibleIf(order.isEdited > 0)
            orderUnitCommentImg.visibleIf(!order.comment.isNullOrEmpty())
            orderUnitCheckImg.visibleIf(order.items.any { it.check == 1 })
            if (order.needCleaning == 1) {
                orderStatusTv.text = resources.getString(R.string.need_cleaning, order.passDays)
                orderStatusTv.setTextColor(Color.parseColor("#FFA6A6"))
            }
            if (order.orderStatus != OrderStatus.ACTIVE) {
                if (order.orderStatus != OrderStatus.COMPLETED) {
                    orderStatusTv.text = resources.getString(order.orderStatus.textRes)
                    orderStatusTv.setTextColor(Color.WHITE)
                }
                orderMainConstraint.backgroundTintList =
                    ColorStateList.valueOf(resources.getColor(R.color.red_01))
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

            orderUnitFrontRoot.postDelayed({
                val lp = orderUnitBackRoot.layoutParams
                lp.height = orderUnitFrontRoot.measuredHeight
                orderUnitBackRoot.layoutParams = lp
            }, 50)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.orderUnitHistoryImg -> order?.onHistoryClick?.invoke()
            R.id.orderUnitCommentImg -> if (!order?.comment.isNullOrEmpty())
                commentIsVisible = !commentIsVisible
            R.id.orderUnitChangeDistributorBtn -> order?.onChangeDistributorClick?.invoke()
            R.id.orderUnitEditBtn -> order?.onEditClick?.invoke()
            R.id.orderUnitDeleteBtn ->
                if (order?.orderStatus != OrderStatus.DELETED)
                    order?.onDeleteClick?.invoke()
                else {
                    context.showToast(R.string.deleted)
                    binding.orderUnitRootSwipe.close(true)
                }
            R.id.orderMainConstraint,
            R.id.orderItemList -> order?.onItemClick?.invoke()
        }
    }
}