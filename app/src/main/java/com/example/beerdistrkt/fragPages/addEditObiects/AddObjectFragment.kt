package com.example.beerdistrkt.fragPages.addEditObiects

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.beerdistrkt.*
import com.example.beerdistrkt.databinding.AddObjectFragmentBinding
import com.example.beerdistrkt.fragPages.login.models.AttachedRegion
import com.example.beerdistrkt.fragPages.login.models.Permission
import com.example.beerdistrkt.models.*
import com.example.beerdistrkt.utils.ApiResponseState
import com.example.beerdistrkt.utils.Session
import com.example.beerdistrkt.utils.show

class AddObjectFragment : BaseFragment<AddObjectViewModel>() {

    override val viewModel by lazy {
        getViewModel { AddObjectViewModel(clientID) }
    }

    private val clientID by lazy {
        val args = AddObjectFragmentArgs.fromBundle(arguments ?: Bundle())
        args.clientID
    }

    val binding by viewBinding(AddObjectFragmentBinding::bind)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.add_object_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewModel()
        (activity as AppCompatActivity).supportActionBar?.title =
            if (clientID > 0)
                resources.getString(R.string.edit_client)
            else
                resources.getString(R.string.create_client)

        binding.initView()

        if (Session.get().hasPermission(Permission.ManageRegion) && clientID > 0)
            viewModel.getRegionForClient()
    }

    private fun AddObjectFragmentBinding.initView() {
        addEditClientDoneBtn.setOnClickListener {
            val client = Obieqti(addEditClientName.editText?.text.toString().trim()).apply {
                id = viewModel.clientID
                adress = addEditClientAdress.editText?.text.toString()
                tel = addEditClientPhone.editText?.text.toString()
                comment = addEditComment.editText?.text.toString()
                sk = addEditClientSK.editText?.text.toString()
                sakpiri = addEditClientPerson.editText?.text.toString()
                chek = if (addEditClientChek.isChecked) "1" else "0"
            }

            val priceList = mutableListOf<ObjToBeerPrice>()

            for (i in 0 until addEditClientPricesContainer.childCount) {
                val priceRow = addEditClientPricesContainer.getChildAt(i) as LinearLayout
                val eText = priceRow.getChildAt(1) as EditText
                val price = if (eText.text.isEmpty())
                    eText.hint
                else
                    eText.text

                val beerID = eText.tag as Int

                priceList.add(
                    ObjToBeerPrice(
                        viewModel.clientID,
                        beerID,
                        price.toString().toFloat()
                    )
                )
            }

            viewModel.addClient(ObiectWithPrices(client, priceList))
        }

        addEditClientRegionBtn.setOnClickListener {
            showRegionChooser()
        }
    }

    private fun initViewModel() {
        viewModel.beersLiveData.observe(viewLifecycleOwner, Observer {
            if (it.isNotEmpty() && clientID == 0) drawBeerPrices(it)
        })
        viewModel.clientObjectLiveData.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                fillForm(it)
                viewModel.clientObjectLiveData.value = null
                viewModel.beersLiveData.value?.let { beerList ->
                    drawBeerPrices(beerList)
                }
            }
        })
        viewModel.clientSaveMutableLiveData.observe(viewLifecycleOwner, Observer {
            when (it) {
                is ApiResponseState.Loading -> {
                }
                is ApiResponseState.ApiError -> {
                    if (it.errorCode == DataResponse.mySqlDuplicateError)
                        context?.showInfoDialog(
                            null,
                            R.string.duplicate_client,
                            R.string.ok,
                            R.style.ThemeOverlay_MaterialComponents_Dialog
                        )
                    else
                        context?.showInfoDialog(
                            R.string.server_error,
                            it.errorText,
                            R.string.ok,
                            R.style.ThemeOverlay_MaterialComponents_Dialog
                        )
                }
                is ApiResponseState.Success -> {
                    showToast(R.string.data_saved)
                    findNavController().navigateUp()
                }
            }
        })
        viewModel.clientRegionsLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is ApiResponseState.Success -> showRegions(it.data)
            }
        }
    }

    private fun showRegions(data: List<AttachedRegion>) {
        val regionsString = data
            .joinToString(", ", getString(R.string.regions) + " ") { it.name }
        with(binding) {
            addEditClientRegionsTv.text = regionsString
            addEditClientRegionsTv.show()
            addEditClientRegionBtn.show()
        }
    }

    private fun showRegionChooser() {
        val builder = AlertDialog.Builder(requireContext())
        builder
            .setTitle(getString(R.string.associated_regions))
            .setCancelable(true)
            .setMultiChoiceItems(
                viewModel.getAllRegionNames(),
                viewModel.getSelectedRegions()
            ) { dialogInterface: DialogInterface?, i: Int, b: Boolean ->
                if (b)
                    viewModel.selectedRegions.add(viewModel.regions[i])
                else
                    viewModel.selectedRegions.remove(viewModel.regions[i])
            }
            .setPositiveButton(R.string.ok) { _, _ ->
                viewModel.setNewRegions()
            }
            .setNegativeButton(R.string.cancel) { dialogInterface, i -> }

        val alertDialog = builder.create()
        alertDialog.show()

    }

    private fun fillForm(clientData: ObiectWithPrices) {
        with(binding) {
            addEditClientName.editText?.setText(clientData.obieqti.dasaxeleba)
            addEditClientSK.editText?.setText(clientData.obieqti.sk ?: "")
            addEditClientPerson.editText?.setText(clientData.obieqti.sakpiri ?: "")
            addEditClientAdress.editText?.setText(clientData.obieqti.adress ?: "")
            addEditClientPhone.editText?.setText(clientData.obieqti.tel ?: "")
            addEditComment.editText?.setText(clientData.obieqti.comment ?: "")
            addEditClientChek.isChecked = clientData.obieqti.chek == "1"
        }
    }

    private fun drawBeerPrices(beerList: List<BeerModelBase>) {
        for (i in beerList.indices) {
            val priceItemView = LinearLayout(context).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.END
            }
            val eText = EditText(context).apply {
                id = i + 100
                gravity = Gravity.CENTER
                width = 350
                hint = beerList[i].fasi.toString()
                inputType = InputType.TYPE_NUMBER_FLAG_DECIMAL or InputType.TYPE_CLASS_NUMBER
                tag = beerList[i].id
            }
            val textView = TextView(context).apply {
                id = i + 200
                text = beerList[i].dasaxeleba
            }
            if (viewModel.clientID > 0) {
                val priceObj = viewModel.clientObject?.prices?.elementAtOrNull(i)
                eText.setText((priceObj?.fasi ?: .0f).toString())
            }
            priceItemView.addView(textView, 0)
            priceItemView.addView(eText, 1)
            binding.addEditClientPricesContainer.addView(priceItemView, i)
        }
    }

    companion object {
        const val INSERT = "insert"
        const val EDIT = "edit"
    }
}
