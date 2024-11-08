package com.example.beerdistrkt.fragPages.showHistory

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.beerdistrkt.databinding.OrderHistoryItemViewBinding
import com.example.beerdistrkt.fragPages.beer.domain.model.Beer
import com.example.beerdistrkt.fragPages.sawyobi.adapters.SimpleBeerRowAdapter
import com.example.beerdistrkt.fragPages.sawyobi.models.SimpleBeerRowModel
import com.example.beerdistrkt.utils.visibleIf

class HistoryAdapter(
    private val historyData: List<OrderHistory>,
    private val beerMap: Map<Int, Beer>,
) :
    RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            OrderHistoryItemViewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            beerMap
        )
    }

    override fun getItemCount(): Int = historyData.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var lastFullItemsIndex = position
        while (historyData[lastFullItemsIndex].items.isNullOrEmpty() && lastFullItemsIndex > 0) {
            lastFullItemsIndex--
        }
        holder.bind(historyData[position], historyData[lastFullItemsIndex])
    }

    class ViewHolder(
        private val binding: OrderHistoryItemViewBinding,
        private val beerMap: Map<Int, Beer>,
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(orderHistory: OrderHistory, lastFullItem: OrderHistory) = with(binding) {
            val ctx = root.context
            orderHistoryOperator.text = orderHistory.modifyUser
            orderHistoryChangesDate.text = orderHistory.modifyDate
            orderHistoryClient.text =
                ctx.getString(
                    com.example.beerdistrkt.R.string.client_field,
                    orderHistory.clientName
                )
            orderHistoryOrderDate.text =
                ctx.getString(com.example.beerdistrkt.R.string.date_field, orderHistory.orderDate)
            orderHistoryStatus.text = ctx.getString(
                com.example.beerdistrkt.R.string.order_status_field,
                ctx.getString(orderHistory.orderStatus.textRes)
            )
            orderHistoryDistributor.text =
                ctx.getString(
                    com.example.beerdistrkt.R.string.order_distr_field,
                    orderHistory.distributor
                )
            orderHistoryComment.text = orderHistory.comment
            orderHistoryComment.visibleIf(!orderHistory.comment.isNullOrBlank())

            lastFullItem.items?.let {
                orderHistoryItemsRc.layoutManager =
                    LinearLayoutManager(orderHistoryItemsRc.context)
                val adapter = SimpleBeerRowAdapter(ioModelToSimpleBeerRow(it))
                adapter.onClick = android.view.View.OnClickListener {}
                orderHistoryItemsRc.adapter = adapter
            }
        }

        private fun ioModelToSimpleBeerRow(itemData: List<OrderHistoryDTO.Item>): List<SimpleBeerRowModel> {
            val result = mutableListOf<SimpleBeerRowModel>()
            val a = itemData.groupBy { it.beerID }

            a.values.forEach { singleBeerList ->

                val splitedByBarrelMap = singleBeerList.groupBy { it.canTypeID }

                val countByBarrelMap = splitedByBarrelMap.mapValues {
                    it.value.sumBy { itm -> itm.count }
                }

                val title = beerMap[singleBeerList[0].beerID]?.name ?: "- ცარიელი -"
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
}