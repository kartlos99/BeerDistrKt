package com.example.beerdistrkt.fragPages.realisation

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.beerdistrkt.BaseFragment
import com.example.beerdistrkt.R
import com.example.beerdistrkt.common.fragments.ClientDebtFragment
import com.example.beerdistrkt.customView.TempBeerRowView
import com.example.beerdistrkt.customView.TempBottleRowView
import com.example.beerdistrkt.databinding.AddDeliveryFragmentBinding
import com.example.beerdistrkt.fragPages.realisation.RealisationType.BARREL
import com.example.beerdistrkt.fragPages.realisation.RealisationType.BOTTLE
import com.example.beerdistrkt.fragPages.realisation.models.BarrelRowModel
import com.example.beerdistrkt.fragPages.realisation.models.MoneyRowModel
import com.example.beerdistrkt.fragPages.realisation.models.SaleRowModel
import com.example.beerdistrkt.fragPages.realisationtotal.models.PaymentType
import com.example.beerdistrkt.getViewModel
import com.example.beerdistrkt.notifyNewComment
import com.example.beerdistrkt.setText
import com.example.beerdistrkt.setTint
import com.example.beerdistrkt.simpleTextChangeListener
import com.example.beerdistrkt.text
import com.example.beerdistrkt.utils.ApiResponseState
import com.example.beerdistrkt.utils.explodeAnim
import com.example.beerdistrkt.utils.goAway
import com.example.beerdistrkt.utils.show
import com.example.beerdistrkt.waitFor
import kotlinx.coroutines.flow.collectLatest
import java.util.Calendar

class AddDeliveryFragment : BaseFragment<AddDeliveryViewModel>(), View.OnClickListener {

    override val viewModel by lazy {
        getViewModel { AddDeliveryViewModel(clientID, orderID) }
    }

    private val clientID by lazy {
        val args = AddDeliveryFragmentArgs.fromBundle(arguments ?: Bundle())
        args.clientObjectID
    }
    private val orderID by lazy {
        val args = AddDeliveryFragmentArgs.fromBundle(arguments ?: Bundle())
        args.orderID
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

    private lateinit var vBinding: AddDeliveryFragmentBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        vBinding = AddDeliveryFragmentBinding.inflate(inflater)
        vBinding.lifecycleOwner = this
        return vBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = AddDeliveryFragmentArgs.fromBundle(arguments ?: Bundle())
        viewModel.recordID = args.recordID
        viewModel.operation = args.operacia

        vBinding.addDeliveryDoneBtn.setOnClickListener(this)
        vBinding.addDeliveryDateBtn.setOnClickListener(this)
        vBinding.addDeliveryAddSaleItemBtn.setOnClickListener(this)
        vBinding.addDeliveryMoneyExpander.setOnClickListener(this)
        vBinding.addDeliveryMoneyCashImg.setOnClickListener(this)
        vBinding.addDeliveryMoneyTransferImg.setOnClickListener(this)
        vBinding.initView()

        initViewModel()
        checkForm()
        setPageTitle(resources.getString(R.string.delivery))
        showDebt()
    }

