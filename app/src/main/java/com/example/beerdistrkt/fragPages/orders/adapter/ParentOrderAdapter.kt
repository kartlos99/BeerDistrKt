package com.example.beerdistrkt.fragPages.orders.adapter

import com.example.beerdistrkt.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.RecycledViewPool
import com.example.beerdistrkt.fragPages.orders.models.OrderGroupModel
import com.example.beerdistrkt.models.Order
import com.example.beerdistrkt.utils.visibleIf
import kotlinx.android.synthetic.main.view_order_group.view.*


class ParentOrderAdapter(
    private var orderGroups: MutableList<OrderGroupModel> = mutableListOf()
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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
            val itemList = grItem.getSummedOrder().groupBy {
                it.beerID
            }.toMutableMap()
            val salesList = grItem.getSummedSales().groupBy {
                it.beerID
            }
            salesList.forEach {
                if (!itemList.contains(it.key))
                    itemList[it.key] = emptyList()
            }

            holder.itemView.viewOrderGroupSumRecycler.layoutManager =
                LinearLayoutManager(holder.itemView.viewOrderGroupSumRecycler.context)
            holder.itemView.viewOrderGroupSumRecycler.adapter =
                OrderItemAdapter(itemList.toSortedMap(), salesList)

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

            holder.itemView.viewOrderGroupRecycler.layoutManager = layoutManager
            holder.itemView.viewOrderGroupRecycler.adapter = subOrderAdapter
            holder.itemView.viewOrderGroupRecycler.setRecycledViewPool(viewPool)
        }
    }

    fun setData(data: MutableList<OrderGroupModel>) {
        orderGroups = data
        notifyDataSetChanged()
    }

    fun removeItem(indexes: Pair<Int, Int>) {
        orderGroups[indexes.first].orderAdapter?.removeItem(indexes.second)
        orderGroups[indexes.first].orderAdapter?.notifyItemRemoved(indexes.second)
        notifyItemChanged(indexes.first)
    }

    private class ParentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

//    fun initSummedRecycler()
}