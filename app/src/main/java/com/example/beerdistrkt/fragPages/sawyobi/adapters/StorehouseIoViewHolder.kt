package com.example.beerdistrkt.fragPages.sawyobi.adapters

import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.beerdistrkt.databinding.SawyobiListItemViewBinding
import com.example.beerdistrkt.fragPages.sawyobi.domain.StorehouseIO

class StorehouseIoViewHolder(
    private val binding: SawyobiListItemViewBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: StorehouseIO) {
        binding.shlComment.text = item.comment
        binding.shlComment.isVisible = true
        binding.shlActionDateTv.text = item.ioDate
    }
}