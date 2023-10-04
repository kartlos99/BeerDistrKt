package com.example.beerdistrkt.fragPages.reporting.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.beerdistrkt.databinding.ViewChangesHistoryItemBinding
import com.example.beerdistrkt.fragPages.reporting.model.HistoryUnitModel
import com.example.beerdistrkt.utils.DefaultDiffItemCallback

class ChangesHistoryAdapter : ListAdapter<HistoryUnitModel, ChangesHistoryViewHolder>(
    DefaultDiffItemCallback<HistoryUnitModel>()
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChangesHistoryViewHolder {
        val binding = ViewChangesHistoryItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ChangesHistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChangesHistoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class ChangesHistoryViewHolder(
    val binding: ViewChangesHistoryItemBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: HistoryUnitModel) {
        binding.itemInfo.text = item.text
    }

}