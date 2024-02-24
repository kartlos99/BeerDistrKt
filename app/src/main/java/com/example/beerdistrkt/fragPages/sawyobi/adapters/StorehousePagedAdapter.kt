package com.example.beerdistrkt.fragPages.sawyobi.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import com.example.beerdistrkt.databinding.SawyobiListItemViewBinding
import com.example.beerdistrkt.fragPages.sawyobi.domain.StorehouseIO
import com.example.beerdistrkt.utils.DefaultDiffItemCallback

class StorehousePagedAdapter: PagingDataAdapter<StorehouseIO, StorehouseIoViewHolder>(DefaultDiffItemCallback()) {

    override fun onBindViewHolder(holder: StorehouseIoViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
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