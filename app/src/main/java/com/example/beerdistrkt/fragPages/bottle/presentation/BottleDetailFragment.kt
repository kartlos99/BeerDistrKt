package com.example.beerdistrkt.fragPages.bottle.presentation

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.beerdistrkt.BaseFragment
import com.example.beerdistrkt.R
import com.example.beerdistrkt.collectLatest
import com.example.beerdistrkt.databinding.FragmentBottleDetailBinding
import com.example.beerdistrkt.empty
import com.example.beerdistrkt.fragPages.bottle.domain.model.Bottle
import com.example.beerdistrkt.fragPages.bottle.domain.model.BottleStatus
import com.example.beerdistrkt.fragPages.bottle.presentation.Event.DataSaved
import com.example.beerdistrkt.fragPages.bottle.presentation.Event.Error
import com.example.beerdistrkt.fragPages.bottle.presentation.Event.IncorrectDataEntered
import com.example.beerdistrkt.fragPages.bottle.presentation.Event.ShowLoading
import com.example.beerdistrkt.network.model.isLoading
import com.example.beerdistrkt.network.model.onError
import com.example.beerdistrkt.network.model.onSuccess
import com.example.beerdistrkt.paramViewModels
import com.example.beerdistrkt.setDifferText
import com.example.beerdistrkt.showAskingDialog
import com.example.beerdistrkt.showInfoDialog
import com.example.beerdistrkt.simpleTextChangeListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BottleDetailFragment : BaseFragment<BottleDetailViewModel>() {

    private val bottleID by lazy {
        val args = BottleDetailFragmentArgs.fromBundle(arguments ?: Bundle())
        args.bottleID
    }

    override val viewModel: BottleDetailViewModel by paramViewModels<BottleDetailViewModel, BottleDetailViewModel.Factory> {
        it.create(bottleID)
    }

    private val binding by viewBinding(FragmentBottleDetailBinding::bind)

    override var frLayout: Int? = R.layout.fragment_bottle_detail

    override val titleRes: Int
        get() = if (bottleID > 0) R.string.m_edit else R.string.add_bottle

    private fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.delete -> {
                askForRemoving()
                return true
            }
        }
        return false
    }

    private fun askForRemoving() {
        context?.showAskingDialog(
            R.string.delete_bottle,
            R.string.recovery_is_impossible_from_app,
            R.string.yes,
            R.string.no,
            R.style.ThemeOverlay_MaterialComponents_Dialog
        ) {
            viewModel.deleteBottle()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeViewModel()
        initView()
    }

    private fun initView() {
        if (bottleID > 0)
            setupMenu(R.menu.bottle_detail_manu, ::onMenuItemSelected)
        setupBeerDropDown()
        setupStatusDropDown()
        setListeners()
    }

    private fun setListeners() = with(binding) {
        saveBtn.setOnClickListener {
            binding.infoMessage.text = ""
            viewModel.onSaveClicked()
        }
        bottleNameInput.simpleTextChangeListener { text ->
            viewModel.setName(text.toString())
        }
        bottleVolumeInput.simpleTextChangeListener { text ->
            viewModel.setVolume(text.toString())
        }
        beerInput.onItemClickListener = OnItemClickListener { parent, view, position, id ->
            viewModel.setBeer(position)
        }
        bottlePriceInput.simpleTextChangeListener { text ->
            viewModel.setPrice(text.toString())
        }
        bottleStatusInput.simpleTextChangeListener {
            BottleStatus.from(requireContext(), bottleStatusInput.text.toString())?.let { status ->
                viewModel.setStatus(status)
            }
                ?: showToast(R.string.cant_set_status)
        }
    }

    private fun observeViewModel() = with(viewModel) {
        viewModel.eventsFlow.collectLatest(viewLifecycleOwner, action = ::handleEvents)
        viewModel.currentBottleStateFlow.collectLatest(viewLifecycleOwner, action = ::fillForm)
        apiStateFlow.collectLatest(viewLifecycleOwner) { result ->
            binding.progressIndicator.isVisible = result.isLoading()
            binding.infoMessage.text = String.empty()
            result.onError { error ->
                binding.infoMessage.text = error.message
            }
            result.onSuccess {
//                TODO change this logic
                if (it.isNotEmpty()) showDataSavedInfo()
            }
        }
    }

    private fun showDataSavedInfo() = requireContext().showInfoDialog(
        null,
        R.string.data_saved,
        R.string.ok,
        R.style.ThemeOverlay_MaterialComponents_Dialog,
        false
    ) {
        findNavController().navigateUp()
    }

    private fun fillForm(bottle: Bottle) = with(binding) {
        bottleNameInput.setDifferText(bottle.name)
        bottleVolumeInput.setDifferText(bottle.volume.toString())
        beerInput.setText(bottle.beer.name, false)
        bottlePriceInput.setDifferText(bottle.price.toString())
        bottleStatusInput.setText(getString(bottle.status.displayName), false)
    }

    private fun handleEvents(event: Event) {
        when (event) {
            DataSaved -> {
                findNavController().navigateUp()
            }

            is Error -> {
                showToast("${event.code}: ${event.error}")
            }

            is IncorrectDataEntered -> {
                binding.infoMessage.text = getString(event.msgID)
            }

            is ShowLoading -> binding.progressIndicator.isVisible = event.isLoading
            else -> {}
//        is Event.EditBottle -> TODO()
        }
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