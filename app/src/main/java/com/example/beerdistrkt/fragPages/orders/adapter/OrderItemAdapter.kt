package com.example.beerdistrkt.fragPages.orders.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.beerdistrkt.fragPages.orders.view.OrderItemView
import com.example.beerdistrkt.models.Order

class OrderItemAdapter(
    private var orderItems: Map<Int, List<Order.Item>> = emptyMap()
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(OrderItemView(parent.context))
    }

    override fun getItemCount(): Int = orderItems.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val itemView = holder.itemView
        if (itemView is OrderItemView) {
            val key = orderItems.keys.elementAt(position)
            orderItems[key]?.let {
                itemView.fillData(it)
            }

        }
    }

    private class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}