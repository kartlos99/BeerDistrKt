package com.example.beerdistrkt.fragPages.reporting

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.beerdistrkt.BaseFragment
import com.example.beerdistrkt.R
import com.example.beerdistrkt.collectLatest
import com.example.beerdistrkt.databinding.FragmentChangesListBinding
import com.example.beerdistrkt.fragPages.reporting.DetailedChangeHistoryFragment.Companion.RECORD_ID_KEY
import com.example.beerdistrkt.fragPages.reporting.adapter.ChangesMainListAdapter
import com.example.beerdistrkt.fragPages.reporting.adapter.SimplePaginatedScrollListener
import com.example.beerdistrkt.fragPages.reporting.model.ChangesShortDto

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
        openDetails(item.id.toString())
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
    }

    private fun setupObservers() {
        viewModel.changesFlow.collectLatest(viewLifecycleOwner) { chList ->
            Log.d(TAG, "setupObservers: $chList")
            changesAdapter.submitList(chList)
        }
    }

    private fun getNextPage() {
        Log.d(TAG, "getNextPage: TODO")
    }

    private fun openDetails(id: String) {
        if (isLandscape) {
            val fragment =
                binding.detailsFragmentContainer?.getFragment<DetailedChangeHistoryFragment>()
            fragment?.setItem(id)
        } else {
            val args = Bundle().apply {
                putString(RECORD_ID_KEY, id)
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