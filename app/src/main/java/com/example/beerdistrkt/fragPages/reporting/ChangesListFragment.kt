package com.example.beerdistrkt.fragPages.reporting

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.beerdistrkt.BaseFragment
import com.example.beerdistrkt.MainActivity
import com.example.beerdistrkt.R
import com.example.beerdistrkt.collectLatest
import com.example.beerdistrkt.databinding.FragmentChangesListBinding
import com.example.beerdistrkt.fragPages.reporting.DetailedChangeHistoryFragment.Companion.DB_TABLE_KEY
import com.example.beerdistrkt.fragPages.reporting.DetailedChangeHistoryFragment.Companion.RECORD_ID_KEY
import com.example.beerdistrkt.fragPages.reporting.adapter.ChangesMainListAdapter
import com.example.beerdistrkt.fragPages.reporting.adapter.SimplePaginatedScrollListener
import com.example.beerdistrkt.fragPages.reporting.model.ChangesShortDto
import com.example.beerdistrkt.fragPages.reporting.model.DbTableName
import com.example.beerdistrkt.utils.ApiResponseState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChangesListFragment : BaseFragment<ChangesListViewModel>() {

    override val viewModel: ChangesListViewModel by viewModels()

    override var frLayout: Int? = R.layout.fragment_changes_list

    val binding by viewBinding(FragmentChangesListBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.initView()
    }

    private val changesAdapter = ChangesMainListAdapter().apply {
        onItemClick = ::onItemClicked
    }

    private fun onItemClicked(item: ChangesShortDto) {
        openDetails(item.editedRecordID.toString(), item.tableName)
    }

    fun FragmentChangesListBinding.initView() {
        val layoutManager = LinearLayoutManager(requireContext())
        val scrollListener = SimplePaginatedScrollListener(layoutManager).apply {
            getNext = ::getNextPage
        }
        changesListRv.apply {
            this.layoutManager = layoutManager
            adapter = changesAdapter
            addOnScrollListener(scrollListener)
        }
        setupObservers()
        toggleChangesList?.setOnClickListener {
            viewModel.toggleChangesList()
        }
        if (isLandscape) {
            childFragmentManager.beginTransaction()
                .replace(R.id.detailsFragmentContainer, DetailedChangeHistoryFragment())
                .commit()
        }
    }

    private fun setupObservers() {
        viewModel.changesLiveData.observe(viewLifecycleOwner) { changesResult ->
            when (changesResult) {
                is ApiResponseState.Loading -> binding.indeterminateProgressBar.isVisible =
                    changesResult.showLoading

                is ApiResponseState.Success -> changesAdapter.submitList(changesResult.data)
                is ApiResponseState.ApiError -> showToast(changesResult.errorText)
                else -> {}
            }
        }
        viewModel.changesListVisibilityState.collectLatest(viewLifecycleOwner) {
            handleChangesListVisibility(it)
        }
        lifecycleScope.launch {
            delay(1800)
            val mActivity = (requireActivity() as MainActivity)
            if (mActivity.isDestroyed.not() && mActivity.isFinishing.not() && isResumed)
                if (isLandscape.not()) {
                    mActivity.exitFullScreen()
                }
        }
    }

    private fun handleChangesListVisibility(show: Boolean) = lifecycleScope.launch {
        if (isLandscape) {
            if (show) {
                binding.detailsDivider?.setGuidelinePercent(0.4f)
                binding.toggleChangesList?.setImageResource(R.drawable.ic_arrow_left_24dp)
            } else {
                binding.detailsDivider?.setGuidelinePercent(0.0f)
                binding.toggleChangesList?.setImageResource(R.drawable.ic_arrow_right_24dp)
            }
        }
    }

    private fun getNextPage() {
        Log.d(TAG, "getNextPage: TODO")
    }

    private fun getDetailsFragment(): DetailedChangeHistoryFragment? {
        return if (binding.detailsFragmentContainer != null)
            childFragmentManager.findFragmentById(R.id.detailsFragmentContainer) as? DetailedChangeHistoryFragment
        else
            null
    }

    private fun openDetails(id: String, tableName: DbTableName) {
        if (isLandscape) {
            val fragment = getDetailsFragment()
            fragment?.setItem(id, tableName.name)
        } else {
            val args = Bundle().apply {
                putString(RECORD_ID_KEY, id)
                putString(DB_TABLE_KEY, tableName.name)
            }
            findNavController().navigate(
                R.id.action_changesListFragment_to_detailedChangeHistoryFragment,
                args
            )
        }
    }

    private val isLandscape: Boolean
        get() = binding.detailsFragmentContainer != null &&
                binding.detailsFragmentContainer!!.isVisible
}