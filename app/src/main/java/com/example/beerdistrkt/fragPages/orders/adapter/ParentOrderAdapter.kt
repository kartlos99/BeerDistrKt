package com.example.beerdistrkt.fragPages.orders.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.RecycledViewPool
import com.example.beerdistrkt.R
import com.example.beerdistrkt.databinding.ViewOrderGroupBinding
import com.example.beerdistrkt.databinding.ViewOrderGroupBottomItemBinding
import com.example.beerdistrkt.fragPages.orders.models.OrderGroupModel
import com.example.beerdistrkt.getSummedRemainingOrder
import com.example.beerdistrkt.models.CanModel
import com.example.beerdistrkt.models.Order
import com.example.beerdistrkt.utils.visibleIf
import java.util.*


class ParentOrderAdapter(
    private var orderGroups: MutableList<OrderGroupModel>,
    private val barrelsList: List<CanModel>,
    private val onGroupExpand: (dID: Int, state: Boolean) -> Unit
) : RecyclerView.Adapter<ParentOrderAdapter.ParentItemHolder>() {

    var onOrderDrag: ((orderID: Int, newSortValue: Double) -> Unit)? = null
    var onMitanaClick: View.OnClickListener? = null
    var deliveryMode = false

    private val barrelMap = barrelsList.groupBy { it.id }

    private val viewPool = RecycledViewPool()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParentItemHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            BOTTOM_ITEM -> ParentItemHolder.ParentBottomSumViewHolder(
                ViewOrderGroupBottomItemBinding.inflate(inflater, parent, false)
            )
            else -> ParentItemHolder.ParentViewHolder(
                ViewOrderGroupBinding.inflate(
                    inflater,
                    parent,
                    false
                )
            )
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == orderGroups.size)
            BOTTOM_ITEM
        else
            MAIN_ORDER_ITEM
    }

    override fun getItemCount(): Int {
        return orderGroups.size + 1
    }

    override fun onBindViewHolder(holder: ParentItemHolder, position: Int) {

        when (holder) {
            is ParentItemHolder.ParentBottomSumViewHolder -> bindBottomItem(holder)
            is ParentItemHolder.ParentViewHolder -> with(holder.binding) {

                orderGroups[position].let { grItem ->

                    viewOrderGroupDistributor.text = grItem.distributorName
                    val itemsList = grItem.ordersList.getSummedRemainingOrder()

                    var literSum = 0
                    itemsList.forEach {
                        literSum += it.count * (barrelMap[it.canTypeID]?.get(0)?.volume ?: 0)
                    }
                    viewOrderGroupTotalLt.text =
                        holder.itemView.context.getString(R.string.lt, literSum)

                    val groupedItemsList = itemsList.groupBy {
                        it.beerID
                    }.toMutableMap()

                    viewOrderGroupSumRecycler.layoutManager =
                        LinearLayoutManager(viewOrderGroupSumRecycler.context)
                    viewOrderGroupSumRecycler.adapter =
                        OrderItemAdapter(groupedItemsList.toSortedMap())

                    viewOrderGroupTitle.setOnClickListener {
                        grItem.isExpanded = !grItem.isExpanded
                        setGrItemState(this, grItem.isExpanded)
                        onGroupExpand.invoke(grItem.distributorID, grItem.isExpanded)
                    }

                    setGrItemState(this, grItem.isExpanded)

                    // Create layout manager with initial prefetch item count
                    val layoutManager = LinearLayoutManager(
                        viewOrderGroupRecycler.context,
                        LinearLayoutManager.VERTICAL,
                        false
                    )
                    layoutManager.initialPrefetchItemCount = grItem.ordersList.size

                    // Create sub item view adapter
                    val subOrderAdapter = OrderAdapter(grItem.ordersList)
                    subOrderAdapter.setMode(deliveryMode)
                    orderGroups[position].orderAdapter = subOrderAdapter

                    var dragStartPosition = -1
                    Log.d("drag size1", "${grItem.ordersList.size}")

                    var draggingOrder: Order? = null

                    var newSortValue: Double? = null
                    var orderBkgColor: ColorStateList? = null

                    val touchHelper = ItemTouchHelper(object : ItemTouchHelper.Callback() {

                        override fun getMovementFlags(
                            recyclerView: RecyclerView,
                            viewHolder: RecyclerView.ViewHolder
                        ): Int {
                            return makeMovementFlags(ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0)
                        }

                        override fun onMove(
                            recyclerView: RecyclerView,
                            sourceVH: RecyclerView.ViewHolder,
                            targetVH: RecyclerView.ViewHolder
                        ): Boolean {
                            val sourcePosition = sourceVH.adapterPosition
                            val targetPosition = targetVH.adapterPosition
                            Log.d("drag size2", "${grItem.ordersList.size}")

                            newSortValue = calculateSortValue(sourcePosition, targetPosition)

                            Collections.swap(grItem.ordersList, sourcePosition, targetPosition)
                            subOrderAdapter.notifyItemMoved(sourcePosition, targetPosition)
                            Log.d("drag", "$sourcePosition to $targetPosition")
                            return false
                        }

                        override fun clearView(
                            recyclerView: RecyclerView,
                            viewHolder: RecyclerView.ViewHolder
                        ) {
                            super.clearView(recyclerView, viewHolder)
                            Log.d("drag posFin", "${viewHolder.adapterPosition}")
                            val dragFinalPosition = viewHolder.adapterPosition
                            viewHolder.itemView.findViewById<ConstraintLayout>(R.id.orderMainConstraint)
                                ?.backgroundTintList = orderBkgColor

                            if (dragStartPosition != dragFinalPosition && dragStartPosition != -1) {
                                if (draggingOrder != null && newSortValue != null) {
                                    onOrderDrag?.invoke(draggingOrder!!.ID, newSortValue!!)
                                    draggingOrder!!.sortValue = newSortValue!!
                                }
                            }
                        }

                        override fun onSelectedChanged(
                            viewHolder: RecyclerView.ViewHolder?,
                            actionState: Int
                        ) {
                            super.onSelectedChanged(viewHolder, actionState)
                            if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
                                val bkgView =
                                    viewHolder?.itemView?.findViewById<ConstraintLayout>(R.id.orderMainConstraint)
                                orderBkgColor = bkgView?.backgroundTintList
                                bkgView?.backgroundTintList =
                                    ColorStateList.valueOf(Color.parseColor("#FF9696"))
                                dragStartPosition = viewHolder?.adapterPosition ?: -1
                                if (dragStartPosition >= 0)
                                    draggingOrder = grItem.ordersList[dragStartPosition]
                                Log.d("drag posStart", "$dragStartPosition")
                            }
                        }

                        fun calculateSortValue(sourcePosition: Int, targetPosition: Int): Double {
                            var newValue = .0
                            if (sourcePosition < targetPosition) {
                                newValue =
                                    if (targetPosition == grItem.ordersList.size - 1) {
                                        grItem.ordersList[targetPosition].sortValue.plus(1.0)
                                    } else {
                                        listOf(
                                            grItem.ordersList[targetPosition].sortValue,
                                            grItem.ordersList[targetPosition + 1].sortValue
                                        ).average()
                                    }
                            }
                            if (sourcePosition > targetPosition) {
                                newValue =
                                    if (targetPosition == 0) {
                                        grItem.ordersList[targetPosition].sortValue.minus(1.0)
                                    } else {
                                        listOf(
                                            grItem.ordersList[targetPosition].sortValue,
                                            grItem.ordersList[targetPosition - 1].sortValue
                                        ).average()
                                    }
                            }
                            return newValue
                        }

                        override fun onSwiped(
                            viewHolder: RecyclerView.ViewHolder,
                            direction: Int
                        ) {
                        }

                        override fun isLongPressDragEnabled(): Boolean {
                            return true
                        }
                    })

//                if (Session.get().userType == UserType.ADMIN
//                    || Session.get().userType == UserType.MANAGER
//                    || Session.get().userID == grItem.distributorID.toString()
//                )
                    touchHelper.attachToRecyclerView(viewOrderGroupRecycler)

                    viewOrderGroupRecycler.layoutManager = layoutManager
                    viewOrderGroupRecycler.adapter = subOrderAdapter
                    viewOrderGroupRecycler.setRecycledViewPool(viewPool)
                }
            }
        }
    }

    private fun bindBottomItem(holder: ParentItemHolder.ParentBottomSumViewHolder) {
        with(holder.binding) {
            addDeliveryBtn.setOnClickListener(onMitanaClick)

            val allOrders = mutableListOf<Order>()
            orderGroups.forEach {
                allOrders.addAll(it.ordersList)
            }
            val remainingOrderSum = allOrders.getSummedRemainingOrder()
            var litraji = 0
//        val barrelMap = barrelsList.groupBy { it.id }
            remainingOrderSum.forEach {
                litraji += it.count * (barrelMap[it.canTypeID]?.get(0)?.volume ?: 0)
            }
            val itemList = remainingOrderSum.groupBy {
                it.beerID
            }.toMutableMap()

            totalOrderTitle.text = "შეკვეთების ჯამი\nსაერთო ლიტრაჟი: $litraji"

            totalSummedOrderRecycler.layoutManager =
                LinearLayoutManager(totalSummedOrderRecycler.context)
            totalSummedOrderRecycler.adapter =
                OrderItemAdapter(itemList.toSortedMap())

            totalSummedOrderRecycler.visibleIf(!deliveryMode)
            totalOrderTitle.visibleIf(!deliveryMode)
        }
    }

    private fun setGrItemState(binding: ViewOrderGroupBinding, isExpanded: Boolean) {
        binding.viewOrderGroupRecycler.visibleIf(isExpanded)
        binding.viewOrderGroupCollapseImg.rotation = if (isExpanded) 180f else 0f
    }

    fun setData(data: MutableList<OrderGroupModel>) {
        // ase ganaxlebis Semdeg drag-i urevda da amovige
        orderGroups = data
        notifyDataSetChanged()
    }

    fun removeItem(indexes: Pair<Int, Int>) {
        orderGroups[indexes.first].orderAdapter?.removeItem(indexes.second)
        notifyItemChanged(indexes.first)
    }

    fun updateLastItem(mode: Boolean) {
        deliveryMode = mode
        notifyDataSetChanged()
    }


    sealed class ParentItemHolder(view: View) : RecyclerView.ViewHolder(view) {
        class ParentViewHolder(val binding: ViewOrderGroupBinding) : ParentItemHolder(binding.root)

        class ParentBottomSumViewHolder(val binding: ViewOrderGroupBottomItemBinding) :
            ParentItemHolder(binding.root)
    }


    companion object {
        const val MAIN_ORDER_ITEM = 1
        const val BOTTOM_ITEM = 2
    }
}