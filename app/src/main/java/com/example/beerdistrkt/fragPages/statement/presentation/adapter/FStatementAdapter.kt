package com.example.beerdistrkt.fragPages.statement.presentation.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.graphics.toColorInt
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.beerdistrkt.R
import com.example.beerdistrkt.common.adapter.SimpleDataAdapter
import com.example.beerdistrkt.databinding.StatementMoneyItemBinding
import com.example.beerdistrkt.databinding.StatementSaleItemOldBinding
import com.example.beerdistrkt.databinding.StatementSaleSubItemBinding
import com.example.beerdistrkt.fragPages.statement.presentation.model.FStatementUiItem
import com.example.beerdistrkt.fragPages.statement.presentation.model.SaleItemUiModel
import com.example.beerdistrkt.getAttrColor
import com.example.beerdistrkt.setFrictionSize
import com.example.beerdistrkt.utils.DefaultDiffItemCallback
import com.example.beerdistrkt.utils.hide
import java.text.DecimalFormat

class FStatementAdapter(
    private val editOldSalePermission: Boolean,
    private val editSalePermission: Boolean,
    private val isGrouped: () -> Boolean,
) : ListAdapter<FStatementUiItem, RecyclerView.ViewHolder>(
    DefaultDiffItemCallback()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_SALE -> StatementSaleItemViewHolder(
                StatementSaleItemOldBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
//                editOldSalePermission,
//                editSalePermission,
//                isGrouped,
            )

            VIEW_TYPE_MONEY -> StatementMoneyItemViewHolder(
                StatementMoneyItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )

            else -> throw NoSuchElementException("Unknown view type - FStatementAdapter")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is FStatementUiItem.Money -> (holder as StatementMoneyItemViewHolder).bind(item)
            is FStatementUiItem.Sale -> (holder as StatementSaleItemViewHolder).bind(item)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is FStatementUiItem.Money -> VIEW_TYPE_MONEY
            is FStatementUiItem.Sale -> VIEW_TYPE_SALE
        }
    }

//    fun getClickedItem(position: Int): FinanceStatementUiModel = super.getItem(position)

    companion object {
        const val DASH = "-"
        const val VIEW_TYPE_MONEY = 1
        const val VIEW_TYPE_SALE = 2
    }

    class StatementSaleItemViewHolder(
        private val binding: StatementSaleItemOldBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        private val df = DecimalFormat("#0.00")

        init {
            binding.root.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        fun bind(item: FStatementUiItem.Sale) = with(binding) {

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
            groupPriceTv.text = if (item.isGift) COST_FREE else
                getFormattedString(item.price, df).setFrictionSize(
                    frSize,
                    frictionColor
                )
            balanceTv.text = df.format(item.balance).setFrictionSize(frSize, frictionColor)
            giftImg.isVisible = item.isGift

//            saleTypeIcon.setImageResource(item.iconRes.orZero())
//
//            saleTypeIcon.isVisible = item.iconRes != null
//            saleTypeColor.isVisible = item.color != null
//
//            if (item.color != null) {
//                saleTypeIcon.setColorFilter(item.color)
//                saleTypeColor.setBackgroundColor(item.color)
//            } else {
//                saleTypeIcon.setTintFromAttr(R.attr.mainTextColor)
//            }

            val subItemsAdapter = SimpleDataAdapter<SaleItemUiModel>(
                layoutId = R.layout.statement_sale_sub_item,
                onBind = { subItem, view ->
                    StatementSaleSubItemBinding.bind(view).apply {
                        priceTv.setTextColor(textColor)
                        priceTv.text = getFormattedString(subItem.price, df).setFrictionSize(
                            frSize,
                            frictionColor
                        )
                        detailsTv.text = subItem.details
                        subItem.recordType.icon?.let { saleTypeIcon.setImageResource(it) }
                        subItem.itemColor?.let { saleTypeColor.setBackgroundColor(it) }
                        saleTypeColor.isVisible = subItem.itemColor != null
                    }
                }
            )
            saleItemsRc.adapter = subItemsAdapter
            saleItemsRc.layoutManager = LinearLayoutManager(root.context)
            subItemsAdapter.submitList(item.items)

//            detailsTv.isVisible = item.items.isNotEmpty()
//            item.items.firstOrNull()?.let {
//                detailsTv.text = it.details
//            }

            groupPriceTv.setTextColor(textColor)
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

        fun bind(item: FStatementUiItem.Money) = with(binding) {

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

    /*class StatementViewHolder(
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
                getFormattedString(item.price, df).setFrictionSize(frSize, frictionColor)
            tAmonListOut.text =
                getFormattedString(item.pay, df).setFrictionSize(frSize, frictionColor)
            tAmonListBalance.text =
                df.format(item.balance).setFrictionSize(frSize, frictionColor)

            recordTypeIndicator.setImageResource(item.iconRes.orZero())
            recordTypeIndicator.isVisible = item.iconRes != null

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
        *//*

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
    }*/
}
