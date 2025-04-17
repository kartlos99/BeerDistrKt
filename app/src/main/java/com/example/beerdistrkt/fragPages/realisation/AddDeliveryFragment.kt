package com.example.beerdistrkt.fragPages.realisation

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.beerdistrkt.BaseFragment
import com.example.beerdistrkt.R
import com.example.beerdistrkt.collectLatest
import com.example.beerdistrkt.common.fragments.ClientDebtFragment
import com.example.beerdistrkt.customView.TempBeerRowView
import com.example.beerdistrkt.customView.TempBottleRowView
import com.example.beerdistrkt.databinding.AddDeliveryFragmentBinding
import com.example.beerdistrkt.fragPages.realisation.RealisationType.BARREL
import com.example.beerdistrkt.fragPages.realisation.RealisationType.BOTTLE
import com.example.beerdistrkt.fragPages.realisation.RealisationType.NONE
import com.example.beerdistrkt.fragPages.realisation.models.BarrelRowModel
import com.example.beerdistrkt.fragPages.realisation.models.MoneyRowModel
import com.example.beerdistrkt.fragPages.realisation.models.SaleBottleRowModel
import com.example.beerdistrkt.fragPages.realisation.models.SaleRowModel
import com.example.beerdistrkt.fragPages.realisationtotal.models.PaymentType
import com.example.beerdistrkt.notifyNewComment
import com.example.beerdistrkt.paramViewModels
import com.example.beerdistrkt.setText
import com.example.beerdistrkt.setTint
import com.example.beerdistrkt.showInfoDialog
import com.example.beerdistrkt.simpleTextChangeListener
import com.example.beerdistrkt.text
import com.example.beerdistrkt.utils.ApiResponseState
import com.example.beerdistrkt.utils.K_OUT
import com.example.beerdistrkt.utils.MITANA
import com.example.beerdistrkt.utils.MITANA_BOTTLE
import com.example.beerdistrkt.utils.M_OUT
import com.example.beerdistrkt.utils.explodeAnim
import com.example.beerdistrkt.utils.goAway
import com.example.beerdistrkt.utils.show
import com.example.beerdistrkt.waitFor
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import java.util.Calendar

@AndroidEntryPoint
class AddDeliveryFragment : BaseFragment<AddDeliveryViewModel>(), View.OnClickListener {

    override val viewModel by paramViewModels<AddDeliveryViewModel, AddDeliveryViewModel.Factory> {
        it.create(clientID, orderID, recordID, operation)
    }

    private val clientID by lazy {
        val args = AddDeliveryFragmentArgs.fromBundle(requireArguments())
        args.clientObjectID
    }
    private val orderID by lazy {
        val args = AddDeliveryFragmentArgs.fromBundle(requireArguments())
        args.orderID
    }
    private val recordID by lazy {
        AddDeliveryFragmentArgs.fromBundle(requireArguments()).recordID
    }
    private val operation by lazy {
        AddDeliveryFragmentArgs.fromBundle(requireArguments()).operacia
    }

