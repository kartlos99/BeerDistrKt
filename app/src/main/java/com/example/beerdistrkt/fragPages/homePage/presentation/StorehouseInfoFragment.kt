package com.example.beerdistrkt.fragPages.homePage.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.beerdistrkt.BaseFragment
import com.example.beerdistrkt.R
import com.example.beerdistrkt.collectLatest
import com.example.beerdistrkt.databinding.FragmentStorehouseInfoBinding
import com.example.beerdistrkt.fragPages.sawyobi.adapters.SimpleBeerRowAdapter
import com.example.beerdistrkt.fragPages.sawyobi.models.SimpleBeerRowModel
import com.example.beerdistrkt.network.model.ResultState
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StorehouseInfoFragment : BaseFragment<HomeViewModel>() {

    var onToggle: () -> Unit = {}
    override val viewModel: HomeViewModel by viewModels({ requireParentFragment() })
    private val binding by viewBinding(FragmentStorehouseInfoBinding::bind)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_storehouse_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.barrelsListLiveData.observe(viewLifecycleOwner) {
            binding.homeMainStoreHouseLoader.isVisible = it is ResultState.Loading
            when (it) {
                is ResultState.Error -> showToast(R.string.some_error)
                is ResultState.Success -> initStoreHouseRecycler(it.data)
                else -> {}
            }
        }

        binding.homeStoreHouseTitle.setOnClickListener {
            onToggle.invoke()
        }

        viewModel.bottomSheetStateFlow.collectLatest(viewLifecycleOwner) { state ->
            if (state == BottomSheetBehavior.STATE_EXPANDED)
                binding.homeHideStoreHouse.rotation = 0f
            else
                binding.homeHideStoreHouse.rotation = 180f
        }
    }

    private fun initStoreHouseRecycler(data: List<SimpleBeerRowModel>) {
        binding.homeStoreHouseRecycler.layoutManager = LinearLayoutManager(context)
        val adapter = SimpleBeerRowAdapter(
            data.filter { beerRowModel ->
                beerRowModel.values.values.any { barrelCount ->
                    barrelCount > 0
                } || beerRowModel.title == HomeFragment.emptyBarrelTitle
            },
            isHomePage = true
        )
        adapter.onClick = View.OnClickListener {
            if (viewModel.session.region?.hasOwnStorage == true)
                findNavController().navigate(R.id.action_homeFragment_to_sawyobiFragment)
        }
        binding.homeStoreHouseRecycler.adapter = adapter
    }
}