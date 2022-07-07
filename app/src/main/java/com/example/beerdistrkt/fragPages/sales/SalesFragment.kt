package com.example.beerdistrkt.fragPages.sales

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.beerdistrkt.*
import com.example.beerdistrkt.adapters.SalesAdapter
import com.example.beerdistrkt.databinding.SalesFragmentBinding
import com.example.beerdistrkt.fragPages.login.models.Permission
import com.example.beerdistrkt.fragPages.sales.adapter.BarrelsIOAdapter
import com.example.beerdistrkt.models.BarrelIO
import com.example.beerdistrkt.utils.ApiResponseState
import com.example.beerdistrkt.utils.Session
import com.google.android.material.bottomsheet.BottomSheetBehavior
import java.util.*

class SalesFragment : BaseFragment<SalesViewModel>(), AdapterView.OnItemSelectedListener {

    companion object {
        fun newInstance() = SalesFragment()
        const val TAG = "Sales Frag"
    }

    private lateinit var vBinding: SalesFragmentBinding
    override val viewModel: SalesViewModel by lazy {
        getActCtxViewModel()
    }

    private lateinit var expenseBottomSheet: BottomSheetBehavior<*>

    var dateSetListener = OnDateSetListener { _, year, month, day ->
        viewModel.onDataSelected(year, month, day)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        vBinding = SalesFragmentBinding.inflate(inflater)

        vBinding.viewModel = viewModel
        vBinding.lifecycleOwner = this

        vBinding.salesSetDateBtn.setOnClickListener {
            context?.let {
                val datePickerDialog = DatePickerDialog(
                    it,
                    dateSetListener,
                    viewModel.calendar.get(Calendar.YEAR),
                    viewModel.calendar.get(Calendar.MONTH),
                    viewModel.calendar.get(Calendar.DAY_OF_MONTH)
                )
                datePickerDialog.datePicker.maxDate = Date().time
                datePickerDialog.setCancelable(false)
                datePickerDialog.show()
            }
        }
        return vBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.formUsersList()
        vBinding.salesDistributorsSpinner.adapter = ArrayAdapter(
            requireContext(),
            R.layout.simple_dropdown_item,
            viewModel.usersList.map { it.username }
        )
        vBinding.salesDistributorsSpinner.onItemSelectedListener = this
        if (!Session.get().hasPermission(Permission.SeeOthersRealization)) {
            vBinding.salesDistributorsSpinner.setSelection(
                viewModel.usersList.map { it.id }.indexOf(Session.get().userID)
            )
            vBinding.salesDistributorsSpinner.isEnabled = false
        }
        if (!Session.get().hasPermission(Permission.SeeOldRealization)) {
            viewModel.setCurrentDate()
            vBinding.salesSetDateBtn.isEnabled = false
            vBinding.salesDayBackBtn.isEnabled = false
            vBinding.salesDayForwardBtn.isEnabled = false
        }

        initViewModel()
        initBottomSheet()
        initExpenseFragment()
    }

    private fun initExpenseFragment() {
        val fr = ExpenseFragment().apply {
            onClose = {
                expenseBottomSheet.state = BottomSheetBehavior.STATE_HIDDEN
            }
            onTitleClick = {
                expenseBottomSheet.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
        childFragmentManager
            .beginTransaction()
            .replace(R.id.fragSalesExpenseContainer, fr)
            .commit()
    }

    private fun initBottomSheet() {
        expenseBottomSheet = BottomSheetBehavior.from(vBinding.fragSalesExpenseContainer)
        expenseBottomSheet.peekHeight = context?.getDimenPixelOffset(R.dimen.gr_size_56) ?: 0
        expenseBottomSheet.isHideable = true
        expenseBottomSheet.state = BottomSheetBehavior.STATE_HIDDEN

        vBinding.fragSalesExpenseUnit.setOnClickListener {
            if (expenseBottomSheet.state != BottomSheetBehavior.STATE_EXPANDED)
                expenseBottomSheet.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    fun initViewModel() {
        viewModel.salesLiveData.observe(viewLifecycleOwner, Observer {
            val adapter = SalesAdapter(context, it)
            vBinding.salesList1.adapter = adapter
            fillPageData()
        })
        viewModel.usersLiveData.observe(viewLifecycleOwner, Observer {
            viewModel.formUserMap(it)
        })
        viewModel.deleteExpenseLiveData.observe(viewLifecycleOwner, Observer {
            when (it) {
                is ApiResponseState.Success -> {
                    showToast(getString(R.string.msg_record_deleted))
                    fillPageData()
                }
                is ApiResponseState.ApiError -> {
                    showToast(getString(R.string.msg_record_not_deleted))
                }
                else -> {}
            }
            viewModel.deleteExpenseCompleted()
        })
        viewModel.addExpanseLiveData.observe(viewLifecycleOwner, Observer {
            when (it) {
                is ApiResponseState.Success -> {
                    fillPageData()
                    showToast(getString(R.string.msg_record_added))
                }
                is ApiResponseState.ApiError -> {
                    showToast(getString(R.string.msg_record_not_added))
                }
                else -> {}
            }
            viewModel.addExpenseCompleted()
        })
        viewModel.barrelsLiveData.observe(viewLifecycleOwner, Observer {
            initBarrelBlock(it)
        })
        viewModel.selectedDayLiveData.observe(viewLifecycleOwner, Observer {
            vBinding.salesDayForwardBtn.isEnabled = !viewModel.isToday()
        })
    }

    private fun fillPageData() {
        val expenseSum = viewModel.expenses.sumByDouble { it.amount.toDouble() }
        val atHand = viewModel.getCashAmount() - expenseSum
        val transferAmount = viewModel.getTransferAmount()
        val frictionSize = resources.getDimensionPixelSize(R.dimen.sp14)
        vBinding.salesSumPrice.text = resources.getString(R.string.format_gel, viewModel.priceSum).setFrictionSize(frictionSize)
        vBinding.salesSumXarji.text = resources.getString(R.string.format_gel, expenseSum).setFrictionSize(frictionSize)
        vBinding.salesAmountAtHand.text = resources.getString(R.string.format_gel, atHand).setFrictionSize(frictionSize)
        vBinding.salesTakenAmount.text = resources.getString(R.string.format_gel, viewModel.getCashAmount()).setFrictionSize(frictionSize)
        vBinding.salesTakenTransferAmount.text = resources.getString(R.string.format_gel, transferAmount).setFrictionSize(frictionSize)
    }

    private fun initBarrelBlock(data: List<BarrelIO>) {
        vBinding.salesBarrelRecycler.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )
        vBinding.salesBarrelRecycler.adapter = BarrelsIOAdapter(data)
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        viewModel.selectedDistributorID = viewModel.usersList[position].id.toInt()
        viewModel.prepareData()
    }
}