    private fun AddDeliveryFragmentBinding.initView() {
        if (viewModel.operation != null) {
            viewModel.getRecordData()
            addDeliveryBarrelGr.isVisible = false
        }
        addDeliveryhideOnEditGroup.isVisible = viewModel.operation == null
        beerSelector.beerGroupVisibleIf(viewModel.operation != K_OUT)

        when (viewModel.operation) {
            MITANA -> {
                addDeliveryMoneyGr.isVisible = false
                addDeliveryCheckReplace.isVisible = false
            }

            M_OUT -> {
                addDeliveryMitanaGr.isVisible = false
                addDeliveryCheckGift.isVisible = false
                addDeliveryCheckReplace.isVisible = false
            }

            K_OUT -> {
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
            viewModel.cansList,
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
        with(vBinding.addDeliveryComment) {
            if (text().isEmpty() && isChecked) setText(textRes)
            if (text() == getString(textRes) && !isChecked) setText("")
        }

    private fun getColorForValidationIndicator(value: CharSequence): Int {
        return if (value.isNotEmpty() && (value.toString().toDoubleOrNull() ?: .0) > .0)
            R.color.green_08
        else
            R.color.gray_6
    }

    private fun initViewModel() {
        viewModel.clientLiveData.observe(viewLifecycleOwner) {
            vBinding.addDeliveryClientInfo.text = it.customer.dasaxeleba
        }
        viewModel.beerListLiveData.observe(viewLifecycleOwner) {
            vBinding.beerSelector.updateBeers(it)
        }
        viewModel.bottleListLiveData.observe(viewLifecycleOwner) {
            vBinding.bottleSelector.updateBottles(it)
        }
        viewModel.tempRealisationLiveData.observe(viewLifecycleOwner) {
            vBinding.beerSelector.resetForm()
            vBinding.bottleSelector.resetForm()
            vBinding.addDeliveryTempContainer.removeAllViews()
            it.byBarrels.forEach { saleItem ->
                vBinding.addDeliveryTempContainer.addView(
                    TempBeerRowView(context = requireContext(), rowData = saleItem)
                )
            }
            it.byBottles.forEach { bottleSaleItem ->
                vBinding.addDeliveryTempContainer.addView(
                    TempBottleRowView(context = requireContext(), rowData = bottleSaleItem)
                )
            }
        }
        viewModel.saleDayLiveData.observe(viewLifecycleOwner) {
            vBinding.addDeliveryDateBtn.text = it
        }
        viewModel.addSaleLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is ApiResponseState.Success -> {
                    showToast(it.data)
                    if (!vBinding.addDeliveryComment.editText?.text.isNullOrEmpty())
                        notifyNewComment(vBinding.addDeliveryComment.editText?.text.toString())
                    findNavController().navigateUp()
                }

                else -> {}
            }
        }
        viewModel.saleItemEditLiveData.observe(viewLifecycleOwner) {
            if (it != null) {
                fillSale(it)
                viewModel.saleItemEditLiveData.value = null
            }
        }
        viewModel.kOutEditLiveData.observe(viewLifecycleOwner) {
            if (it != null) {
                fillBarrels(it)
                viewModel.kOutEditLiveData.value = null
            }
        }
        viewModel.mOutEditLiveData.observe(viewLifecycleOwner) {
            if (it != null) {
                fillMoney(it)
                viewModel.mOutEditLiveData.value = null
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
        lifecycleScope.launchWhenStarted {
            viewModel.realisationStateFlow.collectLatest {
                when (it) {
                    BARREL -> {
                        vBinding.beerSelector.isVisible = true
                        vBinding.bottleSelector.isVisible = false
                    }

                    BOTTLE -> {
                        vBinding.beerSelector.isVisible = false
                        vBinding.bottleSelector.isVisible = true
                    }
                }
            }
        }
    }

    private fun showDebt() {
        val debtFragment = ClientDebtFragment.getInstance(clientID)
        childFragmentManager.beginTransaction()
            .replace(R.id.addDeliveryDebtContainer, debtFragment)
            .commit()
    }

    private fun fillMoney(moneyRowModel: MoneyRowModel) {
        vBinding.addDeliveryMoneyExpander.goAway()
        when (moneyRowModel.paymentType) {
            PaymentType.Cash -> {
                vBinding.addDeliveryMoneyEt.setText(moneyRowModel.amount.toString())
                500 waitFor {
                    vBinding.addDeliveryMoneyCashImg.explodeAnim()
                }
            }

            PaymentType.Transfer -> {
                vBinding.addDeliveryMoneyTransferEt.setText(moneyRowModel.amount.toString())
                vBinding.addDeliveryMoneyTransferEt.show()
                vBinding.addDeliveryMoneyTransferImg.show()
                vBinding.addDeliveryTransferLariSign.show()
                vBinding.addDeliveryMoneyEt.goAway()
                vBinding.addDeliveryMoneyCashImg.goAway()
                vBinding.addDeliveryLariSign.goAway()
                500 waitFor {
                    vBinding.addDeliveryMoneyTransferImg.explodeAnim()
                }
            }
        }

        vBinding.addDeliveryComment.editText?.setText(moneyRowModel.comment ?: "")
    }

    private fun fillBarrels(barrelRowModel: BarrelRowModel) {

        vBinding.beerSelector.fillBarrels(barrelRowModel)
        vBinding.addDeliveryComment.editText?.setText(barrelRowModel.comment ?: "")
    }

    private fun fillSale(saleData: SaleRowModel) {
        val data = saleData.toTempBeerItemModel(viewModel.cansList, viewModel.beerList)
        vBinding.beerSelector.fillBeerItemForm(data)
        vBinding.addDeliveryCheckGift.isChecked = saleData.unitPrice == 0.0
        vBinding.addDeliveryComment.editText?.setText(saleData.comment ?: "")
    }

    private fun fillBottleSale() {

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.addDeliveryDoneBtn -> {
                when (viewModel.operation) {
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
                if (viewModel.hasAnySaleItem().not() && viewModel.operation != K_OUT)
                    tryCollectSaleItem()

                collectEmptyBarrels()
                viewModel.setMoney(
                    vBinding.addDeliveryMoneyEt.text.toString(),
                    vBinding.addDeliveryMoneyTransferEt.text.toString()
                )
                viewModel.onDoneClick(vBinding.addDeliveryComment.editText?.text.toString())
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

            R.id.addDeliveryAddSaleItemBtn -> {
                when {
                    viewModel.realisationType == BARREL && vBinding.beerSelector.formIsValid() ->
                        viewModel.addSaleItemToList(vBinding.beerSelector.getTempBeerItem())

                    viewModel.realisationType == BOTTLE && vBinding.bottleSelector.isFormValid() ->
                        viewModel.addBottleSaleItem(vBinding.bottleSelector.getTempBottleItem())

                    else ->
                        showToast(R.string.fill_data)
                }
            }

            R.id.addDeliveryMoneyExpander -> {
                vBinding.addDeliveryMoneyTransferEt.show()
                vBinding.addDeliveryMoneyTransferImg.show()
                vBinding.addDeliveryTransferLariSign.show()
                vBinding.addDeliveryMoneyExpander.goAway()
            }

            R.id.addDeliveryMoneyCashImg -> {
                if (viewModel.operation == M_OUT) {
                    vBinding.addDeliveryMoneyEt.goAway()
                    vBinding.addDeliveryMoneyCashImg.goAway()
                    vBinding.addDeliveryLariSign.goAway()
                    vBinding.addDeliveryMoneyTransferEt.show()
                    vBinding.addDeliveryMoneyTransferImg.show()
                    vBinding.addDeliveryTransferLariSign.show()

                    vBinding.addDeliveryMoneyTransferEt.text = vBinding.addDeliveryMoneyEt.text
                    vBinding.addDeliveryMoneyEt.setText("")
                }
            }

            R.id.addDeliveryMoneyTransferImg -> {
                if (viewModel.operation == M_OUT) {
                    vBinding.addDeliveryMoneyTransferEt.goAway()
                    vBinding.addDeliveryMoneyTransferImg.goAway()
                    vBinding.addDeliveryTransferLariSign.goAway()
                    vBinding.addDeliveryMoneyEt.show()
                    vBinding.addDeliveryMoneyCashImg.show()
                    vBinding.addDeliveryLariSign.show()

                    vBinding.addDeliveryMoneyEt.text = vBinding.addDeliveryMoneyTransferEt.text
                    vBinding.addDeliveryMoneyTransferEt.setText("")
                }
            }
        }
    }

    private fun tryCollectSaleItem() {
        when (viewModel.realisationType) {
            BARREL -> if (vBinding.beerSelector.formIsValid())
                viewModel.addSaleItemToList(vBinding.beerSelector.getTempBeerItem())

            BOTTLE -> if (vBinding.bottleSelector.isFormValid())
                viewModel.addBottleSaleItem(vBinding.bottleSelector.getTempBottleItem())
        }
    }

    private fun collectEmptyBarrels() {
        viewModel.barrelOutItems.clear()
        if (viewModel.operation == null) {
            if (vBinding.addDeliveryBarrelOutputCount1.amount > 0)
                viewModel.addBarrelToList(1, vBinding.addDeliveryBarrelOutputCount1.amount)
            if (vBinding.addDeliveryBarrelOutputCount2.amount > 0)
                viewModel.addBarrelToList(2, vBinding.addDeliveryBarrelOutputCount2.amount)
            if (vBinding.addDeliveryBarrelOutputCount3.amount > 0)
                viewModel.addBarrelToList(3, vBinding.addDeliveryBarrelOutputCount3.amount)
            if (vBinding.addDeliveryBarrelOutputCount4.amount > 0)
                viewModel.addBarrelToList(4, vBinding.addDeliveryBarrelOutputCount4.amount)
        } else if (viewModel.operation == K_OUT) {
            viewModel.addBarrelToList(
                vBinding.beerSelector.selectedCan?.id ?: 0,
                vBinding.beerSelector.getBarrelsCount()
            )
        }
    }

    private fun checkForm() = with(vBinding) {
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

    companion object {
        const val MITANA = "mitana"
        const val K_OUT = "kout"
        const val M_OUT = "mout"
    }
}