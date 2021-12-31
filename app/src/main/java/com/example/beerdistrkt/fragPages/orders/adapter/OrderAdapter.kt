package com.example.beerdistrkt.fragPages.orders.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.beerdistrkt.R
import com.example.beerdistrkt.customView.BeerAmountRowView
import com.example.beerdistrkt.fragPages.orders.view.OrderView
import com.example.beerdistrkt.models.Order
import com.example.beerdistrkt.models.OrderStatus

class OrderAdapter(
    private var orders: MutableList<Order> = mutableListOf()
) : RecyclerView.Adapter<OrderAdapter.ViewHolder>() {

    private var deliveryMode = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(OrderView(parent.context))
    }

    override fun getItemCount(): Int = orders.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.orderView.lockSwipe(deliveryMode)
        holder.orderView.order = orders[position]
    }

    fun setData(orders: List<Order>){
        this.orders.clear()
        this.orders.addAll(orders)
        notifyDataSetChanged()
    }

    fun removeItem(index: Int){
        orders[index] = orders[index].copy(orderStatus = OrderStatus.DELETED)
        notifyItemChanged(index)
    }

    fun setMode(mode: Boolean) {
        deliveryMode = mode
        notifyDataSetChanged()
    }

    class ViewHolder(val orderView: OrderView) : RecyclerView.ViewHolder(orderView)
}