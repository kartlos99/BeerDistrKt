package com.example.beerdistrkt.fragPages.expense.presentation.view

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.example.beerdistrkt.R
import com.example.beerdistrkt.databinding.ExpenseItemLayoutBinding
import com.example.beerdistrkt.fragPages.expense.domain.model.Expense

class ExpenseItemView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    var onOptionClick: ((expense: Expense) -> Unit)? = null

    private val binding =
        ExpenseItemLayoutBinding.bind(inflate(context, R.layout.expense_item_layout, this))

    init {
        layoutParams = LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    fun setData(
        expense: Expense,
        canDelete: Boolean = false
    ) = with(binding) {
        comment.text = expense.comment
        expenseAuthor.text = expense.distributor.username
        expenseAmount.text = context.getString(R.string.format_gel, expense.amount)
        optionsBtn.isVisible = canDelete && expense.id != null
        categoryIndicator.setBackgroundColor(expense.category.color)

        optionsBtn.setOnClickListener {
            expense.id?.let {
                onOptionClick?.invoke(expense)
            } ?: throw Exception("expense has no ID!")
        }
    }
}