    private var dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
        viewModel.onSaleDateSelected(year, month, day)
        val timePickerDialog = TimePickerDialog(
            requireContext(),
            timeSetListener,
            viewModel.saleDateCalendar.get(Calendar.HOUR_OF_DAY),
            viewModel.saleDateCalendar.get(Calendar.MINUTE),
            true
        )
        timePickerDialog.setCancelable(false)
        timePickerDialog.show()
    }

    private val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
        viewModel.onSaleTimeSelected(hourOfDay, minute)
    }

    val binding by viewBinding(AddDeliveryFragmentBinding::bind)

    override var frLayout: Int? = R.layout.add_delivery_fragment

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners()
        binding.initView()

        initViewModel()
        checkForm()
        setPageTitle(resources.getString(R.string.delivery))
        showDebt()
    }

    private fun setupListeners() = with(binding) {
        val owner = this@AddDeliveryFragment
        addDeliveryDoneBtn.setOnClickListener(owner)
        addDeliveryDateBtn.setOnClickListener(owner)
        addDeliveryAddSaleItemBtn.setOnClickListener(owner)
        addDeliveryMoneyExpander.setOnClickListener(owner)
        optionIcon.setOnClickListener(owner)
        addDeliveryMoneyCashImg.setOnClickListener(owner)
        addDeliveryMoneyTransferImg.setOnClickListener(owner)
    }

    private fun AddDeliveryFragmentBinding.initView() {
        if (operation != null) {
            addDeliveryBarrelGr.isVisible = false
            realisationTypeSelector.isVisible = false
        }
        addDeliveryhideOnEditGroup.isVisible = operation == null
        beerSelector.beerGroupVisibleIf(operation != K_OUT)

        when (operation) {
            MITANA -> {
                addDeliveryMoneyGr.isVisible = false
                addDeliveryCheckReplace.isVisible = false
                viewModel.switchToBarrel()
            }

            MITANA_BOTTLE -> {
                addDeliveryMoneyGr.isVisible = false
                addDeliveryCheckReplace.isVisible = false
                viewModel.switchToBottle()
            }

            M_OUT -> {
                beerSelector.isVisible = false
                addDeliveryCheckGift.isVisible = false
                addDeliveryCheckReplace.isVisible = false
            }

            K_OUT -> {
                beerSelector.isVisible = true
                addDeliveryMoneyGr.isVisible = false
                addDeliveryCheckGift.isVisible = false
                addDeliveryCheckReplace.isVisible = false
            }
        }

        addDeliveryMoneyEt.simpleTextChangeListener {
            addDeliveryMoneyCashImg.setTint(getColorForValidationIndicator(it))
        }
        addDeliveryMoneyTransferEt.simpleTextChangeListener {
            addDeliveryMoneyTransferImg.setTint(getColorForValidationIndicator(it))
        }

        bottleSelector.withPrices = true
        bottleSelector.initView(
            viewModel.bottleList,
            ::checkForm
        )
        bottleSelector.onDeleteClick = {
            viewModel.removeBottleSaleItemFromList(it)
        }

        beerSelector.withPrices = true
        beerSelector.initView(
            viewModel.beerList,
            viewModel.barrels,
            ::checkForm
        )
        beerSelector.onDeleteClick = {
            viewModel.removeSaleItemFromList(it)
        }

        addDeliveryCheckGift.setOnCheckedChangeListener { _, isChecked ->
            viewModel.isGift = isChecked
            setupAutoComment(isChecked, R.string.gift)
        }
        addDeliveryCheckReplace.setOnCheckedChangeListener { _, isChecked ->
            viewModel.isReplace = isChecked
            setupAutoComment(isChecked, R.string.replace)
        }
        realisationTypeSelector.check(R.id.realizationByBarrel)
        realisationTypeSelector.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    R.id.realizationByBarrel -> viewModel.switchToBarrel()
                    R.id.realizationByBottle -> viewModel.switchToBottle()
                }
                checkForm()
            }
        }
    }

    private fun setupAutoComment(isChecked: Boolean, @StringRes textRes: Int) =
        with(binding.addDeliveryComment) {
            if (text().isEmpty() && isChecked) setText(textRes)
            if (text() == getString(textRes) && !isChecked) setText("")
            updateOptionsView()
        }

    private fun updateOptionsView(forceDisplay: Boolean = false) = with(binding) {
        optionIcon.isVisible = !addDeliveryCheckGift.isChecked && !addDeliveryCheckReplace.isChecked
        if (forceDisplay)
            toggleOptions()
    }

    private fun getColorForValidationIndicator(value: CharSequence): Int {
        return if (value.isNotEmpty() && (value.toString().toDoubleOrNull() ?: .0) > .0)
            R.color.green_08
        else
            R.color.gray_6
    }

    private fun initViewModel() {
        viewModel.clientLiveData.observe(viewLifecycleOwner) {
            binding.addDeliveryClientInfo.text = it.name
        }
        viewModel.beerListLiveData.observe(viewLifecycleOwner) {
            binding.beerSelector.updateBeers(it)
        }
        viewModel.bottleListLiveData.observe(viewLifecycleOwner) {
            binding.bottleSelector.updateBottles(it)
        }
        viewModel.tempRealisationLiveData.observe(viewLifecycleOwner) {
            binding.beerSelector.resetForm()
            binding.bottleSelector.resetForm()
            binding.addDeliveryTempContainer.removeAllViews()
            it.byBarrels.forEach { saleItem ->
                binding.addDeliveryTempContainer.addView(
                    TempBeerRowView(context = requireContext(), rowData = saleItem)
                )
            }
            it.byBottles.forEach { bottleSaleItem ->
                binding.addDeliveryTempContainer.addView(
                    TempBottleRowView(context = requireContext(), rowData = bottleSaleItem)
                )
            }
        }
        viewModel.saleDayLiveData.observe(viewLifecycleOwner) {
            binding.addDeliveryDateBtn.text = it
        }
        viewModel.addSaleLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is ApiResponseState.Success -> {
                    showToast(it.data)
                    if (!binding.addDeliveryComment.editText?.text.isNullOrEmpty())
                        notifyNewComment(binding.addDeliveryComment.editText?.text.toString())
                    findNavController().navigateUp()
                }

                else -> {}
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.eventsFlow.collectLatest {
                when (it) {
                    Event.CustomerNotFount -> showToast(R.string.object_not_found)
                    Event.DuplicateBarrelItem -> showToast(R.string.already_in_list)
                    Event.DuplicateBottleItem -> showToast(R.string.bottle_already_in_list)
                    Event.NoPriceException -> showToast(R.string.no_price_exception)
                }
            }
        }
        viewModel.realisationStateFlow.collectLatest(viewLifecycleOwner) {
            when (it) {
                BARREL -> {
                    binding.beerSelector.isVisible = true
                    binding.bottleSelector.isVisible = false
                }

                BOTTLE -> {
                    binding.beerSelector.isVisible = false
                    binding.bottleSelector.isVisible = true
                }

                NONE -> {}
            }
        }
        viewModel.editOperationState.collectLatest(viewLifecycleOwner) {
            when (it) {
                is SaleRowModel -> fillSale(it)
                is SaleBottleRowModel -> fillBottleSale(it)
                is BarrelRowModel -> fillBarrels(it)
                is MoneyRowModel -> fillMoney(it)
            }
        }
        viewModel.requestPricesFlow.collectLatest(viewLifecycleOwner) {
            it?.let { customerID -> showRequestPriceDialog(customerID) }
        }
    }

    private fun showRequestPriceDialog(customerID: Int) {
        requireContext().showInfoDialog(
            title = R.string.attention,
            text = R.string.no_price_message,
            buttonText = R.string.ok,
            theme = R.style.ThemeOverlay_MaterialComponents_Dialog,
            cancelable = false
        ) {
            val direction = AddDeliveryFragmentDirections
                .actionAddDeliveryFragmentToAddCustomerFragment(customerID)
            findNavController().navigate(direction)
        }
    }

    private fun showDebt() {
        val debtFragment = ClientDebtFragment.getInstance(clientID)
        childFragmentManager.beginTransaction()
            .replace(R.id.addDeliveryDebtContainer, debtFragment)
            .commit()
    }

    private fun fillMoney(moneyRowModel: MoneyRowModel) = with(binding) {
        addDeliveryMoneyExpander.goAway()
        when (moneyRowModel.paymentType) {
            PaymentType.Cash -> {
                addDeliveryMoneyEt.setText(moneyRowModel.amount.toString())
                500 waitFor {
                    addDeliveryMoneyCashImg.explodeAnim()
                }
            }

            PaymentType.Transfer -> {
                addDeliveryMoneyTransferEt.setText(moneyRowModel.amount.toString())
                addDeliveryMoneyTransferEt.show()
                addDeliveryMoneyTransferImg.show()
                addDeliveryTransferLariSign.show()
                addDeliveryMoneyEt.goAway()
                addDeliveryMoneyCashImg.goAway()
                addDeliveryLariSign.goAway()
                500 waitFor {
                    addDeliveryMoneyTransferImg.explodeAnim()
                }
            }
        }

        addDeliveryComment.editText?.setText(moneyRowModel.comment ?: "")
        optionSection.goAway()
    }

    private fun fillBarrels(barrelRowModel: BarrelRowModel) {
        binding.optionSection.goAway()
        binding.beerSelector.fillBarrels(barrelRowModel)
        binding.addDeliveryComment.editText?.setText(barrelRowModel.comment ?: "")
    }

    private fun fillSale(saleData: SaleRowModel) = with(binding) {
        saleData.toTempBeerItemModel(viewModel.barrels, viewModel.beerList)?.let { data ->
            beerSelector.fillBeerItemForm(data)
            addDeliveryCheckGift.isChecked = saleData.unitPrice == 0.0
            addDeliveryComment.editText?.setText(saleData.comment.orEmpty())
        }
            ?: showToast(R.string.missed_barrel_or_beer)
        updateOptionsView(addDeliveryCheckGift.isChecked)
    }

    private fun fillBottleSale(saleBottleRowModel: SaleBottleRowModel) = with(binding) {
        beerSelector.goAway()
        saleBottleRowModel.toTempBottleItemModel(viewModel.bottleList)?.let { data ->
            bottleSelector.fillBottleItemForm(data)
            addDeliveryCheckGift.isChecked = saleBottleRowModel.price == 0.0
            addDeliveryComment.editText?.setText(saleBottleRowModel.comment ?: "")
        }
            ?: showToast(getString(R.string.bottle_identification_error))
        updateOptionsView(addDeliveryCheckGift.isChecked)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.addDeliveryDoneBtn -> {
                when (operation) {
                    MITANA_BOTTLE,
                    MITANA -> {
                        viewModel.barrelOutItems.clear()
                        viewModel.moneyOut.clear()
                    }

                    K_OUT -> {
                        viewModel.moneyOut.clear()
                        viewModel.clearSaleItems()
                    }

                    M_OUT -> {
                        viewModel.clearSaleItems()
                        viewModel.barrelOutItems.clear()
                    }
                }
                if (viewModel.hasAnySaleItem().not() && operation != K_OUT)
                    tryCollectSaleItem()

                collectEmptyBarrels()
                viewModel.setMoney(
                    binding.addDeliveryMoneyEt.text.toString(),
                    binding.addDeliveryMoneyTransferEt.text.toString()
                )
                viewModel.onDoneClick(binding.addDeliveryComment.editText?.text.toString())
            }

            R.id.addDeliveryDateBtn -> {
                val datePickerDialog = DatePickerDialog(
                    requireContext(),
                    dateSetListener,
                    viewModel.saleDateCalendar.get(Calendar.YEAR),
                    viewModel.saleDateCalendar.get(Calendar.MONTH),
                    viewModel.saleDateCalendar.get(Calendar.DAY_OF_MONTH)
                )
                datePickerDialog.setCancelable(false)
                datePickerDialog.show()
            }

            R.id.addDeliveryAddSaleItemBtn -> tryCollectSaleItem()

            R.id.addDeliveryMoneyExpander -> with(binding) {
                addDeliveryMoneyTransferEt.show()
                addDeliveryMoneyTransferImg.show()
                addDeliveryTransferLariSign.show()
                addDeliveryMoneyExpander.goAway()
            }

            R.id.addDeliveryMoneyCashImg -> with(binding) {
                if (operation == M_OUT) {
                    addDeliveryMoneyEt.goAway()
                    addDeliveryMoneyCashImg.goAway()
                    addDeliveryLariSign.goAway()
                    addDeliveryMoneyTransferEt.show()
                    addDeliveryMoneyTransferImg.show()
                    addDeliveryTransferLariSign.show()

                    addDeliveryMoneyTransferEt.text = addDeliveryMoneyEt.text
                    addDeliveryMoneyEt.setText("")
                }
            }

            R.id.addDeliveryMoneyTransferImg -> with(binding) {
                if (operation == M_OUT) {
                    addDeliveryMoneyTransferEt.goAway()
                    addDeliveryMoneyTransferImg.goAway()
                    addDeliveryTransferLariSign.goAway()
                    addDeliveryMoneyEt.show()
                    addDeliveryMoneyCashImg.show()
                    addDeliveryLariSign.show()

                    addDeliveryMoneyEt.text = addDeliveryMoneyTransferEt.text
                    addDeliveryMoneyTransferEt.setText("")
                }
            }

            R.id.optionIcon -> {
                toggleOptions()
            }
        }
    }

    private fun toggleOptions() = with(binding) {
        optionIcon.setImageResource(if (addDeliveryCheckGift.isVisible) R.drawable.ic_more_vert else R.drawable.ic_arrow_right)
        addDeliveryCheckReplace.isVisible = !addDeliveryCheckGift.isVisible
        addDeliveryCheckGift.isVisible = !addDeliveryCheckGift.isVisible
    }

    private fun tryCollectSaleItem() {
        when (viewModel.realisationType) {
            BARREL -> if (binding.beerSelector.formIsValid())
                viewModel.addSaleItemToList(binding.beerSelector.getTempBeerItem())

            BOTTLE -> if (binding.bottleSelector.isFormValid())
                viewModel.addBottleSaleItem(binding.bottleSelector.getTempBottleItem())

            else -> showToast(R.string.fill_data)
        }
    }

    private fun collectEmptyBarrels() = with(binding) {
        viewModel.barrelOutItems.clear()
        if (operation == null) {
            if (addDeliveryBarrelOutputCount1.amount > 0)
                viewModel.addBarrelToList(1, addDeliveryBarrelOutputCount1.amount)
            if (addDeliveryBarrelOutputCount2.amount > 0)
                viewModel.addBarrelToList(2, addDeliveryBarrelOutputCount2.amount)
            if (addDeliveryBarrelOutputCount3.amount > 0)
                viewModel.addBarrelToList(3, addDeliveryBarrelOutputCount3.amount)
            if (addDeliveryBarrelOutputCount4.amount > 0)
                viewModel.addBarrelToList(4, addDeliveryBarrelOutputCount4.amount)
        } else if (operation == K_OUT) {
            viewModel.addBarrelToList(
                beerSelector.getBarrelID(),
                beerSelector.getBarrelsCount()
            )
        }
    }

    private fun checkForm() = with(binding) {
        addDeliveryAddSaleItemBtn.backgroundTintList =
            if (
                viewModel.realisationType == BARREL && beerSelector.formIsValid()
                ||
                viewModel.realisationType == BOTTLE && bottleSelector.isFormValid()
            )
                ColorStateList.valueOf(Color.GREEN)
            else
                ColorStateList.valueOf(Color.RED)

        addDeliveryTotalPrice.text = getString(R.string.cost, viewModel.getPrice())
    }

    override fun onResume() {
        super.onResume()
        viewModel.updateCustomer()
    }
}
