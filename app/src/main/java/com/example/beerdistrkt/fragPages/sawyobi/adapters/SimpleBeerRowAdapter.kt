package com.example.beerdistrkt.fragPages.sawyobi.adapters

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.beerdistrkt.customView.BeerAmountRowView

import com.example.beerdistrkt.fragPages.sawyobi.models.SimpleBeerRowModel

class SimpleBeerRowAdapter(
    val dataList: List<SimpleBeerRowModel>
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(BeerAmountRowView(parent.context))
    }

    override fun getItemCount(): Int = dataList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val itemView = holder.itemView
        if (itemView is BeerAmountRowView) {
            itemView.setData(dataList[position].title, dataList[position].values)
        }
    }

    private class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}