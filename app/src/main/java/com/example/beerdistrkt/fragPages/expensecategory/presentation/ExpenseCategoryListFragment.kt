package com.example.beerdistrkt.fragPages.expensecategory.presentation

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.beerdistrkt.BaseFragment
import com.example.beerdistrkt.R
import com.example.beerdistrkt.adapters.SimpleListAdapter
import com.example.beerdistrkt.databinding.ExpenseCategoryRowBinding
import com.example.beerdistrkt.databinding.FragmentExpenseCategoryListBinding
import com.example.beerdistrkt.fragPages.expense.domain.model.ExpenseCategory
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ExpenseCategoryListFragment: BaseFragment<ExpenseCategoryListViewModel>() {

    override val viewModel: ExpenseCategoryListViewModel by viewModels()

    override var frLayout: Int? = R.layout.fragment_expense_category_list

    private val binding by viewBinding(FragmentExpenseCategoryListBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        observeData()
    }

    private fun initView() = with(binding) {
        addBtn.setOnClickListener {
//            go add page
        }
    }


    private fun observeData() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.categoriesStateFlow.collectLatest {
                    initRecycler(it)
                }
            }
        }
    }

    private fun initRecycler(category: List<ExpenseCategory>) = with(binding.recycler) {
        layoutManager = LinearLayoutManager(requireContext())
        adapter = SimpleListAdapter(
            data = category,
            layoutId = R.layout.expense_category_row,
            onBind = { item, view ->
                ExpenseCategoryRowBinding.bind(view).apply {
                    name.text = item.name
                    status.text = item.status.name
                    indicator.setBackgroundColor(Color.parseColor(item.color))
                    dotsImg.setOnClickListener {
                        item.id?.let {
                            openDetails(it)
                        } ?: showToast("araswori monacemebi")
                    }
                }
            }
        )
    }

    private fun openDetails(itemID: Int) {
        Log.d(TAG, "openDetails: $itemID")
//        findNavController().navigate(
//            BottleListFragmentDirections.actionBottleListFragmentToBottleDetailFragment(
//                bottleID
//            )
//        )
    }

}