package com.example.beerdistrkt.fragPages.sawyobi.adapters

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.beerdistrkt.customView.BeerAmountRowView
import com.example.beerdistrkt.fragPages.orders.view.CounterLinearProgressView.Companion.BOLD_STYLE_ALL

import com.example.beerdistrkt.fragPages.sawyobi.models.SimpleBeerRowModel

class SimpleBeerRowAdapter(
    private val dataList: List<SimpleBeerRowModel>
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var onClick: View.OnClickListener? = null

    var barrelsAmountBoldStyle = BOLD_STYLE_ALL

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(BeerAmountRowView(parent.context))
    }

    override fun getItemCount(): Int = dataList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val itemView = holder.itemView
        if (itemView is BeerAmountRowView) {
            itemView.setBoldStyle(barrelsAmountBoldStyle)
            itemView.setData(dataList[position])
            itemView.setOnClickListener(onClick)
        }
    }

    private class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}