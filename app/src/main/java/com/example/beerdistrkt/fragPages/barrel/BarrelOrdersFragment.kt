package com.example.beerdistrkt.fragPages.barrel

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.beerdistrkt.BaseFragment
import com.example.beerdistrkt.R
import com.example.beerdistrkt.collectLatest
import com.example.beerdistrkt.databinding.BarrelOrdersFragmentBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BarrelOrdersFragment : BaseFragment<BarrelOrdersViewModel>() {

    override val viewModel: BarrelOrdersViewModel by viewModels()

    private val binding by viewBinding(BarrelOrdersFragmentBinding::bind)

    override var frLayout: Int? = R.layout.barrel_orders_fragment

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initData()
        observeData()
    }

    private fun initData() {

    }

    private fun observeData() {
        viewModel.stateFlow.collectLatest(viewLifecycleOwner) { state: BarrelOrdersViewModel.State ->
            binding.loader.isVisible = state.isLoading

        }
    }
}