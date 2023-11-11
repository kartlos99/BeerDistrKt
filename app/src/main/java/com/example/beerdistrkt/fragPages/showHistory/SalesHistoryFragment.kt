package com.example.beerdistrkt.fragPages.showHistory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.beerdistrkt.BaseFragment
import com.example.beerdistrkt.R
import com.example.beerdistrkt.adapters.SimpleListAdapter
import com.example.beerdistrkt.databinding.FragmentSalesHistoryBinding
import com.example.beerdistrkt.databinding.ViewMoneyHistoryItemBinding
import com.example.beerdistrkt.databinding.ViewSalesHistoryItemBinding
import com.example.beerdistrkt.fragPages.sawyobi.models.SimpleBeerRowModel
import com.example.beerdistrkt.getViewModel
import com.example.beerdistrkt.utils.ApiResponseState
import com.example.beerdistrkt.utils.visibleIf

class SalesHistoryFragment: BaseFragment<SalesHistoryViewModel>() {

    private val frBinding by viewBinding(FragmentSalesHistoryBinding::bind)

    override val viewModel by lazy {
        getViewModel { SalesHistoryViewModel() }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sales_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewModel()
        setPageTitle(R.string.history)
        arguments?.let {
            when (it.getString(KEY_HISTORY_OF)) {
                delivery -> {
                    viewModel.getData(it.getInt(KEY_RECORD_ID, 0))
                    setPageTitle(R.string.sale_history_title)
                }
                money -> {
                    viewModel.getMoneyData(it.getInt(KEY_RECORD_ID, 0))
                    setPageTitle(R.string.money_history_title)
                }
            }
        }
    }

    private fun initViewModel() {
        viewModel.saleHistoryLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is ApiResponseState.Loading -> frBinding.fragSalesHistoryLoader.visibleIf(it.showLoading)
                is ApiResponseState.Success -> showHistory(it.data)
                else -> showToast(R.string.server_error)
            }
        }
        viewModel.moneyHistoryLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is ApiResponseState.Loading -> frBinding.fragSalesHistoryLoader.visibleIf(it.showLoading)
                is ApiResponseState.Success -> showMoneyHistory(it.data)
                else -> showToast(R.string.server_error)
            }
        }
    }

    private fun showMoneyHistory(data: List<MoneyHistory>) {
        if (data.isEmpty()) {
            showToast(getString(R.string.history_not_found))
            return
        }
        val obj = data[0]
        frBinding.fragSalesHistoryBaseInfo.text = StringBuilder()
            .append("ობიექტი: ")
            .append(obj.client.dasaxeleba)
            .append("\nდისტრიბუტორი: ")
            .append(obj.distributor.username)

        frBinding.fragSalesHistoryRc.layoutManager = LinearLayoutManager(context)
        val adapter = SimpleListAdapter(
            data,
            null,
            R.layout.view_money_history_item,
            onBind = {item, view ->
                val moneyHistoryBinding = ViewMoneyHistoryItemBinding.bind(view)
                moneyHistoryBinding.viewMoneyHistoryOperator.text = item.modifyUser.username
                moneyHistoryBinding.viewMoneyHistoryModifyDate.text = item.modifyDate
                moneyHistoryBinding.viewMoneyHistoryDate.text = getString(R.string.date_field, item.operationDate.split(" ")[0])
                moneyHistoryBinding.viewMoneyHistoryAmount.text = getString(R.string.amount_is, item.moneyAmount)
                moneyHistoryBinding.viewMoneyHistoryComment.visibleIf(!item.comment.isNullOrBlank())
                moneyHistoryBinding.viewMoneyHistoryComment.text = item.comment
                moneyHistoryBinding.viewMoneyHistoryPaymentType.setImageResource(item.paymentType.iconRes)
            }
        )
        frBinding.fragSalesHistoryRc.adapter = adapter
    }

    private fun showHistory(data: List<SaleHistory>) {
        if (data.isEmpty()) {
            showToast(getString(R.string.history_not_found))
            return
        }
        val obj = data[0]
        frBinding.fragSalesHistoryBaseInfo.text = StringBuilder()
            .append("ობიექტი: ")
            .append(obj.client.dasaxeleba)
            .append("\nდისტრიბუტორი: ")
            .append(obj.distributor.username)

        frBinding.fragSalesHistoryRc.layoutManager = LinearLayoutManager(context)
        val adapter = SimpleListAdapter(
            data,
            null,
            R.layout.view_sales_history_item,
            onBind = {item, view ->
                val itemBinding = ViewSalesHistoryItemBinding.bind(view)
                itemBinding.viewSaleHistoryOperator.text = item.modifyUser.username
                itemBinding.viewSaleHistoryModifyDate.text = item.modifyDate
                itemBinding.viewSaleHistoryDate.text = getString(R.string.date_field, item.saleDate.split(" ")[0])
                itemBinding.viewSaleHistoryPrice.text = getString(R.string.price_field, item.unitPrice)
                itemBinding.viewSaleHistoryComment.visibleIf(!item.comment.isNullOrBlank())
                itemBinding.viewSaleHistoryComment.text = item.comment
                itemBinding.viewSaleHistoryBeerRow.setData(SimpleBeerRowModel(
                    item.beer.dasaxeleba ?: "", mapOf(item.canTypeID to item.count), null, null, item.beer.displayColor
                ))
            }
        )
        frBinding.fragSalesHistoryRc.adapter = adapter
    }

    companion object {
        const val KEY_RECORD_ID = "record_id"
        const val KEY_HISTORY_OF = "KEY_HISTORY_OF"

        const val money = "money"
        const val delivery = "delivery"
    }
}

