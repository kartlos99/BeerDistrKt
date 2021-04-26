package com.example.beerdistrkt.fragPages.showHistory

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.beerdistrkt.R
import com.example.beerdistrkt.fragPages.sawyobi.adapters.SimpleBeerRowAdapter
import com.example.beerdistrkt.fragPages.sawyobi.models.SimpleBeerRowModel
import com.example.beerdistrkt.models.BeerModel
import com.example.beerdistrkt.utils.visibleIf
import kotlinx.android.synthetic.main.order_history_item_view.view.*

class HistoryAdapter(
    private val historyData: List<OrderHistory>,
    private val beerMap: Map<Int, BeerModel>
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

//    var onLongClick: ((date: String) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.order_history_item_view, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = historyData.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val ctx = holder.itemView.context
//        holder.itemView.shlActionDateTv.text = dataMap.keys.toList()[position]
        holder.itemView.orderHistoryOperator.text = historyData[position].modifyUser
        holder.itemView.orderHistoryChangesDate.text = historyData[position].modifyDate
        holder.itemView.orderHistoryClient.text =
            ctx.getString(R.string.client_field, historyData[position].clientName)
        holder.itemView.orderHistoryOrderDate.text =
            ctx.getString(R.string.date_field, historyData[position].orderDate)
        holder.itemView.orderHistoryStatus.text = ctx.getString(R.string.order_status_field,
            ctx.getString(historyData[position].orderStatus.textRes))
        holder.itemView.orderHistoryDistributor.text =
            ctx.getString(R.string.order_distr_field, historyData[position].distributor)
        holder.itemView.orderHistoryComment.text = historyData[position].comment
        holder.itemView.orderHistoryComment.visibleIf(!historyData[position].comment.isNullOrBlank())

        var lastFullItemsIndex = position
        while (historyData[lastFullItemsIndex].items.isNullOrEmpty() && lastFullItemsIndex > 0) {
            lastFullItemsIndex--
        }

        historyData[lastFullItemsIndex].items?.let {
            holder.itemView.orderHistoryItemsRc.layoutManager =
                LinearLayoutManager(holder.itemView.orderHistoryItemsRc.context)
            val adapter = SimpleBeerRowAdapter(ioModelToSimpleBeerRow(it))
            adapter.onClick = View.OnClickListener {}
            holder.itemView.orderHistoryItemsRc.adapter = adapter
        }
    }

    private class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private fun ioModelToSimpleBeerRow(itemData: List<OrderHistoryDTO.Item>): List<SimpleBeerRowModel> {
        val result = mutableListOf<SimpleBeerRowModel>()
        val a = itemData.groupBy { it.beerID }

        a.values.forEach { singleBeerList ->

            val splitedByBarrelMap = singleBeerList.groupBy { it.canTypeID }

            val countByBarrelMap = splitedByBarrelMap.mapValues {
                it.value.sumBy { itm -> itm.count }
            }

            val title = beerMap[singleBeerList[0].beerID]?.dasaxeleba ?: "- ცარიელი -"
//            val icon =
//                if (singleBeerList[0].beerID == 0) R.drawable.ic_barrel_output_24 else R.drawable.ic_beer_input_24
//            val iconColor =
//                if (singleBeerList[0].beerID == 0) R.color.orange_08 else R.color.green_09

            result.add(
                SimpleBeerRowModel(
                    title,
                    countByBarrelMap,
                    null,
                    null,
                    beerMap[singleBeerList[0].beerID]?.displayColor
                )
            )
        }

        return result
    }
}