package com.example.beerdistrkt.adapters

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.ContextMenu
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.recyclerview.widget.RecyclerView
import com.example.beerdistrkt.R
import com.example.beerdistrkt.fragPages.login.models.Permission
import com.example.beerdistrkt.models.StatementModel
import com.example.beerdistrkt.showToast
import com.example.beerdistrkt.utils.*
import kotlinx.android.synthetic.main.amonaweri_list_row.view.*
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class StatementAdapter(
    private val statementList: MutableList<StatementModel>,
    private val location: Int
) : RecyclerView.Adapter<StatementAdapter.StatementViewHolder>() {

    var isGrouped = true
        set(value) {
            statementList.clear()
            notifyDataSetChanged()
            field = value
        }
    private var checkGrouped: (() -> Boolean) = {
        isGrouped
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatementViewHolder {
        return StatementViewHolder(
            parent.inflate(R.layout.amonaweri_list_row),
            location,
            checkGrouped
        )
    }

    override fun getItemCount(): Int = statementList.size

    override fun onBindViewHolder(holder: StatementViewHolder, position: Int) {
        val currItem = statementList[position]
        val df = DecimalFormat("#0.00")
        holder.itemView.tag = currItem
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

    fun addItems(data: List<StatementModel>) {
        val sizeBefore = statementList.size
        statementList.addAll(data)
        notifyItemRangeInserted(sizeBefore, statementList.size)
    }

    fun getItem(position: Int): StatementModel = statementList[position]

    fun clearData() {
        statementList.clear()
        notifyDataSetChanged()
    }

    companion object {
        const val DASH = "-"
    }


    class StatementViewHolder(
        view: View,
        private val location: Int,
        private val isGrouped: () -> Boolean
    ) : RecyclerView.ViewHolder(view), View.OnCreateContextMenuListener {

        init {
            view.setOnCreateContextMenuListener(this)
        }

        @SuppressLint("SimpleDateFormat")
        override fun onCreateContextMenu(
            menu: ContextMenu?,
            v: View?,
            menuInfo: ContextMenu.ContextMenuInfo?
        ) {
            val ctx = itemView.context ?: return
            if (isGrouped())
                ctx.showToast(R.string.remove_grouping)
            else
                itemView.tag?.let {
                    val itemData = it as StatementModel

                    val selectedItemDate: Date =
                        itemData.getItemDate(ctx.getString(R.string.patern_datetime)) ?: return

                    val dateFormat = SimpleDateFormat(ctx.getString(R.string.patern_date))

                    if (Session.get().hasPermission(Permission.EditOldSale) ||
                        (dateFormat.format(selectedItemDate) == dateFormat.format(Date())
                                && Session.get().hasPermission(Permission.EditSale))
                    ) {
                        if (location == 0) {
                            menu?.setHeaderTitle(ctx.getString(R.string.finance_menu_title))
                            menu?.add(adapterPosition, CtxMenuItem.Edit.itemID, 1, CtxMenuItem.Edit.title) //groupId, itemId, order, title
                            menu?.add(adapterPosition, CtxMenuItem.History.itemID, 2, CtxMenuItem.History.title)
                            menu?.add(adapterPosition, CtxMenuItem.Delete.itemID, 3, CtxMenuItem.Delete.title)
                        }
                        if (location == 1) {
                            menu?.setHeaderTitle(ctx.getString(R.string.barrel_menu_title))
                            menu?.add(adapterPosition, CtxMenuItem.EditBarrel.itemID, 1, CtxMenuItem.EditBarrel.title)
                            menu?.add(adapterPosition, CtxMenuItem.DeleteBarrel.itemID, 3, CtxMenuItem.DeleteBarrel.title)
                        }

                    } else
                        ctx.showToast(R.string.no_edit_access)
                }
        }
    }
}

enum class CtxMenuItem(@StringRes val title: Int, val itemID: Int) {
    Edit(R.string.m_edit, 201),
    EditBarrel(R.string.m_edit, 211),
    History(R.string.history, 202),
    Delete(R.string.delete, 203),
    DeleteBarrel(R.string.delete, 213)
}