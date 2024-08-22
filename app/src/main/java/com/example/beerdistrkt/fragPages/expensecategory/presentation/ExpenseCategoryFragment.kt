package com.example.beerdistrkt.fragPages.expensecategory.presentation

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.beerdistrkt.BaseFragment
import com.example.beerdistrkt.R
import com.example.beerdistrkt.common.domain.model.EntityStatus
import com.example.beerdistrkt.databinding.FragmentExpenseCategoryBinding
import com.example.beerdistrkt.fragPages.expense.domain.model.ExpenseCategory
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ExpenseCategoryFragment : BaseFragment<ExpenseCategoryViewModel>() {


    override var frLayout: Int? = R.layout.fragment_expense_category

    private val binding by viewBinding(FragmentExpenseCategoryBinding::bind)

    private val category by lazy {
        val args = ExpenseCategoryFragmentArgs.fromBundle(arguments ?: Bundle())
        args.category
    }

    @Inject
    lateinit var factory: ExpenseCategoryViewModel.Factory

    override val viewModel: ExpenseCategoryViewModel by lazy {
        factory.create(category)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        observeData()
    }

    private fun initView() = with(binding) {
        category?.let {
            fillForm(it)
        }
        saveBtn.setOnClickListener {
            viewModel.onSaveClick(
                nameInput.text.toString().trim(),
                EntityStatus.fromDisplayName(requireContext(), statusInput.text.toString()),
                "#ee2222"
            )
        }
        colorBtn.setOnClickListener { }
    }

    private fun setupStatusDropDown(categories: List<Int>) {
        binding.statusInput.setAdapter(ArrayAdapter(
            requireContext(),
            R.layout.support_simple_spinner_dropdown_item,
            categories.map { getString(it) }
        ))
    }

    private fun fillForm(expenseCategory: ExpenseCategory) = with(binding) {
        nameInput.setText(expenseCategory.name)
        statusInput.setText(getString(expenseCategory.status.displayName), false)
    }

    private fun observeData() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.screenStateFlow.collectLatest {
                    updateUi(it)
                }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.categoryStatusesStateFlow.collectLatest {
                    setupStatusDropDown(it)
                }
            }
        }
    }

    private fun updateUi(uiState: UiState) = with(binding) {
        infoMessage.isVisible = uiState.error != null
        infoMessage.text = uiState.error
        if (uiState.successResult != null)
            showToast(R.string.data_saved)
    }

}