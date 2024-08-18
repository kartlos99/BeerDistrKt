package com.example.beerdistrkt.fragPages.expense.presentation

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.beerdistrkt.BaseFragment
import com.example.beerdistrkt.R
import com.example.beerdistrkt.databinding.AddEditExpenseFragmentBinding
import com.example.beerdistrkt.fragPages.expense.domain.model.ExpenseCategory
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddEditExpenseFragment : BaseFragment<AddEditExpenseViewModel>() {

    override val viewModel: AddEditExpenseViewModel by viewModels()

    override var frLayout: Int? = R.layout.add_edit_expense_fragment

    private val binding by viewBinding(AddEditExpenseFragmentBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        observeData()
    }

    private fun initView() = with(binding) {
        categoryChipsGroup.setOnClickListener {
            setInfo(null)
        }
        saveBtn.setOnClickListener {
            viewModel.onDoneClick(
                expenseAmountInput.text.toString(),
                expenseCommentInput.text.toString(),
                categoryChipsGroup.checkedChipId
            )
        }
    }

    private fun observeData() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.categoriesStateFlow.collectLatest {
                    drawCategories(it)
                }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.errorStateFlow.collectLatest {
                    setInfo(it)
                }
            }
        }
    }

    private fun drawCategories(expenseCategories: List<ExpenseCategory>) {
        binding.categoryChipsGroup.removeAllViews()
        expenseCategories.forEach { categoryData ->
            val chip = Chip(context).apply {
                id = categoryData.id ?: -1
                text = categoryData.name
                checkedIcon = ResourcesCompat.getDrawable(resources, R.drawable.check_24, null)
                isCheckable = true
                chipStrokeWidth = 4f
                chipStrokeColor = ColorStateList.valueOf(Color.parseColor(categoryData.color))
            }
            binding.categoryChipsGroup.addView(chip)
        }
    }

    private fun setInfo(info: String?) {
        binding.infoMessage.text = info
    }
}