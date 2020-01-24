package com.example.beerdistrkt.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.beerdistrkt.R
import com.example.beerdistrkt.models.SaleInfo
import java.util.*
import kotlin.collections.ArrayList

class SalesAdapter(
    context: Context?,
    salesList: List<SaleInfo>
): BaseAdapter() {
//    private var salesList: ArrayList<SaleInfo>? = null
    private var layoutInflater: LayoutInflater = context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private val salesList: ArrayList<SaleInfo>

    init {
        this.salesList = ArrayList()
        this.salesList.addAll(salesList)
    }

    override fun getCount(): Int {
        return salesList.size
    }

    override fun getItem(i: Int): Any {
        return salesList[i]
    }

    override fun getItemId(i: Int): Long {
        return i.toLong()
    }

    override fun getView(
        i: Int,
        convertView: View?,
        viewGroup: ViewGroup
    ): View? {
        val listRowView: View
        val viewHolder: ViewHolder
        if (convertView == null) {
            listRowView = layoutInflater.inflate(R.layout.sales_row, null)
            viewHolder = ViewHolder()
            viewHolder.t_dasaxeleba = listRowView.findViewById<View>(R.id.t_beerName) as TextView
            viewHolder.t_litraji = listRowView.findViewById<View>(R.id.t_litraji) as TextView
            listRowView.tag = viewHolder
        } else {
            listRowView = convertView
            viewHolder = listRowView.tag as ViewHolder
        }
        val saleInfo: SaleInfo = getItem(i) as SaleInfo
        viewHolder.t_dasaxeleba?.text = saleInfo.beerName
        viewHolder.t_litraji?.text = saleInfo.litraji.toString()
        return listRowView
    }

    private class ViewHolder {
        var t_dasaxeleba: TextView? = null
        var t_litraji: TextView? = null
    }
}