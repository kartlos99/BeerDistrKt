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
import androidx.navigation.fragment.findNavController
import com.example.beerdistrkt.BaseFragment
import com.example.beerdistrkt.R
import com.example.beerdistrkt.common.fragments.ClientDebtFragment
import com.example.beerdistrkt.customView.TempBeerRowView
import com.example.beerdistrkt.databinding.AddOrdersFragmentBinding
import com.example.beerdistrkt.getViewModel
import com.example.beerdistrkt.models.Order
import com.example.beerdistrkt.notifyNewComment
import com.example.beerdistrkt.utils.ApiResponseState
import com.example.beerdistrkt.utils.Session
import com.example.beerdistrkt.utils.visibleIf
import java.util.*

class AddOrdersFragment : BaseFragment<AddOrdersViewModel>(), View.OnClickListener,
    AdapterView.OnItemSelectedListener {

    override val viewModel by lazy {
        getViewModel { AddOrdersViewModel(clientID, orderID) }
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
        vBinding.lifecycleOwner = this

        return vBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewModel()

        vBinding.addOrderStatusGroup.visibleIf(viewModel.editingOrderID > 0)

        vBinding.addOrderAddItemBtn.setOnClickListener {
            if (vBinding.beerSelector.formIsValid())
                viewModel.addOrderItemToList(vBinding.beerSelector.getTempBeerItem())
            else
                showToast(R.string.fill_data)
        }
        vBinding.addOrderDoneBtn.setOnClickListener(this)
        vBinding.addOrderOrderDate.setOnClickListener(this)

        vBinding.beerSelector.withPrices = true
        vBinding.beerSelector.initView(
            viewModel.beerList,
            viewModel.cansList,
            ::checkForm
        )
        vBinding.beerSelector.onDeleteClick = {
            viewModel.removeOrderItemFromList(it)
        }
        initDistributorSpinner()

        vBinding.addOrderStatusSpinner.adapter = ArrayAdapter(
            requireContext(),
            R.layout.simple_dropdown_item,
            viewModel.orderStatusList.map {
                resources.getString(it.textRes)
            }
        )
        vBinding.addOrderStatusSpinner.onItemSelectedListener = this

        checkForm()
        (activity as AppCompatActivity).supportActionBar?.title = getString(
            if (viewModel.editingOrderID > 0)
                R.string.edit_order
            else
                R.string.add_order
        )
        showDebt()
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

    private fun checkForm() {
        vBinding.addOrderAddItemBtn.backgroundTintList = if (vBinding.beerSelector.formIsValid())
            ColorStateList.valueOf(Color.GREEN)
        else
            ColorStateList.valueOf(Color.RED)
    }

    private fun initViewModel() {
        viewModel.clientLiveData.observe(viewLifecycleOwner) {
            vBinding.addOrderClientInfo.text = it.obieqti.dasaxeleba
            vBinding.addOrderCheckBox.isChecked = it.obieqti.chek == "1"
        }
        viewModel.orderItemsLiveData.observe(viewLifecycleOwner) {
            vBinding.beerSelector.resetForm()
            vBinding.addOrderItemsContainer.removeAllViews()
            it.forEach { orderItem ->
                vBinding.addOrderItemsContainer.addView(
                    TempBeerRowView(context = requireContext(), rowData = orderItem)
                )
            }
        }
        viewModel.orderDayLiveData.observe(viewLifecycleOwner) {
            vBinding.addOrderOrderDate.text = it
        }
        viewModel.orderItemDuplicateLiveData.observe(viewLifecycleOwner) {
            if (it) {
                showToast(R.string.already_in_list)
                viewModel.orderItemDuplicateLiveData.value = false
            }
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

                viewModel.orderItemEditLiveData.value = null
            }
        }
        viewModel.getDebtLiveData.observe(viewLifecycleOwner) {
            if (it is ApiResponseState.Success) {
                with(vBinding) {
                    addOrderWarning.text = getString(R.string.need_cleaning, it.data.passDays)
                    addOrderWarning.visibleIf(it.data.needCleaning == 1)
                    initRegionSpinner()
                    addOrderDistributorRegionTitle.visibleIf(it.data.availableRegions.size > 1)
                    addOrderDistributorRegionSpinner.visibleIf(it.data.availableRegions.size > 1)
                }
            }
        }
    }

    private fun showDebt() {
        val debtFragment = ClientDebtFragment.getInstance(clientID)
        childFragmentManager.beginTransaction()
            .replace(R.id.addOrderDebtContainer, debtFragment)
            .commit()
    }

    private fun fillOrderForm(order: Order) {
        with(vBinding) {
            addOrderClientInfo.text = order.client.dasaxeleba
            addOrderWarning.text = getString(R.string.need_cleaning, order.passDays)
            addOrderWarning.visibleIf(order.needCleaning == 1)
            addOrderCheckBox.isChecked = order.isChecked()
            addOrderComment.setText(order.comment)
            addOrderOrderDate.text = order.orderDate
            addOrderDistributorSpinner.setSelection(
                viewModel.getDistributorIndex(order.distributorID.toString())
            )
            addOrderStatusSpinner.setSelection(
                viewModel.orderStatusList.indexOf(order.orderStatus)
            )
            addOrderStatusGroup.visibleIf(viewModel.editingOrderID > 0)
            (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.edit_order)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.addOrderDoneBtn -> {
                if (vBinding.beerSelector.formIsValid() && viewModel.orderItemsList.isEmpty())
                    viewModel.addOrderItemToList(vBinding.beerSelector.getTempBeerItem())
                if (viewModel.orderItemsList.isNotEmpty()) {
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

    override fun onNothingSelected(parent: AdapterView<*>?) {}

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (parent?.id) {
            R.id.addOrderDistributorSpinner ->
                viewModel.selectedDistributor = viewModel.usersList[position]
            R.id.addOrderStatusSpinner ->
                viewModel.selectedStatus = viewModel.orderStatusList[position]
            R.id.addOrderDistributorRegionSpinner -> {
                viewModel.updateDistributorList(viewModel.availableRegions[position].regionID.toInt())
                initDistributorSpinner()
            }
        }
    }
}
