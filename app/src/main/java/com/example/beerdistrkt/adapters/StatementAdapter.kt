package com.example.beerdistrkt.adapters

import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.beerdistrkt.R
import com.example.beerdistrkt.models.StatementModel
import com.example.beerdistrkt.utils.*
import kotlinx.android.synthetic.main.amonaweri_list_row.view.*
import java.text.DecimalFormat

class StatementAdapter(
    private val statementList: List<StatementModel>,
    private val location: Int
) : RecyclerView.Adapter<StatementViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatementViewHolder {
        return StatementViewHolder(parent.inflate(R.layout.amonaweri_list_row))
    }

    override fun getItemCount(): Int = statementList.size

    override fun onBindViewHolder(holder: StatementViewHolder, position: Int) {
        val currItem = statementList[position]
        val df = DecimalFormat("#0.00")

        holder.itemView.t_amon_list_tarigi.text = currItem.tarigi
        holder.itemView.t_amon_list_balance.text = df.format(currItem.balance)
        holder.itemView.t_amonaweri_row_comment.text = currItem.comment.orEmpty()
        when (location) {
            M_PAGE -> {
                holder.itemView.t_amon_list_in.text = getFormattedString(currItem.price, df)
                holder.itemView.t_amon_list_out.text = getFormattedString(currItem.pay, df)
            }
            K_PAGE -> {
                holder.itemView.t_amon_list_in.text = getFormattedString(currItem.k_in, df)
                holder.itemView.t_amon_list_out.text = getFormattedString(currItem.k_out, df)
            }
        }

        holder.itemView.t_amon_list_in.setTextColor(if (currItem.comment.isNullOrBlank()) Color.BLACK else Color.MAGENTA)
        holder.itemView.t_amon_list_out.setTextColor(if (currItem.comment.isNullOrBlank()) Color.BLACK else Color.MAGENTA)

        if (currItem.comment.isNullOrBlank())
            holder.itemView.t_amonaweri_row_comment.goAway()

        holder.itemView.setOnClickListener {
            if (!currItem.comment.isNullOrBlank())
                holder.itemView.t_amonaweri_row_comment
                    .visibleIf(holder.itemView.t_amonaweri_row_comment.visibility != View.VISIBLE)
        }
    }

    private fun getFormattedString(value: Float, formatter: DecimalFormat): String =
        if (value == 0F)
            DASH
        else
            formatter.format(value)

    companion object {
        const val DASH = "-"
    }
}

class StatementViewHolder(view: View) : RecyclerView.ViewHolder(view) {

}