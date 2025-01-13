package com.example.beerdistrkt.fragPages.realisationtotal

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.beerdistrkt.BaseFragment
import com.example.beerdistrkt.MainActivity
import com.example.beerdistrkt.R
import com.example.beerdistrkt.adapters.SalesAdapter
import com.example.beerdistrkt.collectLatest
import com.example.beerdistrkt.common.adapter.SimpleDataAdapter
import com.example.beerdistrkt.databinding.BottlesSaleRowBinding
import com.example.beerdistrkt.databinding.SalesFragmentBinding
import com.example.beerdistrkt.fragPages.login.models.Permission
import com.example.beerdistrkt.fragPages.realisationtotal.adapter.BarrelsIOAdapter
import com.example.beerdistrkt.fragPages.realisationtotal.domain.model.BottleSale
import com.example.beerdistrkt.fragPages.realisationtotal.domain.model.RealizationDay
import com.example.beerdistrkt.getDimenPixelOffset
import com.example.beerdistrkt.models.BarrelIO
import com.example.beerdistrkt.models.SaleInfo
import com.example.beerdistrkt.network.model.ResultState
import com.example.beerdistrkt.network.model.onError
import com.example.beerdistrkt.network.model.onSuccess
import com.example.beerdistrkt.orZero
import com.example.beerdistrkt.setAmount
import com.example.beerdistrkt.utils.Session
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar
import java.util.Date

@AndroidEntryPoint
class SalesFragment : BaseFragment<SalesViewModel>(), AdapterView.OnItemSelectedListener {

    private lateinit var vBinding: SalesFragmentBinding
    override val viewModel: SalesViewModel by viewModels()

    private lateinit var expenseBottomSheet: BottomSheetBehavior<*>
    private lateinit var bottleSalesAdapter: SimpleDataAdapter<BottleSale>

    private var dateSetListener = OnDateSetListener { _, year, month, day ->
        viewModel.onDataSelected(year, month, day)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        vBinding = SalesFragmentBinding.inflate(inflater)
        return vBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.formUsersList()

        initView()
        initViewModel()
        initBottomSheet()
        initExpenseFragment()
    }

    private fun initView() = with(vBinding) {
        salesSetDateBtn.setOnClickListener {
            DatePickerDialog(
                requireContext(),
                dateSetListener,
                viewModel.calendar.get(Calendar.YEAR),
                viewModel.calendar.get(Calendar.MONTH),
                viewModel.calendar.get(Calendar.DAY_OF_MONTH)
            ).apply {
                datePicker.maxDate = Date().time
                setCancelable(false)
            }
                .show()
        }
        salesDayBackBtn.setOnClickListener {
            viewModel.changeDay(-1)
        }
        salesDayForwardBtn.setOnClickListener {
            viewModel.changeDay(1)
        }
        salesDistributorsSpinner.adapter = ArrayAdapter(
            requireContext(),
            R.layout.simple_dropdown_item,
            viewModel.getDistributorNamesList()
        )
        salesDistributorsSpinner.onItemSelectedListener = this@SalesFragment

        if (!Session.get().hasPermission(Permission.SeeOthersRealization)) {
            val currentUserIndexInSpinner =
                viewModel.visibleDistributors.map { it.id }.indexOf(Session.get().userID)
            if (currentUserIndexInSpinner >= 0) {
                salesDistributorsSpinner.setSelection(currentUserIndexInSpinner)
            } else {
                viewModel.forceLogout()
            }
            salesDistributorsSpinner.isEnabled = false
        }
        if (!Session.get().hasPermission(Permission.SeeOldRealization)) {
            viewModel.setCurrentDate()
            salesSetDateBtn.isEnabled = false
            salesDayBackBtn.isEnabled = false
            salesDayForwardBtn.isEnabled = false
        }
        setupBottleSalesAdapter()
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

    private fun fillBarrelSaleInfo(data: List<SaleInfo>) {
        vBinding.salesList1.adapter = SalesAdapter(context, data)
    }

    fun initViewModel() = with(viewModel) {
        usersLiveData.observe(viewLifecycleOwner) {
            formUserMap(it)
        }
        selectedDayLiveData.observe(viewLifecycleOwner) { dateString ->
            vBinding.salesDayForwardBtn.isEnabled = !isToday()
            vBinding.salesSetDateBtn.text = dateString
        }
        dayStateFlow.collectLatest(viewLifecycleOwner) { result ->
            vBinding.progressIndicator.isVisible = result is ResultState.Loading
            result
                .onSuccess {
                    fillPageData(it)
                }.onError { error ->
                    fillPageData(null)
                    showToast(error.message)
                }
        }
    }

    private fun setupBottleSalesAdapter() = with(vBinding) {
        bottleSalesAdapter = SimpleDataAdapter(
            layoutId = R.layout.bottles_sale_row,
            onBind = { item, view ->
                with(BottlesSaleRowBinding.bind(view)) {
                    bottleName.text = item.name
                    count.text = getString(R.string.format_count, item.count.toString())
                    price.setAmount(item.price)
                }
            }
        )
        bottlesRecycler.adapter = bottleSalesAdapter
        bottlesRecycler.layoutManager = LinearLayoutManager(context)
    }

    private fun fillPageData(dayInfo: RealizationDay?) = with(vBinding) {
        val expenseSumValue = dayInfo?.getTotalExpense().orZero()
        val atHand = dayInfo?.getCashAmount().orZero() - expenseSumValue
        salesSumPrice.setAmount(dayInfo?.getTotalPrice().orZero())
        salesTakenAmount.setAmount(dayInfo?.getCashAmount().orZero())
        salesTakenTransferAmount.setAmount(dayInfo?.getTransferAmount().orZero())
        expenseSum.setAmount(expenseSumValue)
        salesAmountAtHand.setAmount(atHand)

        fillBarrelSaleInfo(dayInfo?.sale.orEmpty())
        initBarrelBlock(dayInfo?.barrels.orEmpty())
        bottleSalesAdapter.submitList(dayInfo?.bottleSale)
    }

    private fun initBarrelBlock(data: List<BarrelIO>) {
        vBinding.salesBarrelRecycler.layoutManager = LinearLayoutManager(requireContext())
        vBinding.salesBarrelRecycler.adapter = BarrelsIOAdapter(data)
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        viewModel.selectedDistributorID = viewModel.visibleDistributors[position].id.toInt()
        viewModel.prepareData()
    }

    companion object {
        const val TAG = "Sales Frag"
    }
}