package com.example.beerdistrkt.fragPages.sawyobi.adapters

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.beerdistrkt.customView.BeerAmountRowView
import com.example.beerdistrkt.fragPages.orders.view.CounterLinearProgressView.Companion.BOLD_STYLE_ALL

import com.example.beerdistrkt.fragPages.sawyobi.models.SimpleBeerRowModel

class SimpleBeerRowAdapter(
    private val dataList: List<SimpleBeerRowModel>,
    private val isHomePage: Boolean = false
) : RecyclerView.Adapter<SimpleBeerRowAdapter.ViewHolder>() {

    var onClick: View.OnClickListener? = null

    var barrelsAmountBoldStyle = BOLD_STYLE_ALL

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(BeerAmountRowView(parent.context))
    }

    override fun getItemCount(): Int = dataList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.view.apply {
            setOnClickListener(onClick)
            isHomePage = this@SimpleBeerRowAdapter.isHomePage
            showLiters = this@SimpleBeerRowAdapter.isHomePage
            setBoldStyle(barrelsAmountBoldStyle)
            setData(dataList[position])
        }

    }

    class ViewHolder(val view: BeerAmountRowView) : RecyclerView.ViewHolder(view)
}