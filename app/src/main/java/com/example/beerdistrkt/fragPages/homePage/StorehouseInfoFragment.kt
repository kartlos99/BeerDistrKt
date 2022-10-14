package com.example.beerdistrkt.fragPages.homePage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.beerdistrkt.BaseFragment
import com.example.beerdistrkt.R
import com.example.beerdistrkt.databinding.FragmentStorehouseInfoBinding
import com.example.beerdistrkt.fragPages.sawyobi.adapters.SimpleBeerRowAdapter
import com.example.beerdistrkt.fragPages.sawyobi.models.SimpleBeerRowModel
import com.example.beerdistrkt.getActCtxViewModel
import com.example.beerdistrkt.utils.ApiResponseState
import com.example.beerdistrkt.utils.Session
import com.example.beerdistrkt.utils.visibleIf
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.flow.collectLatest

class StorehouseInfoFragment : BaseFragment<HomeViewModel>() {

    var onToggle: () -> Unit = {}
    override val viewModel: HomeViewModel by lazy { getActCtxViewModel() }
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
            when (it) {
                is ApiResponseState.Loading -> {
                    binding.homeMainStoreHouseLoader.visibleIf(it.showLoading)
                }
                is ApiResponseState.Success -> initStoreHouseRecycler(it.data)
                else -> showToast(R.string.some_error)
            }
        }

        binding.homeStoreHouseTitle.setOnClickListener {
            onToggle.invoke()
        }

        lifecycleScope.launchWhenStarted {
            viewModel.bottomSheetStateFlow.collectLatest { state ->
                if (state == BottomSheetBehavior.STATE_EXPANDED)
                    binding.homeHideStoreHouse.rotation = 0f
                else
                    binding.homeHideStoreHouse.rotation = 180f
            }
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
            true
        )
        adapter.onClick = View.OnClickListener {
            if (Session.get().region?.hasOwnStorage() == true)
                findNavController().navigate(R.id.action_homeFragment_to_sawyobiFragment)
        }
        binding.homeStoreHouseRecycler.adapter = adapter
    }
}