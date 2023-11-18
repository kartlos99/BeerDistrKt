package com.example.beerdistrkt.fragPages.reporting

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.beerdistrkt.BaseFragment
import com.example.beerdistrkt.MainActivity
import com.example.beerdistrkt.R
import com.example.beerdistrkt.collectLatest
import com.example.beerdistrkt.databinding.FragmentDetailedChangeHinstoryBinding
import com.example.beerdistrkt.fragPages.reporting.adapter.ChangesHistoryAdapter
import com.example.beerdistrkt.fragPages.reporting.model.DbTableName
import com.example.beerdistrkt.fragPages.reporting.model.HistoryCellType
import com.example.beerdistrkt.fragPages.reporting.model.HistoryUnitModel
import com.example.beerdistrkt.utils.ApiResponseState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
        binding.fullScreenToggle.setOnClickListener {
            viewModel.toggleFullScreen()
        }
        setObservers()
    }

    private fun setObservers() {
        viewModel.fullScreenState.collectLatest(viewLifecycleOwner) { isFullScreen ->
            handleFullScreenChange(isFullScreen)
        }
    }

    private fun handleFullScreenChange(isFullScreen: Boolean) = lifecycleScope.launch {
        delay(DELAY_FOR_FULL_SCREEN)
        if (isFullScreen) {
            (requireActivity() as MainActivity).setFullScreen()
            binding.fullScreenToggle.setImageResource(R.drawable.fullscreen_exit_24)
        } else {
            (requireActivity() as MainActivity).exitFullScreen()
            binding.fullScreenToggle.setImageResource(R.drawable.fullscreen_24)
        }
    }

    private fun setupRecycler(columnCount: Int = 0) = with(binding) {
        val adapter = ChangesHistoryAdapter()
        val layoutManager = GridLayoutManager(requireContext(), columnCount + 1)
        detailedChangesRv.layoutManager = layoutManager
        detailedChangesRv.adapter = adapter

        viewModel.historyLiveData.observe(viewLifecycleOwner) { historyResult ->
            when (historyResult) {
                is ApiResponseState.Loading -> {
                    val dataWhileLoading = listOf(
                        HistoryUnitModel(getString(R.string.loading), HistoryCellType.Empty)
                    )
                    binding.indeterminateProgressBar.isVisible = historyResult.showLoading
                    if (historyResult.showLoading)
                        adapter.submitList(dataWhileLoading)
                }

                is ApiResponseState.Success -> adapter.submitList(historyResult.data)
                is ApiResponseState.ApiError -> showToast(historyResult.errorText)
                ApiResponseState.Sleep -> {
                    adapter.submitList(listOf())
                }
                else -> {}
            }
        }
    }

    fun setItem(id: String, tableStr: String) {
        val table = DbTableName.valueOf(tableStr)
        setPageTitle(table.displayName)
        setupRecycler(table.visibleKeys.size)
        viewModel.getHistory(id, table)
    }

    companion object {
        const val RECORD_ID_KEY = "RECORD_ID_KEY"
        const val DB_TABLE_KEY = "DB_TABLE_KEY"

        private const val DELAY_FOR_FULL_SCREEN = 100L
    }
}