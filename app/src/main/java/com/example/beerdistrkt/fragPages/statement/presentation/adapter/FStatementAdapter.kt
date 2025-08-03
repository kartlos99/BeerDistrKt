package com.example.beerdistrkt.fragPages.statement.presentation.adapter

import android.graphics.Color
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.toColorInt
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.beerdistrkt.R
import com.example.beerdistrkt.databinding.StatementListRowBinding
import com.example.beerdistrkt.databinding.StatementMoneyItemBinding
import com.example.beerdistrkt.databinding.StatementSaleItemBinding
import com.example.beerdistrkt.fragPages.statement.domain.model.StatementRecordType
import com.example.beerdistrkt.fragPages.statement.model.CtxMenuItem
import com.example.beerdistrkt.fragPages.statement.presentation.model.FinanceStatementUiModel
import com.example.beerdistrkt.getAttrColor
import com.example.beerdistrkt.orZero
import com.example.beerdistrkt.setFrictionSize
import com.example.beerdistrkt.setTintFromAttr
import com.example.beerdistrkt.showToast
import com.example.beerdistrkt.utils.DefaultDiffItemCallback
import com.example.beerdistrkt.utils.hide
import java.text.DecimalFormat

class FStatementAdapter(
    private val editOldSalePermission: Boolean,
    private val editSalePermission: Boolean,
    private val isGrouped: () -> Boolean,
) : ListAdapter<FinanceStatementUiModel, RecyclerView.ViewHolder>(
    DefaultDiffItemCallback()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
//            0 -> StatementViewHolder(
//                StatementListRowBinding.inflate(LayoutInflater.from(parent.context)),
//                editOldSalePermission,
//                editSalePermission,
//                isGrouped,
//            )

            0 -> StatementMoneyItemViewHolder(
                StatementMoneyItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
            else -> StatementSaleItemViewHolder(
                StatementSaleItemBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when (item.recordType) {
            StatementRecordType.TAKE_MONEY -> (holder as StatementMoneyItemViewHolder).bind(item)
            else -> (holder as StatementSaleItemViewHolder).bind(item)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position).recordType) {
            StatementRecordType.TAKE_MONEY -> 0
            else -> 1
        }
    }

    fun getClickedItem(position: Int): FinanceStatementUiModel = super.getItem(position)

    companion object {
        const val DASH = "-"
    }

    class StatementSaleItemViewHolder(
        private val binding: StatementSaleItemBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        private val df = DecimalFormat("#0.00")

        init {
            binding.root.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        fun bind(item: FinanceStatementUiModel) = with(binding) {

            root.tag = item
            dateTv.text = item.dateStr
            commentTv.text = item.comment

            val defTextColor = root.context.getAttrColor(R.attr.mainTextColor)
            val textColor = if (item.comment.isNullOrBlank()) defTextColor else Color.MAGENTA

            val frictionColor = if (item.comment.isNullOrBlank())
                FRICTION_PART_COLOR.toColorInt()
            else
                Color.MAGENTA

            val frSize = root.resources.getDimensionPixelSize(R.dimen.sp12)
            saleTv.text = if (item.isGift) COST_FREE else
                getFormattedString(item.price, df).setFrictionSize(
                    frSize,
                    frictionColor
                )
            balanceTv.text = df.format(item.balance).setFrictionSize(frSize, frictionColor)

            saleTypeIcon.setImageResource(item.iconRes.orZero())

            saleTypeIcon.isVisible = item.iconRes != null
            saleTypeColor.isVisible = item.color != null

            if (item.color != null) {
                saleTypeIcon.setColorFilter(item.color)
                saleTypeColor.setBackgroundColor(item.color)
            } else {
                saleTypeIcon.setTintFromAttr(R.attr.mainTextColor)
            }

            detailsTv.isVisible = item.details != null
            item.details?.let {
                detailsTv.text = it
            }

            saleTv.setTextColor(textColor)
            balanceTv.setTextColor(textColor)

            if (item.comment.isNullOrBlank())
                commentTv.hide()

            root.setOnClickListener {
                if (!item.comment.isNullOrBlank())
                    commentTv.isVisible = !commentTv.isVisible
            }
        }

        private fun getFormattedString(value: Double, formatter: DecimalFormat): String =
            if (value == .0)
                DASH
            else
                formatter.format(value)

        companion object {
            private const val FRICTION_PART_COLOR = "#808080"
            private const val COST_FREE = "უფასო"
        }
    }

    class StatementMoneyItemViewHolder(
        private val binding: StatementMoneyItemBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        private val df = DecimalFormat("#0.00")

        init {
            binding.root.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        fun bind(item: FinanceStatementUiModel) = with(binding) {

            root.tag = item
            dateTv.text = item.dateStr

            commentTv.text = item.comment

            val defTextColor = root.context.getAttrColor(R.attr.mainTextColor)
            val textColor = if (item.comment.isNullOrBlank()) defTextColor else Color.MAGENTA

            val frictionColor = if (item.comment.isNullOrBlank())
                FRICTION_PART_COLOR.toColorInt()
            else
                Color.MAGENTA

            val frSize = root.resources.getDimensionPixelSize(R.dimen.sp12)
            balanceTv.text = df.format(item.balance).setFrictionSize(frSize, frictionColor)
            moneyTv.text = getFormattedString(item.pay, df).setFrictionSize(
                frSize,
                frictionColor
            )

            saleTv.setTextColor(textColor)
            moneyTv.setTextColor(textColor)
            balanceTv.setTextColor(textColor)

            if (item.comment.isNullOrBlank())
                commentTv.hide()

            root.setOnClickListener {
                if (!item.comment.isNullOrBlank())
                    commentTv.isVisible = !commentTv.isVisible
            }
        }

        private fun getFormattedString(value: Double, formatter: DecimalFormat): String =
            if (value == .0)
                DASH
            else
                formatter.format(value)

        companion object {
            private const val FRICTION_PART_COLOR = "#808080"
        }
    }

    class StatementViewHolder(
        private val binding: StatementListRowBinding,
        private val editOldSalePermission: Boolean,
        private val editSalePermission: Boolean,
        private val isGrouped: () -> Boolean,
    ) : RecyclerView.ViewHolder(binding.root), View.OnCreateContextMenuListener {

        private val df = DecimalFormat("#0.00")

        init {
            binding.root.setOnCreateContextMenuListener(this)
            binding.root.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        fun bind(item: FinanceStatementUiModel) = with(binding) {

            root.tag = item
            tAmonListTarigi.text = item.dateStr
            tAmonaweriRowComment.text = item.comment

            val defTextColor = root.context.getAttrColor(R.attr.mainTextColor)
            val textColor = if (item.comment.isNullOrBlank()) defTextColor else Color.MAGENTA

            val frictionColor = if (item.comment.isNullOrBlank())
                FRICTION_PART_COLOR.toColorInt()
            else
                Color.MAGENTA

            val frSize = root.resources.getDimensionPixelSize(R.dimen.sp12)
            tAmonListIn.text = if (item.isGift) COST_FREE else
                getFormattedString(item.price, df).setFrictionSize(
                    frSize,
                    frictionColor
                )
            tAmonListOut.text =
                getFormattedString(item.pay, df).setFrictionSize(frSize, frictionColor)
            tAmonListBalance.text =
                df.format(item.balance).setFrictionSize(frSize, frictionColor)

            recordTypeIndicator.setImageResource(item.iconRes.orZero())
//            if (item.groupGift && isGrouped())
//                recordTypeIndicator.setImageResource(R.drawable.ic_gift_24)
//            else
//                recordTypeIndicator.setImageResource(0)

//            item.recordType.icon?.let {
//                recordTypeIndicator.setImageResource(it)
//            }

            recordTypeIndicator.isVisible = item.iconRes != null
//                    || (item.groupGift && isGrouped() && location == M_PAGE)

            if (item.color != null)
                recordTypeIndicator.setColorFilter(item.color)
            else
                recordTypeIndicator.setTintFromAttr(R.attr.mainTextColor)

            tAmonListIn.setTextColor(textColor)
            tAmonListOut.setTextColor(textColor)
            tAmonListBalance.setTextColor(textColor)

            if (item.comment.isNullOrBlank())
                tAmonaweriRowComment.hide()

            root.setOnClickListener {
                if (!item.comment.isNullOrBlank())
                    tAmonaweriRowComment.isVisible =
                        tAmonaweriRowComment.visibility != View.VISIBLE
            }
        }

        private fun getFormattedString(value: Double, formatter: DecimalFormat): String =
            if (value == .0)
                DASH
            else
                formatter.format(value)

        override fun onCreateContextMenu(
            menu: ContextMenu?,
            v: View?,
            menuInfo: ContextMenu.ContextMenuInfo?
        ) {
            val ctx = itemView.context ?: return
            if (isGrouped())
                ctx.showToast(R.string.remove_grouping)
            else
                (itemView.tag as? FinanceStatementUiModel)?.let { itemData ->
//                    val itemData = it as StatementModel

//                    val selectedItemDate: Date =
//                        itemData.getItemDate(ctx.getString(R.string.patern_datetime)) ?: return

//                    val dateFormat = SimpleDateFormat(ctx.getString(R.string.patern_date))

                    if (editOldSalePermission || (itemData.isSaleToday() && editSalePermission)) {
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
                    } else
                        ctx.showToast(R.string.no_edit_access)
                }
        }

        companion object {
            private const val FRICTION_PART_COLOR = "#808080"
            private const val COST_FREE = "უფასო"
        }
    }
}
