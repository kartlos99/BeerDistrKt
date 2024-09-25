package com.example.beerdistrkt.fragPages.bottlemanagement

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.beerdistrkt.BaseFragment
import com.example.beerdistrkt.R
import com.example.beerdistrkt.databinding.FragmentBottleDetailBinding
import com.example.beerdistrkt.fragPages.bottlemanagement.Event.DataSaved
import com.example.beerdistrkt.fragPages.bottlemanagement.Event.Error
import com.example.beerdistrkt.fragPages.bottlemanagement.Event.IncorrectDataEntered
import com.example.beerdistrkt.fragPages.bottlemanagement.Event.ShowLoading
import com.example.beerdistrkt.getViewModel
import com.example.beerdistrkt.models.bottle.BaseBottleModel
import com.example.beerdistrkt.models.bottle.BottleStatus
import kotlinx.coroutines.flow.collectLatest

class BottleDetailFragment : BaseFragment<BottleDetailViewModel>() {

    private val bottleID by lazy {
        val args = BottleDetailFragmentArgs.fromBundle(arguments ?: Bundle())
        args.bottleID
    }

    override val viewModel: BottleDetailViewModel by lazy {
        getViewModel { BottleDetailViewModel(bottleID) }
    }

    private val binding by viewBinding(FragmentBottleDetailBinding::bind)

    override var frLayout: Int? = R.layout.fragment_bottle_detail


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeViewModel()
        initView()
    }

    private fun initView() = with(binding) {
        setupBeerDropDown()
        setupStatusDropDown()
        saveBtn.setOnClickListener {
            binding.infoMessage.text = ""
            viewModel.readForm(
                bottleNameInput.text.toString(),
                bottleVolumeInput.text.toString(),
                beerInput.text.toString(),
                bottlePriceInput.text.toString(),
                BottleStatus.from(
                    requireContext(), bottleStatusInput.text.toString()
                )
            )
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launchWhenStarted {
            viewModel.eventsFlow.collectLatest {
                when (it) {
                    DataSaved -> {
                        findNavController().navigateUp()
                    }

                    is Error -> {
                        showToast("${it.code}: ${it.error}")
                    }

                    is IncorrectDataEntered -> {
                        binding.infoMessage.text = getString(it.msgID)
                    }

                    is ShowLoading -> binding.progressIndicator.isVisible = it.isLoading
                    else -> {}
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.stateFlow.collectLatest {
                when (it) {
                    is Event.EditBottle -> fillForm(it.bottle)
                    else -> {}
                }
            }
        }
    }

    private fun fillForm(bottle: BaseBottleModel) = with(binding) {
        setPageTitle(R.string.m_edit)
        bottleNameInput.setText(bottle.name)
        bottleVolumeInput.setText(bottle.volume.toString())
        beerInput.setText(bottle.beer.dasaxeleba, false)
        bottlePriceInput.setText(bottle.price.toString())
        bottleStatusInput.setText(getString(bottle.status.displayName), false)
    }

    private fun setupStatusDropDown() {
        val data = BottleStatus.entries
            .filter {
                it == BottleStatus.ACTIVE || it == BottleStatus.INACTIVE
            }
            .map {
                getString(it.displayName)
            }
        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.support_simple_spinner_dropdown_item,
            data
        )
        binding.bottleStatusInput.setAdapter(adapter)
    }

    private fun setupBeerDropDown() {
        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.support_simple_spinner_dropdown_item,
            viewModel.getBeerNames()
        )
        binding.beerInput.setAdapter(adapter)
    }

}