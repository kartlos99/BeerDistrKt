package com.example.beerdistrkt.fragPages.expense.presentation

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.children
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.beerdistrkt.BaseFragment
import com.example.beerdistrkt.R
import com.example.beerdistrkt.collectLatest
import com.example.beerdistrkt.databinding.AddEditExpenseFragmentBinding
import com.example.beerdistrkt.fragPages.expense.domain.model.Expense
import com.example.beerdistrkt.fragPages.expense.domain.model.ExpenseCategory
import com.example.beerdistrkt.setDifferText
import com.example.beerdistrkt.showAskingDialog
import com.example.beerdistrkt.simpleTextChangeListener
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.withCreationCallback

@AndroidEntryPoint
class AddEditExpenseFragment : BaseFragment<AddEditExpenseViewModel>() {

    override val viewModel: AddEditExpenseViewModel by viewModels(
        extrasProducer = {
            defaultViewModelCreationExtras.withCreationCallback<AddEditExpenseViewModel.Factory> { factory ->
                factory.create(expense)
            }
        }
    )

    override var frLayout: Int? = R.layout.add_edit_expense_fragment

    private val binding by viewBinding(AddEditExpenseFragmentBinding::bind)

    private val expense by lazy {
        val args = AddEditExpenseFragmentArgs.fromBundle(arguments ?: Bundle())
        args.expense
    }

    override val titleRes: Int
        get() = if (expense == null) R.string.add_expense else R.string.edit_expense

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        observeData()
    }

    private fun initView() = with(binding) {
        if (expense != null)
            setupMenu(R.menu.expense_detail_manu, ::onMenuItemSelected)
        categoryChipsGroup.setOnCheckedStateChangeListener { _, checkedIds ->
            viewModel.setCategory(checkedIds.firstOrNull() ?: -1)
        }
        saveBtn.setOnClickListener {
            viewModel.onDoneClick(
                expenseAmountInput.text.toString().trim(),
                expenseCommentInput.text.toString().trim(),
                categoryChipsGroup.checkedChipId
            )
        }
        expenseCommentInput.simpleTextChangeListener {
            viewModel.setComment(it.toString())
        }
        expenseAmountInput.simpleTextChangeListener {
            viewModel.setAmount(it.toString())
        }
    }

    private fun fillForm(expense: Expense) = with(binding) {
        expenseCommentInput.setDifferText(expense.comment)
        categoryChipsGroup.children.forEach { view ->
            if (view.id == expense.category.id)
                (view as Chip).isChecked = true
        }
    }

    private fun observeData() = with(viewModel) {
        categoriesStateFlow.collectLatest(viewLifecycleOwner) { categories ->
            drawCategories(categories)
            observeInputData()
        }
        errorStateFlow.collectLatest(viewLifecycleOwner) {
            setInfo(it)
        }
        uiEventsFlow.collectLatest(viewLifecycleOwner) { uiEvent ->
            when (uiEvent) {
                AddExpenseUiEvent.GoBack -> findNavController().navigateUp()
            }
        }
    }

    private fun observeInputData() = with(viewModel) {
        expenseState.collectLatest(viewLifecycleOwner) { expense ->
            fillForm(expense)
        }
        expenseAmountState.collectLatest(viewLifecycleOwner) { amountText ->
            binding.expenseAmountInput.setDifferText(amountText)
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
                chipStrokeColor = ColorStateList.valueOf(categoryData.color)
            }
            binding.categoryChipsGroup.addView(chip)
        }
    }

    private fun setInfo(info: String?) {
        binding.infoMessage.text = info
    }

    private fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.delete -> {
                askForRemoving()
                return true
            }
        }
        return false
    }

    private fun askForRemoving() {
        context?.showAskingDialog(
            R.string.delete_expense,
            R.string.recovery_is_impossible_from_app,
            R.string.yes,
            R.string.no,
            R.style.ThemeOverlay_MaterialComponents_Dialog
        ) {
            viewModel.onDeleteClick()
        }
    }
}