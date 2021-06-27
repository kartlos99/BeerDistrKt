package com.example.beerdistrkt.fragPages.orders

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.beerdistrkt.*
import com.example.beerdistrkt.databinding.OrdersFragmentBinding
import com.example.beerdistrkt.fragPages.login.models.Permission
import com.example.beerdistrkt.fragPages.orders.adapter.ParentOrderAdapter
import com.example.beerdistrkt.models.OrderStatus
import com.example.beerdistrkt.utils.*
import kotlinx.android.synthetic.main.view_order_group_bottom_item.view.*
import java.util.*

class OrdersFragment : BaseFragment<OrdersViewModel>(), SwipeRefreshLayout.OnRefreshListener {

    companion object {
        fun newInstance() = OrdersFragment()
    }

    override val viewModel: OrdersViewModel by lazy { getViewModel<OrdersViewModel>() }

    private lateinit var vBinding: OrdersFragmentBinding
    private lateinit var ordersAdapter: ParentOrderAdapter
    private var orderListSize = 0
    private var switchToDelivery: SwitchCompat? = null

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
                OrdersFragmentDirections.actionOrdersFragmentToObjListFragment(ADD_ORDER)
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

    private fun onModeChange(checked: Boolean) {
        viewModel.deliveryMode = checked
        (activity as AppCompatActivity).supportActionBar?.title =
            if (checked)
                resources.getString(R.string.delivery)
            else
                resources.getString(R.string.order_main)

        if (vBinding.ordersRecycler.layoutManager?.itemCount ?: 0 > 0) {
            vBinding.ordersRecycler.layoutManager?.findViewByPosition(orderListSize)?.let {
                it.addDeliveryBtn.visibleIf(checked)
                it.totalSummedOrderRecycler.visibleIf(!checked)
                it.totalOrderTitle.visibleIf(!checked)
            }
            ordersAdapter.updateLastItem(checked)
        }
        vBinding.orderRootConstraint.setBackgroundColor(
            if (checked) resources.getColor(R.color.colorAccent_33) else Color.WHITE
        )
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initViewModel()

        (activity as AppCompatActivity).supportActionBar?.title =
            resources.getString(R.string.order_main)
    }

    private fun initViewModel() {
        viewModel.ordersLiveData.observe(viewLifecycleOwner, Observer {
            when (it) {
                is ApiResponseState.Success -> {
                    orderListSize = it.data.size
                    ordersAdapter = ParentOrderAdapter(
                        it.data, viewModel.barrelsList,
                        viewModel::saveDistributorGroupState
                    )
                    ordersAdapter.onOrderDrag = viewModel::onOrderDrag
                    ordersAdapter.onMitanaClick = View.OnClickListener { view ->
                        view.findNavController().navigate(
                            OrdersFragmentDirections.actionOrdersFragmentToObjListFragment(
                                MITANA
                            )
                        )
                    }
                    vBinding.ordersRecycler.adapter = ordersAdapter
                    onModeChange(viewModel.deliveryMode)
                    switchToDelivery?.isChecked = viewModel.deliveryMode
                }
                is ApiResponseState.Loading -> {
                    vBinding.ordersSwipeRefresh.isRefreshing = it.showLoading
                }
                is ApiResponseState.ApiError -> showToast(it.errorText)
                else -> showToast(R.string.something_is_wrong)
            }
        })
        viewModel.orderDayLiveData.observe(viewLifecycleOwner, Observer {
            vBinding.setDateBtn.text = it
        })
        viewModel.askForOrderDeleteLiveData.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                if (Session.get().hasPermission(Permission.EditOrder)) {
                    context?.showAskingDialog(
                        null,
                        R.string.confirm_delete_order,
                        R.string.yes,
                        R.string.no, R.style.ThemeOverlay_MaterialComponents_Dialog
                    ) {
                        viewModel.deleteOrder(it)
                    }
                } else
                    showToast(R.string.no_permission_common)
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
                    if (Session.get().hasPermission(Permission.EditOrder)) {
                        val actionOrderEdit =
                            OrdersFragmentDirections.actionOrdersFragmentToAddOrdersFragment(it.clientID)
                        actionOrderEdit.orderID = it.ID
                        vBinding.root.findNavController().navigate(actionOrderEdit)
                    } else
                        showToast(R.string.no_permission_common)
                } else {
                    showToast(R.string.cannot_edit)
                }
                viewModel.editOrderLiveData.value = null
            }
        })
        viewModel.changeDistributorLiveData.observe(viewLifecycleOwner, Observer { order ->
            if (order != null) {
                requireContext().showListDialog(
                    R.string.choose_distributor,
                    viewModel.getDistributorsArray(order.clientID)
                ) { distributorPos ->
                    viewModel.changeDistributor(order.ID, distributorPos)
                }
                viewModel.changeDistributorLiveData.value = null
            }
        })
        viewModel.onItemClickLiveData.observe(viewLifecycleOwner, Observer { order ->
            if (order != null) {
                viewModel.onItemClickLiveData.value = null
                vBinding.root.findNavController().navigate(
                    OrdersFragmentDirections
                        .actionOrdersFragmentToAddDeliveryFragment(order.clientID, null)
                )
            }
        })
        viewModel.onShowHistoryLiveData.observeSingleEvent(viewLifecycleOwner) {
            view?.findNavController()?.navigate(
                OrdersFragmentDirections.actionOrdersFragmentToShowHistoryFragment(it)
            )
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getOrders()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vBinding.ordersSwipeRefresh.setOnRefreshListener(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.order_option_menu, menu)

        val swView = menu.findItem(R.id.appBarOrderSwitch).actionView as RelativeLayout
        switchToDelivery = swView.getChildAt(0) as SwitchCompat
        switchToDelivery?.setOnCheckedChangeListener { _, isChecked ->
            onModeChange(isChecked)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return false
    }

    override fun onRefresh() {
        viewModel.getOrders()

    }
}
