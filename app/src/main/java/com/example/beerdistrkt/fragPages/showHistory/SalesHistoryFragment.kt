package com.example.beerdistrkt.fragPages.showHistory

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.beerdistrkt.BaseFragment
import com.example.beerdistrkt.R
import com.example.beerdistrkt.adapters.SimpleListAdapter
import com.example.beerdistrkt.databinding.FragmentSalesHistoryBinding
import com.example.beerdistrkt.databinding.ViewBottleSalesHistoryItemBinding
import com.example.beerdistrkt.databinding.ViewMoneyHistoryItemBinding
import com.example.beerdistrkt.databinding.ViewSalesHistoryItemBinding
import com.example.beerdistrkt.fragPages.sawyobi.models.SimpleBeerRowModel
import com.example.beerdistrkt.fragPages.sawyobi.models.SimpleBottleRowModel
import com.example.beerdistrkt.paramViewModels
import com.example.beerdistrkt.utils.ApiResponseState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SalesHistoryFragment : BaseFragment<SalesHistoryViewModel>() {

    private val binding by viewBinding(FragmentSalesHistoryBinding::bind)

    private val historyOf: String by lazy {
        SalesHistoryFragmentArgs.fromBundle(requireArguments()).historyOf
    }
    private val recordID: Int by lazy {
        SalesHistoryFragmentArgs.fromBundle(requireArguments()).recordID
    }

    override val viewModel: SalesHistoryViewModel
            by paramViewModels<SalesHistoryViewModel, SalesHistoryViewModel.Factory> { factory ->
                factory.create(recordID, historyOf)
            }

    override var frLayout: Int? = R.layout.fragment_sales_history

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewModel()

        setPageTitle(when(viewModel.historyOf) {
            BARREL_DELIVERY -> R.string.sale_history_title
            BOTTLE_DELIVERY -> R.string.sale_history_title
            MONEY -> R.string.money_history_title
            else -> R.string.ok
        })
    }

    private fun initViewModel() {
        viewModel.saleHistoryLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is ApiResponseState.Loading ->
                    binding.fragSalesHistoryLoader.isVisible = it.showLoading

                is ApiResponseState.Success -> showHistory(it.data)
                else -> showToast(R.string.server_error)
            }
        }
        viewModel.bottleSaleHistoryLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is ApiResponseState.Loading -> binding.fragSalesHistoryLoader.isVisible =
                    it.showLoading

                is ApiResponseState.Success -> showBottleHistory(it.data)
                else -> showToast(R.string.server_error)
            }
        }
        viewModel.moneyHistoryLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is ApiResponseState.Loading -> binding.fragSalesHistoryLoader.isVisible =
                    it.showLoading

                is ApiResponseState.Success -> showMoneyHistory(it.data)
                else -> showToast(R.string.server_error)
            }
        }
    }

    private fun showBottleHistory(historyList: List<BottleSaleHistory>) {
        if (historyList.isEmpty()) {
            showToast(getString(R.string.history_not_found))
            return
        }
        binding.fragSalesHistoryBaseInfo.text = StringBuilder()
            .append("ობიექტი: ")
            .append(historyList.first().client.name)
            .append("\nდისტრიბუტორი: ")
            .append(historyList.first().distributor.username)

        binding.fragSalesHistoryRc.layoutManager = LinearLayoutManager(context)
        binding.fragSalesHistoryRc.adapter = SimpleListAdapter(
            historyList,
            null,
            R.layout.view_bottle_sales_history_item,
            onBind = { item, view ->
                val itemBinding = ViewBottleSalesHistoryItemBinding.bind(view)
                itemBinding.viewSaleHistoryOperator.text = item.modifyUser.username
                itemBinding.viewSaleHistoryModifyDate.text = item.modifyDate
                itemBinding.viewSaleHistoryDate.text =
                    getString(R.string.date_field, item.saleDate.split(" ")[0])
                itemBinding.viewSaleHistoryPrice.text =
                    getString(R.string.price_field, item.unitPrice)
                itemBinding.viewSaleHistoryComment.isVisible = !item.comment.isNullOrBlank()
                itemBinding.viewSaleHistoryComment.text = item.comment
                itemBinding.viewSaleHistoryBottleRow.fillData(
                    SimpleBottleRowModel(
                        item.bottle.name,
                        item.count
                    )
                )
            }
        )
    }

    private fun showMoneyHistory(data: List<MoneyHistory>) {
        if (data.isEmpty()) {
            showToast(getString(R.string.history_not_found))
            return
        }
        val moneyHistory = data[0]
        binding.fragSalesHistoryBaseInfo.text = StringBuilder()
            .append("ობიექტი: ")
            .append(moneyHistory.client.name)
            .append("\nდისტრიბუტორი: ")
            .append(moneyHistory.distributor.username)

        binding.fragSalesHistoryRc.layoutManager = LinearLayoutManager(context)
        val adapter = SimpleListAdapter(
            data,
            null,
            R.layout.view_money_history_item,
            onBind = { item, view ->
                val moneyHistoryBinding = ViewMoneyHistoryItemBinding.bind(view)
                moneyHistoryBinding.viewMoneyHistoryOperator.text = item.modifyUser.username
                moneyHistoryBinding.viewMoneyHistoryModifyDate.text = item.modifyDate
                moneyHistoryBinding.viewMoneyHistoryDate.text =
                    getString(R.string.date_field, item.operationDate.split(" ")[0])
                moneyHistoryBinding.viewMoneyHistoryAmount.text =
                    getString(R.string.amount_is, item.moneyAmount)
                moneyHistoryBinding.viewMoneyHistoryComment.isVisible =
                    !item.comment.isNullOrBlank()
                moneyHistoryBinding.viewMoneyHistoryComment.text = item.comment
                moneyHistoryBinding.viewMoneyHistoryPaymentType.setImageResource(item.paymentType.iconRes)
            }
        )
        binding.fragSalesHistoryRc.adapter = adapter
    }

    private fun showHistory(data: List<SaleHistory>) {
        if (data.isEmpty()) {
            showToast(getString(R.string.history_not_found))
            return
        }
        val obj = data[0]
        binding.fragSalesHistoryBaseInfo.text = StringBuilder()
            .append("ობიექტი: ")
            .append(obj.client.name)
            .append("\nდისტრიბუტორი: ")
            .append(obj.distributor.username)

        binding.fragSalesHistoryRc.layoutManager = LinearLayoutManager(context)
        val adapter = SimpleListAdapter(
            data,
            null,
            R.layout.view_sales_history_item,
            onBind = { item, view ->
                val itemBinding = ViewSalesHistoryItemBinding.bind(view)
                itemBinding.viewSaleHistoryOperator.text = item.modifyUser.username
                itemBinding.viewSaleHistoryModifyDate.text = item.modifyDate
                itemBinding.viewSaleHistoryDate.text =
                    getString(R.string.date_field, item.saleDate.split(" ")[0])
                itemBinding.viewSaleHistoryPrice.text =
                    getString(R.string.price_field, item.unitPrice)
                itemBinding.viewSaleHistoryComment.isVisible = !item.comment.isNullOrBlank()
                itemBinding.viewSaleHistoryComment.text = item.comment
                itemBinding.viewSaleHistoryBeerRow.setData(
                    SimpleBeerRowModel(
                        item.beer.name,
                        mapOf(item.canTypeID to item.count),
                        null,
                        null,
                        item.beer.displayColor
                    )
                )
            }
        )
        binding.fragSalesHistoryRc.adapter = adapter
    }

    companion object {
        const val MONEY = "money"
        const val BARREL_DELIVERY = "delivery"
        const val BOTTLE_DELIVERY = "BOTTLE_DELIVERY"
    }
}

