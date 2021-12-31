package com.example.beerdistrkt.fragPages.sales.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.beerdistrkt.databinding.ViewBarrelIoBinding
import com.example.beerdistrkt.models.BarrelIO

class BarrelsIOAdapter (
    private val barrelsList: List<BarrelIO>
) : RecyclerView.Adapter<BarrelsIOAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BarrelsIOAdapter.ViewHolder {
        return ViewHolder(ViewBarrelIoBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int = barrelsList.size

    override fun onBindViewHolder(holder: BarrelsIOAdapter.ViewHolder, position: Int) {
        holder.bind(barrelsList[position])
    }

    inner class ViewHolder(val binding: ViewBarrelIoBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(barrelIO: BarrelIO) {
            with(binding) {
                salesBarrelName.text = barrelIO.barrelName ?: "-"
                salesBarrelRealized.text = barrelIO.saleCount.toString()
                salesBarrelReceived.text = barrelIO.backCount.toString()
            }
        }
    }
}