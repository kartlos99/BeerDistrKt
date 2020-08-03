package com.example.beerdistrkt.fragPages.sales.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.beerdistrkt.R
import com.example.beerdistrkt.models.BarrelIO
import kotlinx.android.synthetic.main.view_barrel_io.view.*

class BarrelsIOAdapter (
    private val barrelsList: List<BarrelIO>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.view_barrel_io, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = barrelsList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val itemView = holder.itemView

        itemView.salesBarrelName.text = barrelsList[position].barrelName ?: "-"
        itemView.salesBarrelRealized.text = barrelsList[position].saleCount.toString()
        itemView.salesBarrelReceived.text = barrelsList[position].backCount.toString()

    }

    private class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}