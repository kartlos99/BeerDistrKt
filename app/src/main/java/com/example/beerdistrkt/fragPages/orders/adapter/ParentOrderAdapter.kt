package com.example.beerdistrkt.fragPages.orders.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.RecycledViewPool
import com.example.beerdistrkt.R
import com.example.beerdistrkt.fragPages.orders.models.OrderGroupModel
import com.example.beerdistrkt.models.Order
import com.example.beerdistrkt.utils.Session
import com.example.beerdistrkt.utils.UserType
import com.example.beerdistrkt.utils.visibleIf
import kotlinx.android.synthetic.main.view_order_group.view.*
import java.util.*


class ParentOrderAdapter(
    private var orderGroups: MutableList<OrderGroupModel>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var onOrderDrag: ((orderID: Int, newSortValue: Double) -> Unit)? = null

    private val viewPool = RecycledViewPool()
//    private var ordersMap = orders.groupBy { it.distributorID }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.view_order_group, parent, false)
        return ParentViewHolder(view)
    }

    override fun getItemCount(): Int {
        return orderGroups.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        orderGroups[position].let { grItem ->

            holder.itemView.viewOrderGroupDistributor.text = grItem.distributorName
            val itemList = grItem.getSummedRemainingOrder().groupBy {
                it.beerID
            }.toMutableMap()

            holder.itemView.viewOrderGroupSumRecycler.layoutManager =
                LinearLayoutManager(holder.itemView.viewOrderGroupSumRecycler.context)
            holder.itemView.viewOrderGroupSumRecycler.adapter =
                OrderItemAdapter(itemList.toSortedMap())

            holder.itemView.viewOrderGroupTitle.setOnClickListener {
                grItem.isExpanded = !grItem.isExpanded
                holder.itemView.viewOrderGroupRecycler.visibleIf(grItem.isExpanded)
                holder.itemView.viewOrderGroupCollapseImg.rotation =
                    if (grItem.isExpanded) 180f else 0f
            }

            // Create layout manager with initial prefetch item count
            val layoutManager = LinearLayoutManager(
                holder.itemView.viewOrderGroupRecycler.context,
                LinearLayoutManager.VERTICAL,
                false
            )
            layoutManager.initialPrefetchItemCount = grItem.ordersList.size

            // Create sub item view adapter
            val subOrderAdapter = OrderAdapter(grItem.ordersList)
            orderGroups[position].orderAdapter = subOrderAdapter

            var dragStartPosition = -1
            Log.d("drag size1", "${grItem.ordersList.size}")

            var draggingOrder: Order? = null

            var newSortValue: Double? = null

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

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}

                override fun isLongPressDragEnabled(): Boolean {
                    return true
                }
            })

            if (Session.get().userType == UserType.ADMIN || Session.get().userID == grItem.distributorID.toString())
                touchHelper.attachToRecyclerView(holder.itemView.viewOrderGroupRecycler)

            holder.itemView.viewOrderGroupRecycler.layoutManager = layoutManager
            holder.itemView.viewOrderGroupRecycler.adapter = subOrderAdapter
            holder.itemView.viewOrderGroupRecycler.setRecycledViewPool(viewPool)
        }
    }

    fun setData(data: MutableList<OrderGroupModel>) {
        // ase ganaxlebis Semdeg drag-i urevda da amovige
        orderGroups = data
        notifyDataSetChanged()
    }

    fun removeItem(indexes: Pair<Int, Int>) {
        orderGroups[indexes.first].orderAdapter?.removeItem(indexes.second)
        orderGroups[indexes.first].orderAdapter?.notifyItemRemoved(indexes.second)
//        notifyItemChanged(indexes.first)
    }

    private class ParentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

//    fun initSummedRecycler()
}