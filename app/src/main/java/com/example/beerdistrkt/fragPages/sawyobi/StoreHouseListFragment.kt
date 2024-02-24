package com.example.beerdistrkt.fragPages.sawyobi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.beerdistrkt.BaseFragment
import com.example.beerdistrkt.R
import com.example.beerdistrkt.databinding.SawyobiListFragmentBinding
import com.example.beerdistrkt.fragPages.sawyobi.adapters.StorehousePagedAdapter
import com.example.beerdistrkt.getViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class StoreHouseListFragment : BaseFragment<StoreHouseListViewModel>() {

    companion object {
        fun newInstance() = StoreHouseListFragment()
        var editingGroupID: String = ""
    }

    private val binding by viewBinding(SawyobiListFragmentBinding::bind)

    override val viewModel by lazy {
        getViewModel { StoreHouseListViewModel() }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.sawyobi_list_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupPagedAdapter()
    }

    private fun SawyobiListFragmentBinding.bindAdapter(
        adapter: StorehousePagedAdapter
    ) {
        sHLRecycler.adapter = adapter
        sHLRecycler.layoutManager = LinearLayoutManager(sHLRecycler.context)
//        val decoration = DividerItemDecoration(sHLRecycler.context, DividerItemDecoration.VERTICAL)
//        sHLRecycler.addItemDecoration(decoration)
    }

    private fun setupPagedAdapter() {

        val adapter = StorehousePagedAdapter()
        binding.bindAdapter(adapter)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                adapter.loadStateFlow.collect {
                    val appendState = it.source.append
                    binding.progressIndicator.isVisible = it.source.append is LoadState.Loading
                    if (appendState is LoadState.Error)
                        showToast(appendState.error.message)
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.items.collectLatest {
                    adapter.submitData(it)
                }
            }
        }
    }

    private fun onItemLongClick(groupID: String) {
        editingGroupID = groupID
        findNavController().navigateUp()
    }

    override fun onStart() {
        super.onStart()
        editingGroupID = ""
    }
}
