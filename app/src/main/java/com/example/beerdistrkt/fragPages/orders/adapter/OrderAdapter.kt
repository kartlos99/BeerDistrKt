package com.example.beerdistrkt.fragPages.orders.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.beerdistrkt.fragPages.orders.view.OrderView
import com.example.beerdistrkt.models.Order

class OrderAdapter(
    private var orders: List<Order> = emptyList()
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(OrderView(parent.context))
    }

    override fun getItemCount(): Int = orders.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val itemView = holder.itemView
        if (itemView is OrderView)
            itemView.fillData(orders[position])
    }

    fun setData(orders: List<Order>){
        this.orders = orders
        notifyDataSetChanged()
    }

    private class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}