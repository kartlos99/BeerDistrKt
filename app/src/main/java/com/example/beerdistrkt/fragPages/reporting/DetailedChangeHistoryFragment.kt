package com.example.beerdistrkt.fragPages.reporting

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.beerdistrkt.BaseFragment
import com.example.beerdistrkt.R
import com.example.beerdistrkt.databinding.FragmentDetailedChangeHinstoryBinding
import com.example.beerdistrkt.fragPages.reporting.adapter.ChangesHistoryAdapter
import com.example.beerdistrkt.fragPages.reporting.model.DbTableName
import com.example.beerdistrkt.fragPages.reporting.model.HistoryCellType
import com.example.beerdistrkt.fragPages.reporting.model.HistoryUnitModel
import com.example.beerdistrkt.utils.ApiResponseState

class DetailedChangeHistoryFragment : BaseFragment<DetailedChangeHistoryViewModel>() {

    override val viewModel: DetailedChangeHistoryViewModel by viewModels()

    val binding by viewBinding(FragmentDetailedChangeHinstoryBinding::bind)

    override var frLayout: Int? = R.layout.fragment_detailed_change_hinstory

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecycler()

        arguments?.let {
            val id = it.getString(RECORD_ID_KEY) ?: return@let
            val table = it.getString(DB_TABLE_KEY) ?: return@let
            setItem(id, table)
        }
    }

    private fun setupRecycler(columnCount: Int = 0) = with(binding) {
        val adapter = ChangesHistoryAdapter()
        val layoutManager = GridLayoutManager(requireContext(), columnCount + 1)
        detailedChangesRv.layoutManager = layoutManager
        detailedChangesRv.adapter = adapter

        viewModel.historyLiveData.observe(viewLifecycleOwner) { historyResult ->
            when (historyResult){
                is ApiResponseState.Loading -> {
                    binding.indeterminateProgressBar.isVisible = historyResult.showLoading
                    if (historyResult.showLoading)
                        adapter.submitList(listOf(HistoryUnitModel(getString(R.string.loading), HistoryCellType.Empty)))
                }
                is ApiResponseState.Success -> adapter.submitList(historyResult.data)
                is ApiResponseState.ApiError -> showToast(historyResult.errorText)
                else -> {}
            }
        }
    }

    fun setItem(id: String, tableStr: String) {
        val table = DbTableName.valueOf(tableStr)
        setupRecycler(table.visibleKeys.size)
        viewModel.getHistory(id, table)
    }

    companion object {
        const val RECORD_ID_KEY = "RECORD_ID_KEY"
        const val DB_TABLE_KEY = "DB_TABLE_KEY"
    }
}