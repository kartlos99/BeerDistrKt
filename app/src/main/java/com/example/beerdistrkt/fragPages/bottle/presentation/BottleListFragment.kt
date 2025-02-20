package com.example.beerdistrkt.fragPages.bottle.presentation

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.children
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.beerdistrkt.BaseFragment
import com.example.beerdistrkt.R
import com.example.beerdistrkt.collectLatest
import com.example.beerdistrkt.common.adapter.SimpleDataAdapter
import com.example.beerdistrkt.databinding.BottleRowBinding
import com.example.beerdistrkt.databinding.FragmentBottleListBinding
import com.example.beerdistrkt.fragPages.bottle.domain.model.Bottle
import com.example.beerdistrkt.getDimenPixelOffset
import com.example.beerdistrkt.network.model.onError
import com.example.beerdistrkt.network.model.onLoading
import com.example.beerdistrkt.network.model.onSuccess
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BottleListFragment : BaseFragment<BottleListViewModel>() {

    override val viewModel: BottleListViewModel by viewModels()

    private val binding by viewBinding(FragmentBottleListBinding::bind)

    override var frLayout: Int? = R.layout.fragment_bottle_list

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setListeners()
        initRecycler()
    }

    private fun setListeners() {
        binding.addBottle.setOnClickListener {
            openDetails(0)
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

    private fun initRecycler() = with(binding.bottlesRv) {

        val bottlesAdapter = SimpleDataAdapter<Bottle>(
            layoutId = R.layout.bottle_row,
            onBind = { item, view ->
                BottleRowBinding.bind(view).apply {
                    bottleTitleTv.text = item.name
                    bottleSizeTv.text = getString(R.string.lt_f, item.volume)
//                        bottleImage.setImageDrawable()
                    dotsImg.setOnClickListener {
                        openDetails(item.id)
                    }

                    root.children.forEach {
                        it.alpha = if (item.isActive) 1f else 0.4f
                    }
                    root.elevation = if (item.isActive)
                        context.getDimenPixelOffset(R.dimen.gr_size_8).toFloat()
                    else
                        0f
                }
            }
        )

        layoutManager = LinearLayoutManager(requireContext())
        adapter = bottlesAdapter

        viewModel.bottlesFlow.collectLatest(viewLifecycleOwner) { result ->
            result.onLoading {
                bottlesAdapter.submitList(emptyList())
            }
            result.onError { error ->
                showToast(error.formatedMessage)
            }
            result.onSuccess {
                bottlesAdapter.submitList(it)
            }
        }
    }
}