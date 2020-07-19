package com.example.beerdistrkt.fragPages.orders

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.beerdistrkt.BaseFragment
import com.example.beerdistrkt.R
import com.example.beerdistrkt.databinding.OrdersFragmentBinding
import com.example.beerdistrkt.fragPages.orders.adapter.OrderAdapter
import com.example.beerdistrkt.fragPages.orders.adapter.ParentOrderAdapter
import com.example.beerdistrkt.getViewModel
import com.example.beerdistrkt.models.OrderStatus
import com.example.beerdistrkt.showAskingDialog
import com.example.beerdistrkt.utils.ADD_ORDER
import com.example.beerdistrkt.utils.ApiResponseState
import com.example.beerdistrkt.utils.visibleIf
import kotlinx.android.synthetic.main.orders_fragment.*
import java.util.*

class OrdersFragment : BaseFragment<OrdersViewModel>() {

    companion object {
        fun newInstance() = OrdersFragment()
    }

    override val viewModel: OrdersViewModel by lazy { getViewModel<OrdersViewModel>() }

    private lateinit var vBinding: OrdersFragmentBinding
    private lateinit var ordersAdapter: ParentOrderAdapter

    private var dateSetListener = OnDateSetListener { _, year, month, day ->
        viewModel.onDateSelected(year, month, day)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        vBinding = OrdersFragmentBinding.inflate(inflater)
        vBinding.lifecycleOwner = this
        vBinding.viewModel = viewModel
        vBinding.addOrderBtn.setOnClickListener {
            it.findNavController().navigate(
                OrdersFragmentDirections.actionOrdersFragmentToObjListFragment(
                    ADD_ORDER
                )
            )
        }

        vBinding.ordersRecycler.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
//        vBinding.ordersRecycler.adapter = ordersAdapter

        /*val beerList: ArrayList<String> = ArrayList<String>()
        beerList.add("-")
        beerList.add("ertI")
        beerList.add("oRi")
        beerList.add("Sami 3")
        beerList.add("-")

        vBinding.testRecycler.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        vBinding.testRecycler.adapter = BeerAdapter(beerList)
        val snapHelper: SnapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(vBinding.testRecycler)

        vBinding.testRecycler.scrollToPosition(0)

        vBinding.testRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
            }
        })*/


        vBinding.setDateBtn.setOnClickListener {
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                dateSetListener,
                viewModel.orderDateCalendar.get(Calendar.YEAR),
                viewModel.orderDateCalendar.get(Calendar.MONTH),
                viewModel.orderDateCalendar.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.setCancelable(false)
            datePickerDialog.show()
//            val view = snapHelper.findSnapView(vBinding.testRecycler.layoutManager)
//            view?.let {
//                val tv = it.findViewById<TextView>(R.id.tvBeerName)
//                tv?.text =
//                    "oorraaaaA" + vBinding.testRecycler.getChildAdapterPosition(it).toString()
//
//                val layoutManager = vBinding.testRecycler.layoutManager
//                val snapView = snapHelper.findSnapView(layoutManager)
//                snapHelper.calculateDistanceToFinalSnap(layoutManager!!, it)
//                Log.d("testPos", layoutManager.getPosition(it).toString())
//            }

        }


        return vBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.ordersLiveData.observe(viewLifecycleOwner, Observer {
            when (it) {
                is ApiResponseState.Success -> {
                    ordersAdapter = ParentOrderAdapter(it.data)
                    ordersAdapter.onOrderDrag = viewModel::onOrderDrag
                    vBinding.ordersRecycler.adapter = ordersAdapter
                }
                is ApiResponseState.Loading -> orderLoaderBar.visibleIf(it.showLoading)
            }
        })
        viewModel.orderDayLiveData.observe(viewLifecycleOwner, Observer {
            vBinding.setDateBtn.text = it
        })
        viewModel.askForOrderDeleteLiveData.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                context?.showAskingDialog(
                    null,
                    R.string.confirm_delete_order,
                    R.string.yes,
                    R.string.no, R.style.ThemeOverlay_MaterialComponents_Dialog
                ) {
                    viewModel.deleteOrder(it)
                }
                viewModel.askForOrderDeleteLiveData.value = null
            }
        })
        viewModel.orderDeleteLiveData.observe(viewLifecycleOwner, Observer {
            when (it) {
                is ApiResponseState.Success -> {
                    showToast(R.string.msg_record_deleted)
                    ordersAdapter.removeItem(it.data)
                    viewModel.orderDeleteLiveData.value = ApiResponseState.Sleep
                }
            }
        })
        viewModel.editOrderLiveData.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                if (it.orderStatus == OrderStatus.ACTIVE) {
                    val actionOrderEdit =
                        OrdersFragmentDirections.actionOrdersFragmentToAddOrdersFragment(it.clientID)
                    actionOrderEdit.orderID = it.ID
                    vBinding.root.findNavController().navigate(actionOrderEdit)
                } else {
                    showToast(R.string.cannot_edit)
                }
                viewModel.editOrderLiveData.value = null
            }
        })
        (activity as AppCompatActivity).supportActionBar?.title = resources.getString(R.string.order_main)
    }

    override fun onResume() {
        super.onResume()
        viewModel.getOrders()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("_KA", "onViewCreated")
    }
}
