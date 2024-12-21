package com.example.beerdistrkt.fragPages.orders

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.beerdistrkt.BaseFragment
import com.example.beerdistrkt.R
import com.example.beerdistrkt.collectLatest
import com.example.beerdistrkt.common.fragments.ClientDebtFragment
import com.example.beerdistrkt.customView.TempBeerRowView
import com.example.beerdistrkt.customView.TempBottleRowView
import com.example.beerdistrkt.databinding.AddOrdersFragmentBinding
import com.example.beerdistrkt.fragPages.realisation.RealisationType.BARREL
import com.example.beerdistrkt.fragPages.realisation.RealisationType.BOTTLE
import com.example.beerdistrkt.fragPages.realisation.RealisationType.NONE
import com.example.beerdistrkt.models.Order
import com.example.beerdistrkt.notifyNewComment
import com.example.beerdistrkt.paramViewModels
import com.example.beerdistrkt.utils.ApiResponseState
import com.example.beerdistrkt.utils.Session
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import java.util.Calendar

@AndroidEntryPoint
class AddOrdersFragment : BaseFragment<AddOrdersViewModel>(), View.OnClickListener,
    AdapterView.OnItemSelectedListener {

    override val viewModel by paramViewModels<AddOrdersViewModel, AddOrdersViewModel.Factory> { factory ->
        factory.create(clientID, orderID)
    }

    private lateinit var vBinding: AddOrdersFragmentBinding

    private val clientID by lazy {
        val args = AddOrdersFragmentArgs.fromBundle(arguments ?: Bundle())
        args.clientObjectID
    }
    private val orderID by lazy {
        val args = AddOrdersFragmentArgs.fromBundle(arguments ?: Bundle())
        args.orderID
    }

    private var dateSetListener = OnDateSetListener { _, year, month, day ->
        viewModel.onOrderDateSelected(year, month, day)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        vBinding = AddOrdersFragmentBinding.inflate(inflater)
        return vBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vBinding.addOrderAddItemBtn.setOnClickListener(this)
        vBinding.addOrderDoneBtn.setOnClickListener(this)
        vBinding.addOrderOrderDate.setOnClickListener(this)
        vBinding.initView()

        checkForm()
        (activity as AppCompatActivity).supportActionBar?.title = getString(
            if (viewModel.editingOrderID > 0)
                R.string.edit_order
            else
                R.string.add_order
        )
        showDebt()
        initViewModel()
    }

    fun AddOrdersFragmentBinding.initView() {

        addOrderStatusGroup.isVisible = viewModel.editingOrderID > 0

        beerSelector.initView(
            viewModel.beers,
            viewModel.barrels,
            ::checkForm
        )
        beerSelector.onDeleteClick = {
            viewModel.removeOrderItemFromList(it)
        }
        bottleSelector.initView(
            viewModel.bottleList,
            ::checkForm
        )
        bottleSelector.onDeleteClick = {
            viewModel.removeOrderItemFromList(it)
        }
        initDistributorSpinner()

        addOrderStatusSpinner.adapter = ArrayAdapter(
            requireContext(),
            R.layout.simple_dropdown_item,
            viewModel.orderStatusList.map {
                resources.getString(it.textRes)
            }
        )
        addOrderStatusSpinner.onItemSelectedListener = this@AddOrdersFragment

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

    private fun initDistributorSpinner() {
        val userAdapter = ArrayAdapter(
            requireContext(),
            R.layout.simple_dropdown_item,
            viewModel.getDistributorNamesList()
        )
        vBinding.addOrderDistributorSpinner.adapter = userAdapter
        vBinding.addOrderDistributorSpinner.onItemSelectedListener = this
        vBinding.addOrderDistributorSpinner.setSelection(
            viewModel.getDistributorIndex(Session.get().userID ?: return)
        )
    }

    private fun initRegionSpinner() {
        val regionAdapter = ArrayAdapter(
            requireContext(),
            R.layout.simple_dropdown_item,
            viewModel.availableRegions.map { it.name }
        )
        vBinding.addOrderDistributorRegionSpinner.apply {
            adapter = regionAdapter
            onItemSelectedListener = this@AddOrdersFragment
            setSelection(
                viewModel.availableRegions
                    .map { it.regionID }
                    .indexOf(Session.get().region?.regionID)
            )
        }
    }

    private fun checkForm() = with(vBinding) {
        addOrderAddItemBtn.backgroundTintList = if (
            viewModel.realisationType == BARREL && beerSelector.formIsValid()
            ||
            viewModel.realisationType == BOTTLE && bottleSelector.isFormValid()
        )
            ColorStateList.valueOf(Color.GREEN)
        else
            ColorStateList.valueOf(Color.RED)
    }

    private fun initViewModel() {
        viewModel.clientLiveData.observe(viewLifecycleOwner) { customer ->
            vBinding.addOrderClientInfo.text = customer.name
            vBinding.addOrderCheckBox.isChecked = customer.hasCheck
        }
        viewModel.tempOrderLiveData.observe(viewLifecycleOwner) {
            vBinding.beerSelector.resetForm()
            vBinding.bottleSelector.resetForm()
            vBinding.addOrderItemsContainer.removeAllViews()
            it.byBarrels.forEach { orderItem ->
                vBinding.addOrderItemsContainer.addView(
                    TempBeerRowView(context = requireContext(), rowData = orderItem)
                )
            }
            it.byBottles.forEach { bottleSaleItem ->
                vBinding.addOrderItemsContainer.addView(
                    TempBottleRowView(context = requireContext(), rowData = bottleSaleItem)
                )
            }
        }
        viewModel.orderDayLiveData.observe(viewLifecycleOwner) {
            vBinding.addOrderOrderDate.text = it
        }
        viewModel.addOrderLiveData.observe(viewLifecycleOwner) {
            if (it is ApiResponseState.Success) {
                showToast(it.data)
                if (!vBinding.addOrderComment.text.isNullOrEmpty())
                    notifyNewComment(vBinding.addOrderComment.text.toString())
                findNavController().navigateUp()
            }
        }
        viewModel.getOrderLiveData.observe(viewLifecycleOwner) {
            if (it != null) {
                fillOrderForm(it)
                viewModel.getOrderLiveData.value = null
            }
        }
        viewModel.orderItemEditLiveData.observe(viewLifecycleOwner) {
            if (it != null) {
                vBinding.beerSelector.fillBeerItemForm(it)
                vBinding.realisationTypeSelector.check(R.id.realizationByBarrel)
                viewModel.switchToBarrel()
                viewModel.orderItemEditLiveData.value = null
            }
        }
        viewModel.bottleOrderItemEditLiveData.observe(viewLifecycleOwner) {
            if (it != null) {
                viewModel.switchToBottle()
                vBinding.bottleSelector.fillBottleItemForm(it)
                vBinding.realisationTypeSelector.check(R.id.realizationByBottle)
                viewModel.bottleOrderItemEditLiveData.value = null
            }
        }
        viewModel.getDebtLiveData.observe(viewLifecycleOwner) {
            if (it is ApiResponseState.Success) {
                with(vBinding) {
                    addOrderWarning.text = getString(R.string.need_cleaning, it.data.passDays)
                    addOrderWarning.isVisible = it.data.needCleaning == 1
                    initRegionSpinner()
                    addOrderDistributorRegionTitle.isVisible = it.data.availableRegions.size > 1
                    addOrderDistributorRegionSpinner.isVisible = it.data.availableRegions.size > 1
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.eventsFlow.collectLatest {
                when (it) {
                    Event.DuplicateBarrelItem -> showToast(R.string.already_in_list)
                    Event.DuplicateBottleItem -> showToast(R.string.bottle_already_in_list)
                }
            }
        }
        viewModel.realisationStateFlow.collectLatest(viewLifecycleOwner) {
            when (it) {
                BARREL -> {
                    vBinding.beerSelector.isVisible = true
                    vBinding.bottleSelector.isVisible = false
                }

                BOTTLE -> {
                    vBinding.beerSelector.isVisible = false
                    vBinding.bottleSelector.isVisible = true
                }

                NONE -> {}
            }
        }
    }

    private fun showDebt() {
        val debtFragment = ClientDebtFragment.getInstance(clientID)
        childFragmentManager.beginTransaction()
            .replace(R.id.addOrderDebtContainer, debtFragment)
            .commit()
    }

    private fun fillOrderForm(order: Order) = with(vBinding) {
        addOrderClientInfo.text = order.customer?.name
        addOrderWarning.text = getString(R.string.need_cleaning, order.passDays)
        addOrderWarning.isVisible = order.needCleaning == 1
        addOrderCheckBox.isChecked = order.isChecked()
        addOrderComment.setText(order.comment)
        addOrderOrderDate.text = order.orderDate
        addOrderDistributorSpinner.setSelection(
            viewModel.getDistributorIndex(order.distributorID.toString())
        )
        addOrderStatusSpinner.setSelection(
            viewModel.orderStatusList.indexOf(order.orderStatus)
        )
        addOrderStatusGroup.isVisible = viewModel.editingOrderID > 0
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.edit_order)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.addOrderAddItemBtn -> tryCollectOrderItem()

            R.id.addOrderDoneBtn -> {
                if (viewModel.hasNoOrderItems())
                    tryCollectOrderItem()

                if (viewModel.hasNoOrderItems().not()) {
                    if (viewModel.editingOrderID > 0)
                        viewModel.editOrder(
                            vBinding.addOrderComment.text.toString(),
                            vBinding.addOrderCheckBox.isChecked
                        )
                    else
                        viewModel.addOrder(
                            vBinding.addOrderComment.text.toString(),
                            vBinding.addOrderCheckBox.isChecked
                        )
                } else
                    showToast(R.string.fill_data)
            }

            R.id.addOrderOrderDate -> {
                val datePickerDialog = DatePickerDialog(
                    requireContext(),
                    dateSetListener,
                    viewModel.orderDateCalendar.get(Calendar.YEAR),
                    viewModel.orderDateCalendar.get(Calendar.MONTH),
                    viewModel.orderDateCalendar.get(Calendar.DAY_OF_MONTH)
                )
                datePickerDialog.setCancelable(false)
                datePickerDialog.show()
            }
        }
    }

    private fun tryCollectOrderItem() = when {
        viewModel.realisationType == BARREL && vBinding.beerSelector.formIsValid() ->
            viewModel.addOrderItemToList(vBinding.beerSelector.getTempBeerItem())

        viewModel.realisationType == BOTTLE && vBinding.bottleSelector.isFormValid() ->
            viewModel.addBottleOrderItem(vBinding.bottleSelector.getTempBottleItem())

        else -> showToast(R.string.fill_data)
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (parent?.id) {
            R.id.addOrderDistributorSpinner ->
                viewModel.selectedDistributor = viewModel.visibleDistributors[position]

            R.id.addOrderStatusSpinner ->
                viewModel.selectedStatus = viewModel.orderStatusList[position]

            R.id.addOrderDistributorRegionSpinner -> {
                viewModel.updateDistributorList(viewModel.availableRegions[position].regionID.toInt())
                initDistributorSpinner()
            }
        }
    }
}
