package com.example.beerdistrkt.fragPages.orders

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.os.Bundle
import android.view.*
import android.widget.RelativeLayout
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.beerdistrkt.*
import com.example.beerdistrkt.databinding.OrdersFragmentBinding
import com.example.beerdistrkt.fragPages.login.models.Permission
import com.example.beerdistrkt.fragPages.orders.adapter.ParentOrderAdapter
import com.example.beerdistrkt.models.OrderStatus
import com.example.beerdistrkt.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.util.*

@AndroidEntryPoint
class OrdersFragment : BaseFragment<OrdersViewModel>(), SwipeRefreshLayout.OnRefreshListener {

    companion object {
        fun newInstance() = OrdersFragment()
    }

    override val viewModel: OrdersViewModel by viewModels()

    private lateinit var vBinding: OrdersFragmentBinding
    private lateinit var ordersAdapter: ParentOrderAdapter
    private var orderListSize = 0
    private var switchToDelivery: SwitchCompat? = null

    private lateinit var searchView: SearchView
    private lateinit var searchItem: MenuItem

    private var dateSetListener = OnDateSetListener { _, year, month, day ->
        viewModel.onDateSelected(year, month, day)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        vBinding = OrdersFragmentBinding.inflate(inflater)
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
        return vBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewModel()
        setPageTitle(resources.getString(R.string.order_main))
        vBinding.initView()
    }

    private fun OrdersFragmentBinding.initView() {
        addOrderBtn.setOnClickListener {
            it.findNavController().navigate(
                OrdersFragmentDirections.actionOrdersFragmentToObjListFragment(ADD_ORDER)
            )
        }
        setDateBtn.setOnClickListener { showDateDialog() }
        ordersRecycler.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        ordersSwipeRefresh.setOnRefreshListener(this@OrdersFragment)
    }

    private fun showDateDialog() {
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            dateSetListener,
            viewModel.orderDateCalendar.get(Calendar.YEAR),
            viewModel.orderDateCalendar.get(Calendar.MONTH),
            viewModel.orderDateCalendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.setCancelable(false)
        datePickerDialog.show()
/*
            val view = snapHelper.findSnapView(vBinding.testRecycler.layoutManager)
            view?.let {
                val tv = it.findViewById<TextView>(R.id.tvBeerName)
                tv?.text =
                    "oorraaaaA" + vBinding.testRecycler.getChildAdapterPosition(it).toString()
                val layoutManager = vBinding.testRecycler.layoutManager
                val snapView = snapHelper.findSnapView(layoutManager)
                snapHelper.calculateDistanceToFinalSnap(layoutManager!!, it)
                Log.d("testPos", layoutManager.getPosition(it).toString())
            }
*/
    }

    private fun onModeChange(checked: Boolean) {
        viewModel.deliveryMode = checked
        setPageTitle(if (checked) resources.getString(R.string.delivery) else resources.getString(R.string.order_main))

        if ((vBinding.ordersRecycler.layoutManager?.itemCount ?: 0) > 0) {
            /*vBinding.ordersRecycler.layoutManager?.findViewByPosition(orderListSize)?.let {
                with(ViewOrderGroupBottomItemBinding.bind(it)) {
                    totalSummedOrderRecycler.visibleIf(!checked)
                    totalOrderTitle.visibleIf(!checked)
                }
            }*/
            ordersAdapter.updateLastItem(checked)
        }
        vBinding.ordersRecycler.setBackgroundColor(
            if (checked) resources.getColor(R.color.color_delivery_bkg) else resources.getColor(R.color.color_order_bkg)
        )
    }

    private fun initViewModel() {
        viewModel.ordersLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is ApiResponseState.Success -> {
                    orderListSize = it.data.size
                    ordersAdapter = ParentOrderAdapter(
                        it.data,
                        viewModel.barrels,
                        viewModel::saveDistributorGroupState
                    )
                    ordersAdapter.onOrderDrag = viewModel::onOrderDrag
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
        }
        viewModel.orderDayLiveData.observe(viewLifecycleOwner) {
            vBinding.setDateBtn.text = it
        }
        viewModel.askForOrderDeleteLiveData.observe(viewLifecycleOwner) {
            if (it != null) {
                if (viewModel.session.hasPermission(Permission.EditOrder)) {
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
        }
        viewModel.orderDeleteLiveData.observe(viewLifecycleOwner, Observer {
            when (it) {
                is ApiResponseState.Success -> {
                    showToast(R.string.msg_record_deleted)
                    ordersAdapter.removeItem(it.data)
                    viewModel.orderDeleteLiveData.value = ApiResponseState.Sleep
                }
                else -> {}
            }
        })
        viewModel.editOrderLiveData.observe(viewLifecycleOwner) {
            if (it != null) {
                if (it.orderStatus == OrderStatus.ACTIVE) {
                    if (viewModel.session.hasPermission(Permission.EditOrder)) {
                        vBinding.root.findNavController().navigate(
                            OrdersFragmentDirections.actionOrdersFragmentToAddOrdersFragment(
                                it.clientID,
                                it.ID
                            )
                        )
                    } else
                        showToast(R.string.no_permission_common)
                } else {
                    showToast(R.string.cannot_edit)
                }
                viewModel.editOrderLiveData.value = null
            }
        }
        viewModel.changeDistributorLiveData.observe(viewLifecycleOwner) { order ->
            if (order != null) {
                requireContext().showListDialog(
                    R.string.choose_distributor,
                    viewModel.getDistributorsArray(order.clientID)
                ) { distributorPos ->
                    viewModel.changeDistributor(order.ID, distributorPos)
                }
                viewModel.changeDistributorLiveData.value = null
            }
        }
        viewModel.onItemClickLiveData.observe(viewLifecycleOwner) { order ->
            if (order != null) {
                viewModel.onItemClickLiveData.value = null
                vBinding.root.findNavController().navigate(
                    OrdersFragmentDirections
                        .actionOrdersFragmentToAddDeliveryFragment(order.clientID, null, 0, 0)
                )
            }
        }
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


    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    @OptIn(FlowPreview::class)
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.order_option_menu, menu)

        val deliveryItem = menu.findItem(R.id.topBarDelivery)
        val swView = menu.findItem(R.id.appBarOrderSwitch).actionView as RelativeLayout
        switchToDelivery = swView.getChildAt(0) as SwitchCompat
        switchToDelivery?.setOnCheckedChangeListener { _, isChecked ->
            onModeChange(isChecked)
            deliveryItem.isVisible = isChecked
        }

        searchItem = menu.findItem(R.id.orderSearch)
        searchView = searchItem.actionView as SearchView

        val pendingQuery = viewModel.searchQuery.value
        if (pendingQuery != null && pendingQuery.isNotEmpty()) {
            searchItem.expandActionView()
            searchView.setQuery(pendingQuery, false)
            viewModel.filterOrders(pendingQuery)
        }
        searchView.changesAsFlow()
            .debounce(300)
            .onEach { query ->
                viewModel.searchQuery.value = query.orEmpty()
                viewModel.filterOrders(query.orEmpty())
            }
            .launchIn(lifecycleScope)
    }

    override fun onStop() {
        super.onStop()
        viewModel.saveFoldsState()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.topBarDelivery -> {
                vBinding.root.findNavController().navigate(
                    OrdersFragmentDirections.actionOrdersFragmentToObjListFragment(MITANA)
                )
                return true
            }
        }
        return false
    }

    override fun onRefresh() {
        viewModel.getOrders()
    }
}
