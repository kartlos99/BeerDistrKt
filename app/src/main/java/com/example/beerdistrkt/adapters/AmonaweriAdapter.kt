package com.example.beerdistrkt.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.beerdistrkt.R
import com.example.beerdistrkt.fragPages.amonaweri.model.StatementModel
import com.example.beerdistrkt.utils.K_PAGE
import com.example.beerdistrkt.utils.M_PAGE
import java.text.DecimalFormat
import kotlin.collections.ArrayList

class AmonaweriAdapter(
    context: Context?,
    amonaweriList: List<StatementModel>,
    location: Int,
    isGrouped: Boolean
) :
    BaseAdapter() {
    private val amonaweriList: ArrayList<StatementModel>
    private val layoutInflater: LayoutInflater
    private val location: Int
//    private val grouped: Boolean
    var context: Context

    override fun getCount(): Int {
        return amonaweriList.size
    }

    override fun getItem(i: Int): StatementModel {
        return amonaweriList[i]
    }

    override fun getItemId(i: Int): Long {
        return i.toLong()
    }

    override fun getView(
        i: Int,
        convertView: View?,
        viewGroup: ViewGroup
    ): View {
        val listRowView: View
        val viewHolder: ViewHolderAmo
        if (convertView == null) {
            listRowView = layoutInflater.inflate(R.layout.amonaweri_list_row, null)
            viewHolder = ViewHolderAmo()
            viewHolder.t_p1 = listRowView.findViewById(R.id.t_amon_list_tarigi)
            viewHolder.t_p2 = listRowView.findViewById(R.id.t_amon_list_in)
            viewHolder.t_p3 = listRowView.findViewById(R.id.t_amon_list_out)
            viewHolder.t_p4 = listRowView.findViewById(R.id.t_amon_list_balance)
            viewHolder.t_comment = listRowView.findViewById(R.id.t_amonaweri_row_comment)
            listRowView.tag = viewHolder
        } else {
            listRowView = convertView
            viewHolder = listRowView.tag as ViewHolderAmo
        }
        val currItem: StatementModel = getItem(i)
        viewHolder.t_p1?.text = currItem.tarigi
        if (location == M_PAGE) {
            val df = DecimalFormat("#0.00")
            if (currItem.price == 0F) {
                viewHolder.t_p2?.text = "-"
            } else {
                viewHolder.t_p2?.text = df.format(currItem.price)
            }
            if (currItem.pay == 0F) {
                viewHolder.t_p3?.text = "-"
            } else {
                viewHolder.t_p3?.text = df.format(currItem.pay)
            }
            viewHolder.t_p4?.text = df.format(currItem.balance)
        }
        if (location == K_PAGE) {
//            if (currItem.k_in == 0F) {
//                viewHolder.t_p2!!.text = "-"
//            } else {
//                viewHolder.t_p2?.text = MyUtil.floatToSmartStr(currItem.k_in)
//            }
//            if (currItem.k_out == 0F) {
//                viewHolder.t_p3?.text = "-"
//            } else {
//                viewHolder.t_p3?.setText(MyUtil.floatToSmartStr(currItem.k_out))
//            }
//            viewHolder.t_p4?.setText(MyUtil.floatToSmartStr(currItem.balance))
        }
        viewHolder.t_comment?.text = currItem.comment ?: ""
        if (currItem.comment.isNullOrEmpty()) { //                viewHolder.t_p1.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
            viewHolder.t_p3!!.setTextColor(Color.BLACK)
            viewHolder.t_p2!!.setTextColor(Color.BLACK)
        } else {
            viewHolder.t_p3!!.setTextColor(Color.MAGENTA)
            viewHolder.t_p2!!.setTextColor(Color.MAGENTA)
        }
        return listRowView
    }

    private inner class ViewHolderAmo {
        var t_p1: TextView? = null
        var t_p2: TextView? = null
        var t_p3: TextView? = null
        var t_p4: TextView? = null
        var t_comment: TextView? = null
    }

    init {
        this.amonaweriList = ArrayList()
        this.amonaweriList.addAll(amonaweriList)
        layoutInflater =
            context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        this.location = location
//        grouped = isGrouped
        this.context = context
    }
}