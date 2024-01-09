package com.example.beerdistrkt.fragPages.orders.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.beerdistrkt.fragPages.orders.view.OrderBottleItemView
import com.example.beerdistrkt.fragPages.orders.view.OrderItemView
import com.example.beerdistrkt.models.Order

class OrderItemAdapter(
    private var orderItems: Map<Int, List<Order.Item>> = emptyMap(),
    private val saleItems: Map<Int, List<Order.Sales>> = emptyMap(),
    private val bottleOrderItems: List<Order.BottleItem> = emptyList(),
    private val bottleSaleItems: List<Order.BottleSaleItem> = emptyList(),
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            BARREL_ORDER_ITEM -> ViewHolder(OrderItemView(parent.context))
            else -> ViewHolder(OrderBottleItemView(parent.context))
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position < orderItems.size)
            BARREL_ORDER_ITEM
        else
            BOTTLE_ORDER_ITEM
    }

    override fun getItemCount(): Int = orderItems.size + bottleOrderItems.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val orderItemView = holder.itemView) {
            is OrderItemView -> {
                // key is beerID
                val key = orderItems.keys.elementAt(position)
                orderItems[key]?.let { orderItemsList ->
                    val salesOfThisBeer = saleItems[key]
                    orderItemView.fillData(orderItemsList, salesOfThisBeer?.toMutableList())
                }
            }

            is OrderBottleItemView -> {
                bottleOrderItems[position - orderItems.size].let { bottleOrder ->
                    val bottleSale =
                        bottleSaleItems.firstOrNull { it.bottle.id == bottleOrder.bottle.id }
                    orderItemView.fillData(bottleOrder, bottleSale)
                }
            }
        }
    }

    private class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    companion object {
        const val BARREL_ORDER_ITEM = 1
        const val BOTTLE_ORDER_ITEM = 2
    }
}