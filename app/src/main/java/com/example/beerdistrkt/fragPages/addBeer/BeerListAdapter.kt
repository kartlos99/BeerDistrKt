package com.example.beerdistrkt.fragPages.addBeer

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.beerdistrkt.databinding.BeerRowBinding
import com.example.beerdistrkt.models.BeerModelBase

class BeerListAdapter(private val beers: List<BeerModelBase>) :
    RecyclerView.Adapter<BeerListAdapter.ViewHolder>() {

    class ViewHolder(val binding: BeerRowBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: BeerModelBase) {
            binding.beerName.text = item.dasaxeleba
            binding.beerPrice.text = item.fasi.toString()

            item.displayColor?.let {
                binding.root.backgroundTintList =
                    ColorStateList.valueOf(Color.parseColor(it))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = BeerRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(beers[position])
    }

    override fun getItemCount() = beers.size

}