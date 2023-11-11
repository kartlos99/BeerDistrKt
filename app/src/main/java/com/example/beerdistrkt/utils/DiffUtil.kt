package com.example.beerdistrkt.utils

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil

interface DiffItem {
    val key: Any? get() = null // provide item unique key here
}

open class DefaultDiffItemCallback<T : DiffItem> : DiffUtil.ItemCallback<T>() {
    override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
        return if (oldItem.key != null && newItem.key != null)
            oldItem::class == newItem::class && oldItem.key == newItem.key
        else
            oldItem === newItem  // default to reference equality to avoid clashes.
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem == newItem
    }
}