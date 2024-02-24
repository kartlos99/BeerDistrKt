package com.example.beerdistrkt.fragPages.sawyobi.adapters

import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.beerdistrkt.databinding.SawyobiListItemViewBinding
import com.example.beerdistrkt.fragPages.sawyobi.models.StorehouseIoPm

class StorehouseIoViewHolder(
    private val binding: SawyobiListItemViewBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: StorehouseIoPm, viewPool: RecyclerView.RecycledViewPool) = with(binding) {
        shlComment.text = item.comment
        shlComment.isVisible = item.comment.isNullOrBlank().not()
        shlActionDateTv.text = item.ioDate

        val adapter = SimpleBeerRowAdapter(
            item.barrelItems ?: listOf(),
            item.bottleItems ?: listOf()
        )
        adapter.onClick = View.OnClickListener {}
        val lm = LinearLayoutManager(shlSubRecycler.context, LinearLayoutManager.VERTICAL, false)
        lm.initialPrefetchItemCount = item.ioCount
        shlSubRecycler.setHasFixedSize(true)
        shlSubRecycler.setRecycledViewPool(viewPool)
        shlSubRecycler.layoutManager = lm
        shlSubRecycler.adapter = adapter
    }
}