package com.example.beerdistrkt.fragPages.mitana

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.beerdistrkt.*
import com.example.beerdistrkt.common.fragments.ClientDebtFragment
import com.example.beerdistrkt.customView.TempBeerRowView
import com.example.beerdistrkt.databinding.AddDeliveryFragmentBinding
import com.example.beerdistrkt.fragPages.mitana.models.BarrelRowModel
import com.example.beerdistrkt.fragPages.mitana.models.MoneyRowModel
import com.example.beerdistrkt.fragPages.mitana.models.SaleRowModel
import com.example.beerdistrkt.fragPages.sales.models.PaymentType
import com.example.beerdistrkt.utils.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.*

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

        bottleSelector.initView(
            viewModel.bottleList,
            ::checkForm
        )

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
        realisationTypeSelector.addOnButtonCheckedListener { group, checkedId, isChecked ->

            Log.d(TAG, "initView: checkedId, isChecked = $checkedId, $isChecked")
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
        viewModel.clientLiveData.observe(viewLifecycleOwner, Observer {
            vBinding.addDeliveryClientInfo.text = it.obieqti.dasaxeleba
        })
        viewModel.beerListLiveData.observe(viewLifecycleOwner, Observer {
            vBinding.beerSelector.updateBeers(it)
        })
        viewModel.saleItemsLiveData.observe(viewLifecycleOwner, Observer {
            vBinding.beerSelector.resetForm()
            vBinding.addDeliveryTempContainer.removeAllViews()
            it.forEach { saleItem ->
                vBinding.addDeliveryTempContainer.addView(
                    TempBeerRowView(context = requireContext(), rowData = saleItem)
                )
            }
        })
        viewModel.saleDayLiveData.observe(viewLifecycleOwner, Observer {
            vBinding.addDeliveryDateBtn.text = it
        })
        viewModel.saleItemDuplicateLiveData.observe(viewLifecycleOwner, Observer {
            if (it) {
                showToast(R.string.already_in_list)
                viewModel.saleItemDuplicateLiveData.value = false
            }
        })
        viewModel.addSaleLiveData.observe(viewLifecycleOwner, Observer {
            when (it) {
                is ApiResponseState.Success -> {
                    showToast(it.data)
                    if (!vBinding.addDeliveryComment.editText?.text.isNullOrEmpty())
                        notifyNewComment(vBinding.addDeliveryComment.editText?.text.toString())
                    findNavController().navigateUp()
                }
                else -> {}
            }
        })
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
        lifecycleScope.launch {
            viewModel.infoSharedFlow.collectLatest {
                showToast(it)
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
                        viewModel.saleItemsList.clear()
                    }
                    M_OUT -> {
                        viewModel.saleItemsList.clear()
                        viewModel.barrelOutItems.clear()
                    }
                }
                if (vBinding.beerSelector.formIsValid() && viewModel.saleItemsList.isEmpty() && viewModel.operation != K_OUT)
                    viewModel.addSaleItemToList(vBinding.beerSelector.getTempBeerItem())
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
                if (vBinding.beerSelector.formIsValid())
                    viewModel.addSaleItemToList(vBinding.beerSelector.getTempBeerItem())
                else
                    showToast(R.string.fill_data)
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
            if (beerSelector.formIsValid())
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
