package com.example.beerdistrkt.fragPages.bottlemanagement

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.beerdistrkt.BaseFragment
import com.example.beerdistrkt.R
import com.example.beerdistrkt.adapters.SimpleListAdapter
import com.example.beerdistrkt.databinding.BottleRowBinding
import com.example.beerdistrkt.databinding.FragmentBottleListBinding
import com.example.beerdistrkt.models.bottle.BaseBottleModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BottleListFragment : BaseFragment<BottleListViewModel>() {

    override val viewModel: BottleListViewModel by viewModels()

    private val binding by viewBinding(FragmentBottleListBinding::bind)

    override var frLayout: Int? = R.layout.fragment_bottle_list

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.addBottle.setOnClickListener {
            openDetails(0)
        }
        lifecycleScope.launch {
            initRecycler(viewModel.getBottleList())
        }
    }

    private fun openDetails(bottleID: Int) {
        Log.d(TAG, "openDetails: $bottleID")
        findNavController().navigate(
            BottleListFragmentDirections.actionBottleListFragmentToBottleDetailFragment(
                bottleID
            )
        )
    }

    private fun initRecycler(bottles: List<BaseBottleModel>) = with(binding.bottlesRv) {
        layoutManager = LinearLayoutManager(requireContext())
        adapter = SimpleListAdapter(
            data = bottles,
            layoutId = R.layout.bottle_row,
            onBind = { item, view ->
                BottleRowBinding.bind(view).apply {
                    bottleTitleTv.text = item.name
                    bottleSizeTv.text = getString(R.string.lt_f, item.volume)
//                        bottleImage.setImageDrawable()
                    dotsImg.setOnClickListener {
                        openDetails(item.id)
                    }
                    root.alpha = if (item.isActive) 1f else 0.4f
                }
            }
        )
    }
}