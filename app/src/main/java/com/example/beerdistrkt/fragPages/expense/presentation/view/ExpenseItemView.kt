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

    var onRemoveClick: ((id: String) -> Unit)? = null

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
        author: String,
        canDelete: Boolean = false
    ) = with(binding) {
        comment.text = expense.comment
        expenseAuthor.text = author
        expenseAmount.text = context.getString(R.string.format_gel, expense.amount)
        removeBtn.isVisible = canDelete && expense.id != null

        removeBtn.setOnClickListener {
            expense.id?.let {
                onRemoveClick?.invoke(expense.id)
            } ?: throw Exception("has no ID!")
        }
    }
}