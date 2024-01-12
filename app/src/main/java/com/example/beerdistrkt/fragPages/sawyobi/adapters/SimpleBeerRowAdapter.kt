package com.example.beerdistrkt.fragPages.sawyobi.adapters

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.beerdistrkt.customView.BeerAmountRowView
import com.example.beerdistrkt.fragPages.orders.view.CounterLinearProgressView.Companion.BOLD_STYLE_ALL
import com.example.beerdistrkt.fragPages.orders.view.OrderBottleItemView

import com.example.beerdistrkt.fragPages.sawyobi.models.SimpleBeerRowModel
import com.example.beerdistrkt.fragPages.sawyobi.models.SimpleBottleRowModel

class SimpleBeerRowAdapter(
    private val barrelList: List<SimpleBeerRowModel> = listOf(),
    private val bottlesList: List<SimpleBottleRowModel> = listOf(),
    private val isHomePage: Boolean = false
) : RecyclerView.Adapter<SimpleBeerRowAdapter.ViewHolder>() {

    var onClick: View.OnClickListener? = null

    var barrelsAmountBoldStyle = BOLD_STYLE_ALL

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            BARREL_ITEM -> ViewHolder(BeerAmountRowView(parent.context))
            else -> ViewHolder(OrderBottleItemView(parent.context))
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position < barrelList.size)
            BARREL_ITEM
        else
            BOTTLE_ITEM
    }

    override fun getItemCount(): Int = barrelList.size + bottlesList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (val rowView = holder.itemView) {
            is BeerAmountRowView -> rowView.apply {
                setOnClickListener(onClick)
                isHomePage = this@SimpleBeerRowAdapter.isHomePage
                showLiters = this@SimpleBeerRowAdapter.isHomePage
                setBoldStyle(barrelsAmountBoldStyle)
                setData(barrelList[position])
            }
            is OrderBottleItemView -> {
                rowView.fillData(bottlesList[position - barrelList.size])
            }
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

    companion object {
        const val BARREL_ITEM = 1
        const val BOTTLE_ITEM = 2
    }
}