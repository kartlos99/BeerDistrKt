package com.example.beerdistrkt.fragPages.statement.presentation.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.graphics.toColorInt
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.beerdistrkt.R
import com.example.beerdistrkt.common.adapter.SimpleDataAdapter
import com.example.beerdistrkt.databinding.FinanceStatementItemBinding
import com.example.beerdistrkt.databinding.StatementSaleSubItemBinding
import com.example.beerdistrkt.fragPages.statement.presentation.adapter.FStatementAdapter.Companion.DASH
import com.example.beerdistrkt.fragPages.statement.presentation.model.FStatementUiItem
import com.example.beerdistrkt.fragPages.statement.presentation.model.SaleItemUiModel
import com.example.beerdistrkt.getAttrColor
import com.example.beerdistrkt.setFrictionSize
import com.example.beerdistrkt.setTimeSize
import com.example.beerdistrkt.utils.DefaultDiffItemCallback
import com.example.beerdistrkt.utils.MINUS_SIGN
import com.example.beerdistrkt.utils.PLUS_SIGN
import com.example.beerdistrkt.utils.hide
import com.example.beerdistrkt.utils.show
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
                FinanceStatementItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
//                editOldSalePermission,
//                editSalePermission,
//                isGrouped,
            )

            VIEW_TYPE_MONEY -> StatementMoneyItemViewHolder(
                FinanceStatementItemBinding.inflate(
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
            is FStatementUiItem.Sale -> (holder as StatementSaleItemViewHolder).bind(item) {
                notifyItemChanged(position)
            }
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

        const val AMOUNT_PATTERN = "#0.00₾"
    }

    class StatementSaleItemViewHolder(
        private val binding: FinanceStatementItemBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        private val df = DecimalFormat(AMOUNT_PATTERN)

        private val frSize by lazy {
            binding.root.resources.getDimensionPixelSize(R.dimen.sp12)
        }

        private val subItemsAdapter by lazy(LazyThreadSafetyMode.NONE) {
            SimpleDataAdapter<SaleItemUiModel>(
                layoutId = R.layout.statement_sale_sub_item,
                onBind = { subItem, view ->
                    StatementSaleSubItemBinding.bind(view).apply {
                        priceTv.text = df.customFormat(subItem.price).setFrictionSize(frSize)
                        detailsTv.text = subItem.details
                        subItem.recordType.icon?.let { saleTypeIcon.setImageResource(it) }
                        subItem.itemColor?.let { saleTypeColor.setBackgroundColor(it) }
                        saleTypeColor.isVisible = subItem.itemColor != null
                        itemName.text = subItem.productName
                    }
                }
            )
        }

        init {
            with(binding) {
                operationDelta.setTextColor(root.context.getAttrColor(R.attr.colorSale))
                statementIcon.backgroundTintList =
                    ColorStateList.valueOf(root.context.getAttrColor(R.attr.colorSaleIconBkg))
                statementIcon.imageTintList =
                    ColorStateList.valueOf(root.context.getAttrColor(R.attr.colorSale))
                statementIcon.setImageResource(R.drawable.ic_delivery)

                saleItemsRc.adapter = subItemsAdapter
            }
        }

        fun bind(item: FStatementUiItem.Sale, updateItem: () -> Unit) = with(binding) {
            root.tag = item
            saleItemsRc.isVisible = item.isExpanded
            subItemSeparatorLine.isVisible = item.isExpanded
            expandDetailsImg.rotation = if (item.isExpanded) 180f else 0f
            expandDetailsImg.setOnClickListener {
                item.isExpanded = !item.isExpanded
                updateItem()
            }

            statementDate.text = item.dateStr.setTimeSize(frSize)

            operationDelta.text = if (item.isGift) COST_FREE else
                df.customFormat(item.price, MINUS_SIGN).setFrictionSize(frSize)

            val balanceColor = root.context
                .getAttrColor(if (item.balance > 0) R.attr.colorWarning else R.attr.mainTextColor)
            balance.setTextColor(balanceColor)
            balance.text = df.format(item.balance).setFrictionSize(frSize)

            comment.isVisible = !item.comment.isNullOrBlank()
            comment.text = item.comment

            giftIcon.isVisible = item.isGift
            subItemsAdapter.submitList(item.items)
        }

        companion object {
            private const val COST_FREE = "უფასო"
        }
    }


    class StatementMoneyItemViewHolder(
        private val binding: FinanceStatementItemBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        private val df = DecimalFormat(AMOUNT_PATTERN)

        init {
            binding.expandDetailsImg.isVisible = false

            binding.saleItemsRc.hide()
            binding.subItemSeparatorLine.hide()
            binding.operationDelta.setTextColor(binding.root.context.getAttrColor(R.attr.colorPayment))
            binding.statementIcon.backgroundTintList =
                ColorStateList.valueOf(binding.root.context.getAttrColor(R.attr.colorPaymentIconBkg))
            binding.statementIcon.imageTintList =
                ColorStateList.valueOf(binding.root.context.getAttrColor(R.attr.colorPayment))
            binding.statementIcon.setImageResource(R.drawable.ic_cash)
        }

        fun bind(item: FStatementUiItem.Money) = with(binding) {

            root.tag = item
            val frSize = root.resources.getDimensionPixelSize(R.dimen.sp12)
            statementDate.text = item.dateStr.setTimeSize(frSize)

            comment.isVisible = !item.comment.isNullOrBlank()
            comment.text = item.comment

            val balanceColor = root.context.getAttrColor(
                if (item.balance > 0) R.attr.colorWarning else R.attr.mainTextColor
            )

            balance.text = df.format(item.balance).setFrictionSize(frSize)
            operationDelta.text = df.customFormat(item.pay, PLUS_SIGN).setFrictionSize(frSize)

            balance.setTextColor(balanceColor)

        }

    }

}

fun DecimalFormat.customFormat(value: Double, sign: String? = null): String =
    if (value == .0)
        DASH
    else
        sign.orEmpty() + this.format(value)
