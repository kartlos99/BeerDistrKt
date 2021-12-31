package com.example.beerdistrkt.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.beerdistrkt.R
import com.example.beerdistrkt.databinding.BeerSliderRowBinding

class BeerAdapter(val beers: ArrayList<String>): RecyclerView.Adapter<BeerAdapter.BeerViewHolder>() {

    class BeerViewHolder(itemView: BeerSliderRowBinding) : RecyclerView.ViewHolder(itemView.root) {
        val tvBeerName = itemView.tvBeerName
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BeerViewHolder {
//        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.beer_slider_row, parent, false)
        val binding = BeerSliderRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BeerViewHolder(binding)
    }

    override fun getItemCount() = beers.size

    override fun onBindViewHolder(holder: BeerViewHolder, position: Int) {
        if (beers[position] == "-"){
            holder.tvBeerName.visibility = View.INVISIBLE
        }else{
            holder.tvBeerName.visibility = View.VISIBLE
        }
        holder.tvBeerName.text = beers[position]
    }
}