package com.example.beerdistrkt.fragPages.sawyobi.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.beerdistrkt.databinding.SawyobiListItemViewBinding
import com.example.beerdistrkt.fragPages.sawyobi.models.StorehouseIoPm
import com.example.beerdistrkt.utils.DefaultDiffItemCallback

class StorehousePagedAdapter: PagingDataAdapter<StorehouseIoPm, StorehouseIoViewHolder>(DefaultDiffItemCallback()) {

    private val viewPool = RecyclerView.RecycledViewPool()

    override fun onBindViewHolder(holder: StorehouseIoViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it, viewPool)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StorehouseIoViewHolder {
        return StorehouseIoViewHolder(
            SawyobiListItemViewBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

}