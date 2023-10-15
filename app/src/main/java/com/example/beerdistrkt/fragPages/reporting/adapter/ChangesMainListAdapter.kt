package com.example.beerdistrkt.fragPages.reporting.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.beerdistrkt.R
import com.example.beerdistrkt.adapters.SimpleListAdapter
import com.example.beerdistrkt.customView.LabeledListItemView
import com.example.beerdistrkt.databinding.ChangesMainListItemLayoutBinding
import com.example.beerdistrkt.fragPages.reporting.model.ChangesShortDto
import com.example.beerdistrkt.utils.DefaultDiffItemCallback

class ChangesMainListAdapter(

) : ListAdapter<ChangesShortDto, ChangesMainListViewHolder>(DefaultDiffItemCallback<ChangesShortDto>()) {

    var onItemClick: (item: ChangesShortDto) -> Unit = {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChangesMainListViewHolder {
        val binding = ChangesMainListItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ChangesMainListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChangesMainListViewHolder, position: Int) {
        holder.bind(getItem(position), onItemClick)
    }
}

class ChangesMainListViewHolder(
    val binding: ChangesMainListItemLayoutBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: ChangesShortDto, onItemClick: (item: ChangesShortDto) -> Unit = {}) =
        with(binding) {
            changeItemName.text = item.tableName.displayName
            changeAuthor.text = binding.root.context.getString(
                R.string.two_separated,
                item.modifyUsername,
                item.modifyDate
            )

            val subItems = item.shortInfo.entries.toList()

            val layoutManager = LinearLayoutManager(binding.root.context)
            val shortItemsAdapter = SimpleListAdapter(
                subItems,
                {
                    return@SimpleListAdapter LabeledListItemView(binding.root.context, null)
                },
                null,
                onBind = { shortInfoItem, view ->
                    (view as LabeledListItemView).apply {
                        setLabel(shortInfoItem.key)
                        setText(shortInfoItem.value)
                    }
                },
                onClick = {
                    onItemClick.invoke(item)
                }
            )
            shortInfoRv.layoutManager = layoutManager
            shortInfoRv.adapter = shortItemsAdapter
            shortInfoRv.setHasFixedSize(true)

            root.setOnClickListener {
                onItemClick.invoke(item)
            }
        }
}

