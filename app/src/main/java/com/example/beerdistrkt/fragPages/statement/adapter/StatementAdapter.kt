package com.example.beerdistrkt.fragPages.statement.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.beerdistrkt.R
import com.example.beerdistrkt.databinding.StatementListRowBinding
import com.example.beerdistrkt.fragPages.statement.model.CtxMenuItem
import com.example.beerdistrkt.fragPages.statement.model.StatementModel
import com.example.beerdistrkt.setFrictionSize
import com.example.beerdistrkt.showToast
import com.example.beerdistrkt.utils.K_PAGE
import com.example.beerdistrkt.utils.M_PAGE
import com.example.beerdistrkt.utils.goAway
import com.example.beerdistrkt.utils.orEmpty
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.math.roundToInt

class StatementAdapter(
    private val statementList: MutableList<StatementModel>,
    private val location: Int,
    private val editOldSalePermission: Boolean,
    private val editSalePermission: Boolean,
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
            StatementListRowBinding.inflate(LayoutInflater.from(parent.context)),
            location,
            editOldSalePermission,
            editSalePermission,
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
        private val binding: StatementListRowBinding,
        private val location: Int,
        private val editOldSalePermission: Boolean,
        private val editSalePermission: Boolean,
        private val isGrouped: () -> Boolean
    ) : RecyclerView.ViewHolder(binding.root), View.OnCreateContextMenuListener {

        init {
            binding.root.setOnCreateContextMenuListener(this)
            binding.root.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        fun bind(item: StatementModel) = with(binding) {
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
                    tAmonListIn.text = if (item.isGift) "უფასო" else
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

            if (item.groupGift && isGrouped())
                recordTypeIndicator.setImageResource(R.drawable.ic_gift_24)
            else
                recordTypeIndicator.setImageResource(0)
            item.recordType.icon?.let {
                recordTypeIndicator.setImageResource(it)
            }

            recordTypeIndicator.isVisible = item.recordType.icon != null
                    || (item.groupGift && isGrouped() && location == M_PAGE)

            tAmonListIn.setTextColor(textColor)
            tAmonListOut.setTextColor(textColor)
            tAmonListBalance.setTextColor(textColor)

            if (item.comment.isNullOrBlank())
                tAmonaweriRowComment.goAway()

            root.setOnClickListener {
                if (!item.comment.isNullOrBlank())
                    tAmonaweriRowComment.isVisible =
                        tAmonaweriRowComment.visibility != View.VISIBLE
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

                    if (editOldSalePermission ||
                        (dateFormat.format(selectedItemDate) == dateFormat.format(Date())
                                && editSalePermission)
                    ) {
                        if (location == M_PAGE) {
                            menu?.setHeaderTitle(ctx.getString(R.string.finance_menu_title))
                            menu?.add(
                                bindingAdapterPosition,
                                CtxMenuItem.Edit.itemID,
                                1,
                                CtxMenuItem.Edit.title
                            ) //groupId, itemId, order, title
                            menu?.add(
                                bindingAdapterPosition,
                                CtxMenuItem.History.itemID,
                                2,
                                CtxMenuItem.History.title
                            )
                            menu?.add(
                                bindingAdapterPosition,
                                CtxMenuItem.Delete.itemID,
                                3,
                                CtxMenuItem.Delete.title
                            )
                        }
                        if (location == K_PAGE) {
                            menu?.setHeaderTitle(ctx.getString(R.string.barrel_menu_title))
                            menu?.add(
                                bindingAdapterPosition,
                                CtxMenuItem.EditBarrel.itemID,
                                1,
                                CtxMenuItem.EditBarrel.title
                            )
                            menu?.add(
                                bindingAdapterPosition,
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
