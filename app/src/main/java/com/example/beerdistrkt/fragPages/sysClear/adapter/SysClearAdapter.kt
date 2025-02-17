package com.example.beerdistrkt.fragPages.sysClear.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.beerdistrkt.databinding.SysClearRowBinding
import com.example.beerdistrkt.fragPages.sysClear.models.SysClearModel

class SysClearAdapter(
    private val dataList: List<SysClearModel>,
    private val onLongPress: ((name: String, id: Int) -> Unit)? = null
) : RecyclerView.Adapter<SysClearAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            SysClearRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int = dataList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = with(holder.binding) {
        val item = dataList[position]
        sysClearItemName.text = item.dasaxeleba
        sysClearItemDate.text = item.clearDate
        sysClearItemDays.text = item.passDays.toString()
        root.setOnLongClickListener {
            onLongPress?.invoke(item.dasaxeleba, item.id)
            return@setOnLongClickListener true
        }
    }

    class ViewHolder(val binding: SysClearRowBinding) : RecyclerView.ViewHolder(binding.root)
}