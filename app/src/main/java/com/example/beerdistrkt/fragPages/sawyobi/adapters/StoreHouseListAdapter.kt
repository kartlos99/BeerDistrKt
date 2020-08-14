package com.example.beerdistrkt.fragPages.sawyobi.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.beerdistrkt.R
import com.example.beerdistrkt.fragPages.sawyobi.models.IoModel
import com.example.beerdistrkt.fragPages.sawyobi.models.SimpleBeerRowModel
import com.example.beerdistrkt.models.BeerModel
import kotlinx.android.synthetic.main.sawyobi_list_item_view.view.*

class StoreHouseListAdapter(
    private val dataMap: Map<String, List<IoModel>>,
    private val beerMap: Map<Int, BeerModel>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.sawyobi_list_item_view, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = dataMap.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder.itemView.shlActionDateTv.text = dataMap.keys.toList()[position]

        val itemData = ioModelToSimpleBeerRow(dataMap.values.toList()[position])

        holder.itemView.shlSubRecycler.layoutManager =
            LinearLayoutManager(holder.itemView.shlSubRecycler.context)
        val adapter = SimpleBeerRowAdapter(itemData)
        adapter.onClick = View.OnClickListener {}
        holder.itemView.shlSubRecycler.adapter = adapter
    }

    private class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private fun ioModelToSimpleBeerRow(itemData: List<IoModel>): List<SimpleBeerRowModel> {
        val result = mutableListOf<SimpleBeerRowModel>()
        val a = itemData.groupBy { it.beerID }

        a.values.forEach { singleBeerList ->

            val splitedByBarrelMap = singleBeerList.groupBy { it.barrelID }

            val countByBarrelMap = splitedByBarrelMap.mapValues {
                it.value.sumBy { ioModel -> ioModel.count }
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