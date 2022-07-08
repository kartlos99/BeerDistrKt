package com.example.beerdistrkt.fragPages.addBeer

import android.app.AlertDialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.beerdistrkt.*
import com.example.beerdistrkt.databinding.AddBeerFragmentBinding
import com.example.beerdistrkt.models.BeerModelBase
import com.example.beerdistrkt.utils.ApiResponseState


class AddBeerFragment : BaseFragment<AddBeerViewModel>() {

    private var beerListAdapter: BeerListAdapter? = null
    private var beerID = 0
    private var beerColor: Int = Color.rgb(128, 128, 128)

    private val binding by viewBinding(AddBeerFragmentBinding::bind)

    override val viewModel by lazy {
        getViewModel { AddBeerViewModel() }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.add_beer_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        beerListAdapter = BeerListAdapter(
            viewModel.beerList,
            ::onEditClick,
            ::onDeleteClick
        )
        binding.beerRv.adapter = beerListAdapter
        binding.beerRv.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.btnBeerUaryofa.setOnClickListener {
            beerID = 0
            binding.eBeerName.editText?.setText("")
            binding.eBeerPr.editText?.setText("")
            binding.btnBeerDone.text = "+"
            binding.tAddeditBeer.text = "ახალი ლუდის დამატება"
            binding.btnBeerUaryofa.isInvisible = true
        }
        binding.btnBeerDone.setOnClickListener {
            if (binding.eBeerName.editText?.text.isNullOrEmpty() || binding.eBeerPr.editText?.text.isNullOrEmpty()) {
                val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
                builder.setMessage(R.string.fields_request2)
                builder.setPositiveButton("OK", { dialogInterface, i -> })
                val alertDialog: AlertDialog = builder.create()
                alertDialog.setTitle(getString(R.string.feel_empty_fields))
                alertDialog.show()
            } else {
                viewModel.sendDataToDB(
                    BeerModelBase(
                        beerID,
                        binding.eBeerName.editText?.text.toString(),
                        String.format(
                            "#%02X%02X%02X",
                            Color.red(beerColor),
                            Color.green(beerColor),
                            Color.blue(beerColor)
                        ),
                        binding.eBeerPr.editText?.text.toString().toDouble(),
                        "_"
                    )

                )
            }
        }
        binding.btnColor.setOnClickListener {
            val colorDialog = ChooseColorDialog { color ->
                myColor(color)
            }
            colorDialog.show(childFragmentManager, beerColor.toString())
        }
        myColor(beerColor)
        initViewModel()
    }

    fun initViewModel() {
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

    private fun onEditClick(beer: BeerModelBase) = with(binding) {
        eBeerName.editText?.setText(beer.dasaxeleba)
        eBeerPr.editText?.setText((beer.fasi ?: 0).toString())
        beerID = beer.id
        btnBeerDone.text = "ჩაწერა"
        tAddeditBeer.text = "რედაქტირება"
        btnBeerUaryofa.isVisible = true
        beerColor = Color.parseColor(beer.displayColor)
        myColor(beerColor)
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

    private fun myColor(color: Int) {
        beerColor = color
        binding.btnColor.backgroundTintList = ColorStateList.valueOf(color)
    }
}
