package com.example.beerdistrkt.fragPages.customer.presentation

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.beerdistrkt.BaseFragment
import com.example.beerdistrkt.R
import com.example.beerdistrkt.databinding.AddCustomerFragmentBinding
import com.example.beerdistrkt.fragPages.customer.domain.model.CustomerGroup
import com.example.beerdistrkt.fragPages.customer.domain.model.PriceEditModel
import com.example.beerdistrkt.fragPages.login.models.AttachedRegion
import com.example.beerdistrkt.fragPages.login.models.Permission
import com.example.beerdistrkt.fragPages.customer.domain.model.Customer
import com.example.beerdistrkt.models.CustomerWithPrices
import com.example.beerdistrkt.models.DataResponse
import com.example.beerdistrkt.models.ObjToBeerPrice
import com.example.beerdistrkt.fragPages.customer.domain.model.ClientBottlePrice
import com.example.beerdistrkt.paramViewModels
import com.example.beerdistrkt.round
import com.example.beerdistrkt.showInfoDialog
import com.example.beerdistrkt.utils.ApiResponseState
import com.example.beerdistrkt.utils.Session
import com.example.beerdistrkt.utils.show
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddCustomerFragment : BaseFragment<AddCustomerViewModel>() {

    override val viewModel by paramViewModels<AddCustomerViewModel, AddCustomerViewModel.Factory> {
        it.create(clientID)
    }

    private val clientID by lazy {
        val args = AddCustomerFragmentArgs.fromBundle(arguments ?: Bundle())
        args.clientID
    }

    val binding by viewBinding(AddCustomerFragmentBinding::bind)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.add_customer_fragment, container, false)
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

    private fun AddCustomerFragmentBinding.initView() {
        setupCustomerGroupDropDown()
        addEditClientDoneBtn.setOnClickListener {
            val customer = Customer(
                id = viewModel.clientID,
                name = addEditClientName.editText?.text.toString().trim(),
                address = addEditClientAdress.editText?.text.toString().trim(),
                tel = addEditClientPhone.editText?.text.toString().trim(),
                comment = addEditComment.editText?.text.toString().trim(),
                identifyCode = addEditClientSK.editText?.text.toString().trim(),
                contactPerson = addEditClientPerson.editText?.text.toString().trim(),
                chek = if (addEditClientCheck.isChecked) "1" else "0",
                group = CustomerGroup.from(requireContext(), clientGroupInput.text.toString()),
                beerPrices = emptyList(),
                bottlePrices = emptyList(),
            )

            viewModel.addClient(
                CustomerWithPrices(
                    customer,
                    getEditedBeerPrices(),
                    getEditedBottlePrices()
                )
            )
        }

        addEditClientRegionBtn.setOnClickListener {
            showRegionChooser()
        }
    }

    private fun setupCustomerGroupDropDown() {
        val data = CustomerGroup.entries
            .map {
                getString(it.displayName)
            }
        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.support_simple_spinner_dropdown_item,
            data
        )
        binding.clientGroupInput.setAdapter(adapter)
    }

    private fun getEditedBeerPrices(): List<ObjToBeerPrice> {
        val priceList = mutableListOf<ObjToBeerPrice>()

        for (i in 0 until binding.addEditClientPricesContainer.childCount) {
            val priceRow = binding.addEditClientPricesContainer.getChildAt(i) as LinearLayout
            val eText = priceRow.getChildAt(1) as EditText
            val price = eText.text.ifEmpty { eText.hint }

            val beerID = eText.tag as Int

            priceList.add(
                ObjToBeerPrice(
                    viewModel.clientID,
                    beerID,
                    price.toString().toFloat()
                )
            )
        }
        return priceList
    }

    private fun getEditedBottlePrices(): List<ClientBottlePrice> {
        val priceList = mutableListOf<ClientBottlePrice>()

        for (i in 0 until binding.addEditClientBottlePricesContainer.childCount) {
            val priceRow = binding.addEditClientBottlePricesContainer.getChildAt(i) as LinearLayout
            val eText = priceRow.getChildAt(1) as EditText
            val price = eText.text.ifEmpty { eText.hint }

            val bottleID = eText.tag as Int

            priceList.add(
                ClientBottlePrice(
                    viewModel.clientID,
                    bottleID,
                    price.toString().toDouble()
                )
            )
        }
        return priceList
    }

    private fun initViewModel() {
        viewModel.clientObjectLiveData.observe(viewLifecycleOwner) {
            if (it != null) {
                it.first?.let(::fillForm)
                drawPriceInputFields(it.second, it.third)
                viewModel.clientObjectLiveData.value = null
            }
        }
        viewModel.clientSaveMutableLiveData.observe(viewLifecycleOwner) {
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

                else -> {}
            }
        }
        viewModel.clientRegionsLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is ApiResponseState.Success -> showRegions(it.data)
                else -> {}
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

    private fun fillForm(clientData: CustomerWithPrices) = with(binding) {
        addEditClientName.editText?.setText(clientData.customer.name)
        addEditClientSK.editText?.setText(clientData.customer.identifyCode ?: "")
        addEditClientPerson.editText?.setText(clientData.customer.contactPerson ?: "")
        addEditClientAdress.editText?.setText(clientData.customer.address ?: "")
        addEditClientPhone.editText?.setText(clientData.customer.tel ?: "")
        addEditComment.editText?.setText(clientData.customer.comment ?: "")
        addEditClientCheck.isChecked = clientData.customer.chek == "1"
        clientGroupInput.setText(getString(clientData.customer.group.displayName), false)
    }

    private fun drawPriceInputFields(
        beerPrices: List<PriceEditModel>,
        bottlePrices: List<PriceEditModel>,
    ) {
        beerPrices.forEachIndexed { index, priceEditModel ->
            binding.addEditClientPricesContainer
                .addView(getFilledPriceView(index, priceEditModel), index)
        }
        bottlePrices.forEachIndexed { index, priceEditModel ->
            binding.addEditClientBottlePricesContainer
                .addView(getFilledPriceView(index, priceEditModel), index)
        }
    }

    private fun getFilledPriceView(index: Int, item: PriceEditModel): LinearLayout {
        val priceItemView = getPriceItemView()
        val eText = getEditTextForPrice(index, item)
        if (viewModel.clientID > 0) {
            eText.setText(item.price.round().toString())
        }
        priceItemView.addView(getTitleViewForPrice(index, item), 0)
        priceItemView.addView(eText, 1)
        return priceItemView
    }

    private fun getPriceItemView(): LinearLayout = LinearLayout(context).apply {
        orientation = LinearLayout.HORIZONTAL
        gravity = Gravity.END
    }

    private fun getEditTextForPrice(index: Int, item: PriceEditModel): EditText =
        EditText(context).apply {
            id = index + 100
            gravity = Gravity.CENTER
            width = 350
            hint = item.defaultPrice.round().toString()
            inputType = InputType.TYPE_NUMBER_FLAG_DECIMAL or InputType.TYPE_CLASS_NUMBER
            tag = item.id
        }

    private fun getTitleViewForPrice(index: Int, item: PriceEditModel) = TextView(context).apply {
        id = index + 200
        text = item.displayName
    }
}
