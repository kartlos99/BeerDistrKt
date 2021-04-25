package com.example.beerdistrkt.fragPages.showHistory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.beerdistrkt.BaseFragment
import com.example.beerdistrkt.R
import com.example.beerdistrkt.adapters.SimpleListAdapter
import com.example.beerdistrkt.fragPages.sawyobi.models.SimpleBeerRowModel
import com.example.beerdistrkt.getViewModel
import com.example.beerdistrkt.utils.ApiResponseState
import com.example.beerdistrkt.utils.visibleIf
import kotlinx.android.synthetic.main.fragment_sales_history.*
import com.example.beerdistrkt.observe
import kotlinx.android.synthetic.main.fragment_show_history.*
import kotlinx.android.synthetic.main.view_money_history_item.view.*
import kotlinx.android.synthetic.main.view_sales_history_item.view.*
import java.lang.StringBuilder

class SalesHistoryFragment: BaseFragment<SalesHistoryViewModel>() {
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
                is ApiResponseState.Loading -> fragSalesHistoryLoader.visibleIf(it.showLoading)
                is ApiResponseState.Success -> showHistory(it.data)
                else -> showToast(R.string.server_error)
            }
        }
        viewModel.moneyHistoryLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is ApiResponseState.Loading -> fragSalesHistoryLoader.visibleIf(it.showLoading)
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
        fragSalesHistoryBaseInfo.text = StringBuilder()
            .append("ობიექტი: ")
            .append(obj.client.dasaxeleba)
            .append("\nდისტრიბუტორი: ")
            .append(obj.distributor.username)

        fragSalesHistoryRc.layoutManager = LinearLayoutManager(context)
        val adapter = SimpleListAdapter(
            data,
            null,
            R.layout.view_money_history_item,
            onBind = {item, view ->
                view.viewMoneyHistoryOperator.text = item.modifyUser.username
                view.viewMoneyHistoryModifyDate.text = item.modifyDate
                view.viewMoneyHistoryDate.text = getString(R.string.date_field, item.operationDate.split(" ")[0])
                view.viewMoneyHistoryAmount.text = getString(R.string.amount_is, item.moneyAmount)
                view.viewMoneyHistoryComment.visibleIf(!item.comment.isNullOrBlank())
                view.viewMoneyHistoryComment.text = item.comment
                view.viewMoneyHistoryPaymentType.setImageResource(item.paymentType.iconRes)
            }
        )
        fragSalesHistoryRc.adapter = adapter
    }

    private fun showHistory(data: List<SaleHistory>) {
        if (data.isEmpty()) {
            showToast(getString(R.string.history_not_found))
            return
        }
        val obj = data[0]
        fragSalesHistoryBaseInfo.text = StringBuilder()
            .append("ობიექტი: ")
            .append(obj.client.dasaxeleba)
            .append("\nდისტრიბუტორი: ")
            .append(obj.distributor.username)

        fragSalesHistoryRc.layoutManager = LinearLayoutManager(context)
        val adapter = SimpleListAdapter(
            data,
            null,
            R.layout.view_sales_history_item,
            onBind = {item, view ->
                view.viewSaleHistoryOperator.text = item.modifyUser.username
                view.viewSaleHistoryModifyDate.text = item.modifyDate
                view.viewSaleHistoryDate.text = getString(R.string.date_field, item.saleDate.split(" ")[0])
                view.viewSaleHistoryPrice.text = getString(R.string.price_field, item.unitPrice.toString())
                view.viewSaleHistoryComment.visibleIf(!item.comment.isNullOrBlank())
                view.viewSaleHistoryComment.text = item.comment
                view.viewSaleHistoryBeerRow.setData(SimpleBeerRowModel(
                    item.beer.dasaxeleba ?: "", mapOf(item.canTypeID to item.count), null, null, item.beer.displayColor
                ))
            }
        )
        fragSalesHistoryRc.adapter = adapter
    }

    companion object {
        const val KEY_RECORD_ID = "record_id"
        const val KEY_HISTORY_OF = "KEY_HISTORY_OF"

        const val money = "money"
        const val delivery = "delivery"
    }
}

