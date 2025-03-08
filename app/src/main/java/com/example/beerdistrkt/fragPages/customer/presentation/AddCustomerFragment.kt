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
import androidx.core.view.isEmpty
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.beerdistrkt.BaseFragment
import com.example.beerdistrkt.R
import com.example.beerdistrkt.collectLatest
import com.example.beerdistrkt.common.domain.model.Goods
import com.example.beerdistrkt.databinding.AddCustomerFragmentBinding
import com.example.beerdistrkt.fragPages.customer.presentation.model.CustomerUiModel
import com.example.beerdistrkt.fragPages.customer.presentation.model.PriceEditModel
import com.example.beerdistrkt.fragPages.login.models.AttachedRegion
import com.example.beerdistrkt.network.model.isLoading
import com.example.beerdistrkt.network.model.onError
import com.example.beerdistrkt.network.model.onSuccess
import com.example.beerdistrkt.paramViewModels
import com.example.beerdistrkt.setDifferText
import com.example.beerdistrkt.showInfoDialog
import com.example.beerdistrkt.simpleTextChangeListener
import com.example.beerdistrkt.utils.ApiResponseState
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
        (activity as AppCompatActivity).supportActionBar?.title =
            if (clientID > 0)
                resources.getString(R.string.edit_client)
            else
                resources.getString(R.string.create_client)

        setListeners()
        observeData()
    }

    private fun setListeners() = with(binding) {
        addEditClientDoneBtn.setOnClickListener {
            viewModel.saveChanges()
        }
        addEditClientRegionBtn.setOnClickListener {
            showRegionChooser()
        }
        addEditClientName.editText?.simpleTextChangeListener(viewModel::onNameChange)
        addEditClientSK.editText?.simpleTextChangeListener(viewModel::onIdentityCodeChange)
        addEditClientPerson.editText?.simpleTextChangeListener(viewModel::onContactPersonChange)
        addEditClientAdress.editText?.simpleTextChangeListener(viewModel::onAddressChange)
        addEditClientPhone.editText?.simpleTextChangeListener(viewModel::onPhoneChange)
        addEditComment.editText?.simpleTextChangeListener(viewModel::onCommentChange)
        clientGroupInput.setOnItemClickListener { parent, view, position, id ->
            viewModel.onGroupChange(position)
        }
    }

    private fun setupCustomerGroupDropDown(items: List<Int>) {

        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.support_simple_spinner_dropdown_item,
            items.map { getString(it) }
        )
        binding.clientGroupInput.setAdapter(adapter)
    }

    private fun observeData() {
        viewModel.customerFlow.collectLatest(viewLifecycleOwner) { result ->
            binding.progressIndicator.isVisible = result.isLoading()
            result.onError { code, message ->
                showToast(message)
            }
            result.onSuccess {
                if (it != null) showDataSavedInfo()
            }
        }
        viewModel.clientRegionsLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is ApiResponseState.Success -> showRegions(it.data)
                else -> {}
            }
        }
        viewModel.customerStateFlow.collectLatest(viewLifecycleOwner, action = ::fillForm)
        viewModel.availableGroupsFlow.collectLatest(viewLifecycleOwner, action = ::setupCustomerGroupDropDown)
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

    private fun fillForm(customer: CustomerUiModel) = with(binding) {
        addEditClientName.editText?.setDifferText(customer.name)
        addEditClientSK.editText?.setDifferText(customer.identifyCode)
        addEditClientPerson.editText?.setDifferText(customer.contactPerson)
        addEditClientAdress.editText?.setDifferText(customer.address)
        addEditClientPhone.editText?.setDifferText(customer.tel)
        addEditComment.editText?.setDifferText(customer.comment)
        addEditClientCheck.isChecked = customer.hasCheck
        clientGroupInput.setText(getString(customer.group.displayName), false)
        drawPriceInputFields(customer)
    }

    private fun drawPriceInputFields(customer: CustomerUiModel) = with(binding) {
        if (addEditClientPricesContainer.isEmpty())
            customer.beerPrices.forEachIndexed { index, priceEditModel ->
                addEditClientPricesContainer
                    .addView(getFilledPriceView(index, priceEditModel, Goods.BEER), index)
            }
        if (addEditClientBottlePricesContainer.isEmpty())
            customer.bottlePrices.forEachIndexed { index, priceEditModel ->
                addEditClientBottlePricesContainer
                    .addView(getFilledPriceView(index, priceEditModel, Goods.BOTTLE), index)
            }
    }

    private fun getFilledPriceView(index: Int, item: PriceEditModel, itemType: Goods): LinearLayout {
        val priceItemView = getPriceItemView()
        val eText = getEditTextForPrice(index, item, itemType).apply {
            setDifferText(item.price)
        }
        priceItemView.addView(getTitleViewForPrice(index, item), 0)
        priceItemView.addView(eText, 1)
        return priceItemView
    }

    private fun getPriceItemView(): LinearLayout = LinearLayout(context).apply {
        orientation = LinearLayout.HORIZONTAL
        gravity = Gravity.END
    }

    private fun getEditTextForPrice(index: Int, item: PriceEditModel, itemType: Goods): EditText =
        EditText(context).apply {
            id = index + 100
            gravity = Gravity.CENTER
            width = 350
            hint = item.defaultPrice
            inputType = InputType.TYPE_NUMBER_FLAG_DECIMAL or InputType.TYPE_CLASS_NUMBER
            tag = item.id

            simpleTextChangeListener {
                viewModel.onPriceInput(index, it.toString(), itemType)
            }
        }

    private fun getTitleViewForPrice(index: Int, item: PriceEditModel) = TextView(context).apply {
        id = index + 200
        text = item.displayName
    }
}
