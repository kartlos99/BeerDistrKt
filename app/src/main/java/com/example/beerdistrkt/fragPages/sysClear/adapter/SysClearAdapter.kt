package com.example.beerdistrkt.fragPages.sysClear.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.beerdistrkt.databinding.SysClearRowBinding
import com.example.beerdistrkt.fragPages.sysClear.models.SysClearModel

class SysClearAdapter(
    private val dataList: List<SysClearModel>
): RecyclerView.Adapter<SysClearAdapter.ViewHolder>() {
//    var onClick: View.OnClickListener? = null
    var onLongPress: ((name: String, id: Int) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(SysClearRowBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int = dataList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder.binding) {
            sysClearItemName.text = dataList[position].dasaxeleba
            sysClearItemDate.text = dataList[position].clearDate
            sysClearItemDays.text = dataList[position].passDays.toString()
            root.setOnLongClickListener {
                onLongPress?.invoke(dataList[position].dasaxeleba, dataList[position].id)
                return@setOnLongClickListener true
            }
        }
    }

    class ViewHolder(val binding: SysClearRowBinding) : RecyclerView.ViewHolder(binding.root)
}