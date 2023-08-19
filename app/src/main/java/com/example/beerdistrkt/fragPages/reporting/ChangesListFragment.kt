package com.example.beerdistrkt.fragPages.reporting

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.BaseAdapter
import android.widget.SimpleAdapter
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.beerdistrkt.BaseFragment
import com.example.beerdistrkt.R
import com.example.beerdistrkt.adapters.SimpleListAdapter
import com.example.beerdistrkt.databinding.ChangesMainListItemLayoutBinding
import com.example.beerdistrkt.databinding.FragmentChangesListBinding
import com.example.beerdistrkt.databinding.ViewMoneyHistoryItemBinding
import com.example.beerdistrkt.fragPages.reporting.DetailedChangeHistoryFragment.Companion.RECORD_ID_KEY
import com.example.beerdistrkt.fragPages.showHistory.SalesHistoryFragment
import com.example.beerdistrkt.utils.visibleIf

class ChangesListFragment : BaseFragment<ChangesListViewModel>() {

    override val viewModel: ChangesListViewModel by viewModels()

    override var frLayout: Int? = R.layout.fragment_changes_list

    val binding by viewBinding(FragmentChangesListBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.initView()
    }

    fun FragmentChangesListBinding.initView() {
        changesListRv.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        changesListRv.adapter = SimpleListAdapter(
            listOf("one", "second"),
            null,
            R.layout.changes_main_list_item_layout,
            onBind = { item, view ->
                val itemBinding = ChangesMainListItemLayoutBinding.bind(view)
                itemBinding.itemName.text = item
            },
            onClick = {
                openDetails(it)
            }
        )
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