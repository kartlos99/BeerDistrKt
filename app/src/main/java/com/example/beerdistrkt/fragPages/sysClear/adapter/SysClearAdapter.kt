package com.example.beerdistrkt.fragPages.sysClear.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.beerdistrkt.R
import com.example.beerdistrkt.customView.BeerAmountRowView
import com.example.beerdistrkt.fragPages.sawyobi.models.SimpleBeerRowModel
import com.example.beerdistrkt.fragPages.sysClear.models.SysClearModel
import kotlinx.android.synthetic.main.sys_clear_row.view.*

class SysClearAdapter(
    private val dataList: List<SysClearModel>
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
//    var onClick: View.OnClickListener? = null
    var onLongPress: ((name: String, id: Int) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.sys_clear_row, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = dataList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder.itemView.sysClearItemName.text = dataList[position].dasaxeleba
        holder.itemView.sysClearItemDate.text = dataList[position].clearDate
        holder.itemView.sysClearItemDays.text = dataList[position].passDays.toString()
        holder.itemView.setOnLongClickListener {
            onLongPress?.invoke(dataList[position].dasaxeleba, dataList[position].id)
            return@setOnLongClickListener true
        }
    }

    private class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}