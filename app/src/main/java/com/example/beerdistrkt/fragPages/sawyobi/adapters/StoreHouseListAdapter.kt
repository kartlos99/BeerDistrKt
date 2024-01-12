package com.example.beerdistrkt.fragPages.sawyobi.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.beerdistrkt.R
import com.example.beerdistrkt.databinding.SawyobiListItemViewBinding
import com.example.beerdistrkt.fragPages.sawyobi.models.CombinedIoModel
import com.example.beerdistrkt.fragPages.sawyobi.models.IoModel
import com.example.beerdistrkt.fragPages.sawyobi.models.SimpleBeerRowModel
import com.example.beerdistrkt.fragPages.sawyobi.models.SimpleBottleRowModel

class StoreHouseListAdapter(
    private val data: List<CombinedIoModel>,
) : RecyclerView.Adapter<StoreHouseListAdapter.ViewHolder>() {

    var onLongClick: ((date: String) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            SawyobiListItemViewBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = with(holder.binding) {
        val item = data[position]
        shlActionDateTv.text = item.date
        root.setOnLongClickListener {
            onLongClick?.invoke(item.groupID)
            return@setOnLongClickListener true
        }
        shlComment.isVisible = item.comment.isNullOrEmpty().not()
        shlComment.text = item.comment

        val itemData = ioModelToSimpleBeerRow(data[position].barrels)
        val bottleItems = data[position].bottles?.map {
            SimpleBottleRowModel(
                it.bottle?.name ?: "- უცნობი -",
                it.count,
                it.bottle?.imageLink
            )
        } ?: listOf()

        val adapter = SimpleBeerRowAdapter(itemData, bottleItems)
        adapter.onClick = View.OnClickListener {}
        shlSubRecycler.layoutManager = LinearLayoutManager(shlSubRecycler.context)
        shlSubRecycler.adapter = adapter
    }

    class ViewHolder(val binding: SawyobiListItemViewBinding) :
        RecyclerView.ViewHolder(binding.root)


    private fun ioModelToSimpleBeerRow(itemData: List<IoModel>?): List<SimpleBeerRowModel> {
        if (itemData == null) return listOf()
        val result = mutableListOf<SimpleBeerRowModel>()
        itemData
            .groupBy { it.beerID }
            .values
            .forEach { singleBeerList ->

            val splitedByBarrelMap = singleBeerList.groupBy { it.barrelID }

            val countByBarrelMap = splitedByBarrelMap.mapValues {
                it.value.sumOf { ioModel -> ioModel.count }
            }

            val title = singleBeerList[0].beer?.dasaxeleba ?: "- ცარიელი -"
            val icon =
                if (singleBeerList[0].beerID == 0) R.drawable.ic_barrel_output_24 else R.drawable.ic_beer_input_24
            val iconColor =
                if (singleBeerList[0].beerID == 0) R.color.orange_08 else R.color.green_09

            result.add(
                SimpleBeerRowModel(
                    title,
                    countByBarrelMap,
                    icon,
                    iconColor,
                    singleBeerList[0].beer?.displayColor
                )
            )
        }

        return result
    }
}