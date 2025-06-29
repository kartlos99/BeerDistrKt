package com.example.beerdistrkt.fragPages.reporting.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.beerdistrkt.R
import com.example.beerdistrkt.databinding.ViewChangesHistoryItemBinding
import com.example.beerdistrkt.fragPages.reporting.model.HistoryCellType
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

    fun bind(item: HistoryUnitModel) = with(binding) {
        itemInfo.text = item.text
        itemInfo.alpha = if (item.type == HistoryCellType.Empty)
            0.5F
        else
            1.0F
        itemRoot.setBackgroundColor(getBackgroundColor(root.context, item.type))
    }

    private fun getBackgroundColor(ctx: Context, type: HistoryCellType) =
        when (type) {
            HistoryCellType.Changed -> ctx.getColor(R.color.history_cell_changed)
            HistoryCellType.Empty -> ctx.getColor(R.color.history_cell_empty)
            HistoryCellType.Header -> ctx.getColor(R.color.history_cell_header)
            else -> ctx.getColor(R.color.history_cell_regular)
        }
}