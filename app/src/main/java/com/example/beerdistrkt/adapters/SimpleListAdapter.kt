package com.example.beerdistrkt.adapters

import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.example.beerdistrkt.utils.inflate

@Suppress("UNCHECKED_CAST")
class SimpleListAdapter<T>(
    var data: List<T>?,
    private val viewCreator: (() -> View)? = null,
    @LayoutRes private val layoutId: Int? = null,
    private val onBind: (item: T, view: View) -> Unit,
    private val onClick: ((item: T) -> Unit)? = null
) :
    RecyclerView.Adapter<SimpleListAdapter.ViewHolder>() {

    private val onItemClickListener = View.OnClickListener {
        val item = it?.tag as T ?: return@OnClickListener
        onClick?.invoke(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(viewCreator?.invoke() ?: parent.inflate(layoutId!!))
    }

    override fun getItemCount(): Int {
        return data?.size ?: 0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data?.get(position) ?: return
        onBind(item, holder.itemView)
        holder.itemView.tag = item
        if (onClick != null)
            holder.itemView.setOnClickListener(onItemClickListener)
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v)
}