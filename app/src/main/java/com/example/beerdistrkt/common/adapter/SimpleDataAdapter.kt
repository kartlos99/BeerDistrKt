package com.example.beerdistrkt.common.adapter

import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.beerdistrkt.utils.DefaultDiffItemCallback
import com.example.beerdistrkt.utils.DiffItem
import com.example.beerdistrkt.utils.inflate

class SimpleDataAdapter<T : DiffItem>(
    private val viewCreator: (() -> View)? = null,
    @LayoutRes
    private val layoutId: Int? = null,
    private val onBind: (item: T, view: View) -> Unit,
    private val onItemClick: ((item: T) -> Unit)? = null
) : ListAdapter<T, SimpleDataAdapter.SimpleDataViewHolder>(
    DefaultDiffItemCallback<T>()
) {

    class SimpleDataViewHolder(v: View) : RecyclerView.ViewHolder(v)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimpleDataViewHolder {
        return SimpleDataViewHolder(viewCreator?.invoke() ?: parent.inflate(layoutId!!))
    }

    override fun onBindViewHolder(holder: SimpleDataViewHolder, position: Int) {
        val item = getItem(position) ?: return
        onBind(item, holder.itemView)
        onItemClick?.let {
            holder.itemView.setOnClickListener {
                onItemClick.invoke(item)
            }
        }
    }

    override fun submitList(list: List<T>?) {
        super.submitList(list?.let { ArrayList(it) })
    }
}