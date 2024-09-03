package com.example.beerdistrkt.fragPages.expensecategory.presentation

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.beerdistrkt.BaseFragment
import com.example.beerdistrkt.R
import com.example.beerdistrkt.collectLatest
import com.example.beerdistrkt.common.domain.model.EntityStatus
import com.example.beerdistrkt.databinding.FragmentExpenseCategoryBinding
import com.example.beerdistrkt.fragPages.addBeer.ChooseColorDialog
import com.example.beerdistrkt.fragPages.expense.domain.model.ExpenseCategory
import com.example.beerdistrkt.setDifferText
import com.example.beerdistrkt.showAskingDialog
import com.example.beerdistrkt.simpleTextChangeListener
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.withCreationCallback
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ExpenseCategoryFragment : BaseFragment<ExpenseCategoryViewModel>() {


    override var frLayout: Int? = R.layout.fragment_expense_category

    private val binding by viewBinding(FragmentExpenseCategoryBinding::bind)

    private val category by lazy {
        val args = ExpenseCategoryFragmentArgs.fromBundle(arguments ?: Bundle())
        args.category
    }

    override val viewModel: ExpenseCategoryViewModel by viewModels<ExpenseCategoryViewModel>(
        extrasProducer = {
            defaultViewModelCreationExtras.withCreationCallback<ExpenseCategoryViewModel.Factory> { factory ->
                factory.create(category ?: ExpenseCategory.newInstance())
            }
        })

    override val titleRes: Int
        get() = if (category == null) R.string.add_category else R.string.edit_category

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
            R.string.remove_category,
            R.string.recovery_is_impossible_from_app,
            R.string.yes,
            R.string.no,
            R.style.ThemeOverlay_MaterialComponents_Dialog
        ) {
            viewModel.deleteCategory()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        observeData()
        setResultListeners()
    }

    private fun initView() = with(binding) {
        if (category != null)
            setupMenu(R.menu.expense_category_detail_manu, ::onMenuItemSelected)
        saveBtn.setOnClickListener {
            viewModel.onSaveClick(
                nameInput.text.toString().trim(),
                EntityStatus.fromDisplayName(requireContext(), statusInput.text.toString()),
            )
        }
        colorBtn.setOnClickListener {
            ChooseColorDialog.getInstance(colorBtn.backgroundTintList?.defaultColor)
                .show(childFragmentManager, ChooseColorDialog.TAG)
        }
        nameInput.simpleTextChangeListener { text ->
            viewModel.setName(text.toString().trim())
        }
        statusInput.simpleTextChangeListener {
            viewModel.setStatus(
                EntityStatus.fromDisplayName(requireContext(), statusInput.text.toString())
            )
        }
    }

    private fun setResultListeners() {
        childFragmentManager.setFragmentResultListener(
            ChooseColorDialog.COLOR_SELECTOR_REQUEST_KEY, viewLifecycleOwner
        ) { _, bundle ->
            viewModel.setColor(bundle.getInt(ChooseColorDialog.SELECTED_COLOR_KEY))
        }
    }

    private fun setupStatusDropDown(categories: List<Int>) {
        binding.statusInput.setAdapter(ArrayAdapter(
            requireContext(),
            R.layout.support_simple_spinner_dropdown_item,
            categories.map { getString(it) }
        ))
    }

    private fun fillForm(expenseCategory: ExpenseCategory) = with(binding) {
        nameInput.setDifferText(expenseCategory.name)
        statusInput.setText(getString(expenseCategory.status.displayName), false)
        colorBtn.backgroundTintList = ColorStateList.valueOf(expenseCategory.color)
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
        viewModel.categoryState.collectLatest(viewLifecycleOwner) { category ->
            fillForm(category)
        }
    }

    private fun updateUi(uiState: UiState) = with(binding) {
        infoMessage.isVisible = uiState.error != null
        infoMessage.text = uiState.error
        if (uiState.successResult != null) {
            when (uiState.successResult) {
                SuccessOf.DELETE -> showToast(R.string.is_deleted)
                SuccessOf.UPDATE -> showToast(R.string.data_saved)
            }
            lifecycleScope.launch {
                delay(AUTO_BACK_DELAY)
                findNavController().navigateUp()
            }
        }
    }

}