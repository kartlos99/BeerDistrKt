package com.example.beerdistrkt.fragPages.beer.presentation

import android.app.AlertDialog
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.beerdistrkt.BaseFragment
import com.example.beerdistrkt.R
import com.example.beerdistrkt.collectLatest
import com.example.beerdistrkt.databinding.AddBeerFragmentBinding
import com.example.beerdistrkt.fragPages.beer.domain.model.Beer
import com.example.beerdistrkt.fragPages.beer.presentation.ChooseColorDialog.Companion.COLOR_SELECTOR_REQUEST_KEY
import com.example.beerdistrkt.fragPages.beer.presentation.ChooseColorDialog.Companion.SELECTED_COLOR_KEY
import com.example.beerdistrkt.fragPages.beer.presentation.adapter.BeerListAdapter
import com.example.beerdistrkt.fragPages.beer.presentation.adapter.TouchCallback
import com.example.beerdistrkt.mapToString
import com.example.beerdistrkt.setDifferText
import com.example.beerdistrkt.showAskingDialog
import com.example.beerdistrkt.showInfoDialog
import com.example.beerdistrkt.simpleTextChangeListener
import com.example.beerdistrkt.utils.ApiResponseState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddBeerFragment : BaseFragment<AddBeerViewModel>() {

    private val binding by viewBinding(AddBeerFragmentBinding::bind)

    override val viewModel: AddBeerViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.add_beer_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initViewModel()
        setResultListeners()
    }

    private fun initView() = with(binding) {
        setupRecycler()
        btnCancel.setOnClickListener {
            viewModel.clearBeerData()
        }
        btnBeerDone.setOnClickListener {
            if (eBeerName.editText?.text.isNullOrEmpty() || eBeerPr.editText?.text.isNullOrEmpty()) {
                showInfoAlertDialog()
            } else {
                viewModel.saveChanges()
            }
        }
        btnColor.setOnClickListener {
            ChooseColorDialog.getInstance(viewModel.currentBeerColor)
                .show(childFragmentManager, ChooseColorDialog.TAG)
        }
        addBtn.setOnClickListener {
            viewModel.initNewBeer()
        }
        eBeerName.editText?.simpleTextChangeListener {
            viewModel.setBeerName(it.toString().trim())
        }
        eBeerPr.editText?.simpleTextChangeListener {
            viewModel.setBeerPrice(it.toString().trim())
        }
    }

    private fun setupRecycler() = with(binding.beerRv) {
        val touchCallback = TouchCallback(viewModel::onItemMove)
        val touchHelper = ItemTouchHelper(touchCallback)

        val beerListAdapter = BeerListAdapter(
            ::onEditClick,
            ::onDeleteClick
        )
        adapter = beerListAdapter
        layoutManager = LinearLayoutManager(context)
        touchHelper.attachToRecyclerView(this)

        viewModel.beersFlow.collectLatest(viewLifecycleOwner) {
            beerListAdapter.submitList(it)
        }
    }

    private fun showInfoAlertDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext()).apply {
            setMessage(R.string.fields_request2)
            setPositiveButton("OK") { dialogInterface, i -> }
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setTitle(getString(R.string.feel_empty_fields))
        alertDialog.show()
    }

    private fun setResultListeners() {
        childFragmentManager.setFragmentResultListener(
            COLOR_SELECTOR_REQUEST_KEY, viewLifecycleOwner
        ) { _, bundle ->
            viewModel.setBeerColor(bundle.getInt(SELECTED_COLOR_KEY))
        }
    }

    fun initViewModel() {
        viewModel.currentBeerStateFlow.collectLatest(viewLifecycleOwner) { beer: Beer? ->
            beer?.let(::fillBeerData)
            binding.modifyBeerGroup.isVisible = beer != null
            binding.addBtn.isVisible = beer == null
        }
        viewModel.addBeerLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is ApiResponseState.Success -> {
                    binding.btnBeerDone.isEnabled = true
                    showToast(it.data + " Added")
                    findNavController().navigateUp()
                }

                is ApiResponseState.ApiError -> {
                    context?.showInfoDialog(
                        R.string.server_error,
                        "${it.errorCode} : ${it.errorText}",
                        R.string.ok,
                        R.style.ThemeOverlay_MaterialComponents_Dialog
                    )
                    binding.btnBeerDone.isEnabled = true
                }

                else -> {}
            }
        }
        viewModel.deleteBeerLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is ApiResponseState.Success -> {
                    binding.btnBeerDone.isEnabled = true
                    showToast("ლუდის სახეობა წაიშალა!")
                    findNavController().navigateUp()
                }

                is ApiResponseState.ApiError -> {
                    context?.showInfoDialog(
                        R.string.server_error,
                        "${it.errorCode} : ${it.errorText}",
                        R.string.ok,
                        R.style.ThemeOverlay_MaterialComponents_Dialog
                    )
                    binding.btnBeerDone.isEnabled = true
                }

                else -> {}
            }
        }
    }

    private fun fillBeerData(beer: Beer) = with(binding) {
        eBeerName.editText?.setDifferText(beer.name)
        eBeerPr.editText?.setDifferText(beer.price.mapToString())
        modifyTitle.text = if (beer.id > 0) "რედაქტირება" else "ახალი"
        btnColor.backgroundTintList = ColorStateList.valueOf(beer.displayColor)
    }

    private fun onEditClick(beer: Beer) {
        viewModel.editBeer(beer)
    }

    private fun onDeleteClick(beerID: Int) {
        requireContext().showAskingDialog(
            R.string.delete,
            R.string.confirm_delete_text,
            R.string.yes,
            R.string.no,
            R.style.ThemeOverlay_MaterialComponents_Dialog
        ) {
            viewModel.removeBeer(beerID)
        }
    }

}
