package com.example.beerdistrkt.adapters

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.recyclerview.widget.RecyclerView
import com.example.beerdistrkt.R
import com.example.beerdistrkt.databinding.AmonaweriListRowBinding
import com.example.beerdistrkt.fragPages.login.models.Permission
import com.example.beerdistrkt.models.StatementModel
import com.example.beerdistrkt.setFrictionSize
import com.example.beerdistrkt.showToast
import com.example.beerdistrkt.utils.*
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

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
            AmonaweriListRowBinding.inflate(LayoutInflater.from(parent.context)),
            location,
            checkGrouped
        )
    }

    override fun getItemCount(): Int = statementList.size

    override fun onBindViewHolder(holder: StatementViewHolder, position: Int) {
        holder.bind(statementList[position])
    }


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
        private val binding: AmonaweriListRowBinding,
        private val location: Int,
        private val isGrouped: () -> Boolean
    ) : RecyclerView.ViewHolder(binding.root), View.OnCreateContextMenuListener {

        init {
            binding.root.setOnCreateContextMenuListener(this)
            binding.root.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        fun bind(item: StatementModel) {
            with(binding) {
                val df = DecimalFormat("#0.00")
                root.tag = item
                tAmonListTarigi.text = item.tarigi
                tAmonaweriRowComment.text = item.comment.orEmpty()

                val defTextColor = Color.parseColor("#292929")
                val textColor = if (item.comment.isNullOrBlank()) defTextColor else Color.MAGENTA

                when (location) {
                    M_PAGE -> {
                        val frictionColor =
                            if (item.comment.isNullOrBlank()) Color.parseColor("#808080") else Color.MAGENTA
                        val frSize = root.resources.getDimensionPixelSize(R.dimen.sp12)
                        tAmonListIn.text =
                            getFormattedString(item.price, df).setFrictionSize(
                                frSize,
                                frictionColor
                            )
                        tAmonListOut.text =
                            getFormattedString(item.pay, df).setFrictionSize(frSize, frictionColor)
                        tAmonListBalance.text =
                            df.format(item.balance).setFrictionSize(frSize, frictionColor)
                    }
                    K_PAGE -> {
                        tAmonListIn.text =
                            if (item.k_in == 0) DASH else item.k_in.toString()
                        tAmonListOut.text =
                            if (item.k_out == 0) DASH else item.k_out.toString()
                        tAmonListBalance.text =
                            item.balance.roundToInt().toString()
                    }
                }

                tAmonListIn.setTextColor(textColor)
                tAmonListOut.setTextColor(textColor)
                tAmonListBalance.setTextColor(textColor)

                if (item.comment.isNullOrBlank())
                    tAmonaweriRowComment.goAway()

                root.setOnClickListener {
                    if (!item.comment.isNullOrBlank())
                        tAmonaweriRowComment
                            .visibleIf(tAmonaweriRowComment.visibility != View.VISIBLE)
                }
            }
        }

        private fun getFormattedString(value: Float, formatter: DecimalFormat): String =
            if (value == 0F)
                DASH
            else
                formatter.format(value)

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
                            menu?.add(
                                adapterPosition,
                                CtxMenuItem.Edit.itemID,
                                1,
                                CtxMenuItem.Edit.title
                            ) //groupId, itemId, order, title
                            menu?.add(
                                adapterPosition,
                                CtxMenuItem.History.itemID,
                                2,
                                CtxMenuItem.History.title
                            )
                            menu?.add(
                                adapterPosition,
                                CtxMenuItem.Delete.itemID,
                                3,
                                CtxMenuItem.Delete.title
                            )
                        }
                        if (location == 1) {
                            menu?.setHeaderTitle(ctx.getString(R.string.barrel_menu_title))
                            menu?.add(
                                adapterPosition,
                                CtxMenuItem.EditBarrel.itemID,
                                1,
                                CtxMenuItem.EditBarrel.title
                            )
                            menu?.add(
                                adapterPosition,
                                CtxMenuItem.DeleteBarrel.itemID,
                                3,
                                CtxMenuItem.DeleteBarrel.title
                            )
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