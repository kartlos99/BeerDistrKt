package com.example.beerdistrkt.fragPages.sawyobi.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.beerdistrkt.R
import com.example.beerdistrkt.databinding.SawyobiListItemViewBinding
import com.example.beerdistrkt.fragPages.sawyobi.models.IoModel
import com.example.beerdistrkt.fragPages.sawyobi.models.SimpleBeerRowModel
import com.example.beerdistrkt.models.BeerModelBase

class StoreHouseListAdapter(
    private val dataMap: Map<String, List<IoModel>>,
    private val beerMap: Map<Int, BeerModelBase>
) : RecyclerView.Adapter<StoreHouseListAdapter.ViewHolder>() {

    var onLongClick: ((date: String) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            SawyobiListItemViewBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int = dataMap.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = with(holder.binding) {
        shlActionDateTv.text = dataMap.values.toList()[position][0].ioDate
        root.setOnLongClickListener {
            onLongClick?.invoke(dataMap.values.toList()[position][0].groupID)
            return@setOnLongClickListener true
        }
        shlComment.isVisible = !dataMap.values.toList()[position][0].comment.isNullOrEmpty()
        shlComment.text = dataMap.values.toList()[position][0].comment

        val itemData = ioModelToSimpleBeerRow(dataMap.values.toList()[position])

        shlSubRecycler.layoutManager = LinearLayoutManager(shlSubRecycler.context)
        val adapter = SimpleBeerRowAdapter(itemData)
        adapter.onClick = View.OnClickListener {}
        shlSubRecycler.adapter = adapter
    }

    class ViewHolder(val binding: SawyobiListItemViewBinding) :
        RecyclerView.ViewHolder(binding.root)


    private fun ioModelToSimpleBeerRow(itemData: List<IoModel>): List<SimpleBeerRowModel> {
        val result = mutableListOf<SimpleBeerRowModel>()
        val a = itemData.groupBy { it.beerID }

        a.values.forEach { singleBeerList ->

            val splitedByBarrelMap = singleBeerList.groupBy { it.barrelID }

            val countByBarrelMap = splitedByBarrelMap.mapValues {
                it.value.sumOf { ioModel -> ioModel.count }
            }

            val title = beerMap[singleBeerList[0].beerID]?.dasaxeleba ?: "- ცარიელი -"
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
                    beerMap[singleBeerList[0].beerID]?.displayColor
                )
            )
        }

        return result
    }
}