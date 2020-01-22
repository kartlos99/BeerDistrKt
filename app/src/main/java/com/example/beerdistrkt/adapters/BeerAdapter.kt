package com.example.beerdistrkt.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.beerdistrkt.R
import kotlinx.android.synthetic.main.beer_slider_row.view.*

class BeerAdapter(val beers: ArrayList<String>): RecyclerView.Adapter<BeerAdapter.BeerViewHolder>() {


    class BeerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvBeerName = itemView.tvBeerName

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BeerViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.beer_slider_row, parent, false)
        return BeerViewHolder(view)
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