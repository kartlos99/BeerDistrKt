package com.example.beerdistrkt.fragPages.barrel.presentation

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.beerdistrkt.BaseFragment
import com.example.beerdistrkt.R
import com.example.beerdistrkt.collectLatest
import com.example.beerdistrkt.common.adapter.SimpleDataAdapter
import com.example.beerdistrkt.databinding.BarrelOrdersFragmentBinding
import com.example.beerdistrkt.databinding.EmptyBarrelRowBinding
import com.example.beerdistrkt.fragPages.barrel.presentation.model.EmptyBarrelUiModel
import com.example.beerdistrkt.orZero
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BarrelOrdersFragment : BaseFragment<BarrelOrdersViewModel>() {

    override val viewModel: BarrelOrdersViewModel by viewModels()

    private val binding by viewBinding(BarrelOrdersFragmentBinding::bind)

    override var frLayout: Int? = R.layout.barrel_orders_fragment

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecycler()
    }

    private fun initRecycler() = with(binding.list) {

        val barrelAdapter = SimpleDataAdapter<EmptyBarrelUiModel>(
            layoutId = R.layout.empty_barrel_row,
            onBind = { item, view ->
                EmptyBarrelRowBinding.bind(view).apply {
                    setData(item)
                }
            }
        )

        layoutManager = LinearLayoutManager(requireContext())
        adapter = barrelAdapter

        viewModel.stateFlow.collectLatest(viewLifecycleOwner) { state: BarrelOrdersViewModel.State ->
            binding.loader.isVisible = state.isLoading
            state.items?.let {
                barrelAdapter.submitList(state.items)
            }
            binding.infoMessage.isVisible = state.errorMessage != null
            binding.infoMessage.text = state.errorMessage
        }
    }

    private fun EmptyBarrelRowBinding.setData(item: EmptyBarrelUiModel) {
        clientName.text = item.customerName
        orderStatus.text = "შეკვ: ${getString(item.orderStatus.textRes)}"
        outputStatusIndication.setColorFilter(item.barrelOutputStatus.color)

        val type50 = item.barrels.firstOrNull { it.barrel.id == 1 }
        orderItemCan50.setCount(type50?.inputCount.orZero(), true)
        actualItemCan50.setCount(type50?.outputCount.orZero(), true)

        val type30 = item.barrels.firstOrNull { it.barrel.id == 2 }
        orderItemCan30.setCount(type30?.inputCount.orZero(), true)
        actualItemCan30.setCount(type30?.outputCount.orZero(), true)

        val type20 = item.barrels.firstOrNull { it.barrel.id == 3 }
        orderItemCan20.setCount(type20?.inputCount.orZero(), true)
        actualItemCan20.setCount(type20?.outputCount.orZero(), true)

        val type10 = item.barrels.firstOrNull { it.barrel.id == 4 }
        orderItemCan10.setCount(type10?.inputCount.orZero(), true)
        actualItemCan10.setCount(type10?.outputCount.orZero(), true)
    }
}