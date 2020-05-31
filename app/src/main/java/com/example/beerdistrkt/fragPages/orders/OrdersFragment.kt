package com.example.beerdistrkt.fragPages.orders

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.beerdistrkt.BaseFragment
import com.example.beerdistrkt.databinding.OrdersFragmentBinding
import com.example.beerdistrkt.fragPages.orders.adapter.OrderAdapter
import com.example.beerdistrkt.getViewModel
import com.example.beerdistrkt.utils.ADD_ORDER

class OrdersFragment : BaseFragment<OrdersViewModel>() {

    companion object {
        fun newInstance() = OrdersFragment()
    }

    override val viewModel: OrdersViewModel by lazy { getViewModel<OrdersViewModel>() }

    private lateinit var vBinding: OrdersFragmentBinding
    private val ordersAdapter by lazy { OrderAdapter() }

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

        vBinding.ordersRecycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        vBinding.ordersRecycler.adapter = ordersAdapter

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
            ordersAdapter.setData(it)
        })
        Log.d("_KA", "onActivityCreated")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("_KA", "onViewCreated")
    }
}
