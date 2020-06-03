package com.example.beerdistrkt.fragPages.orders

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.beerdistrkt.BaseFragment

import com.example.beerdistrkt.R
import com.example.beerdistrkt.getSnapPosition
import com.example.beerdistrkt.getViewModel
import com.example.beerdistrkt.utils.OnSnapPositionChangeListener
import com.example.beerdistrkt.utils.SnapOnScrollListener
import com.example.beerdistrkt.utils.inflate
import com.tbuonomo.viewpagerdotsindicator.BaseDotsIndicator
import com.tbuonomo.viewpagerdotsindicator.OnPageChangeListenerHelper
import kotlinx.android.synthetic.main.add_orders_fragment.*
import kotlinx.android.synthetic.main.add_orders_fragment.view.*
import kotlinx.android.synthetic.main.beer_item_view.view.*

class AddOrdersFragment : BaseFragment<AddOrdersViewModel>() {

    companion object {
        fun newInstance() = AddOrdersFragment()
    }
    val beerList = listOf("ლაგერი", "გაუფილტრავი", "თაფილი")
    var beerPos = 0
    private val snapHelper = PagerSnapHelper()
    private val beerAdapter = BeerAdapter()

    private val clientID by lazy {
        val args = AddOrdersFragmentArgs.fromBundle(arguments ?: Bundle())
        args.clientObjectID
    }

    override val viewModel by lazy {
        getViewModel{ AddOrdersViewModel(clientID) }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val vv = inflater.inflate(R.layout.add_orders_fragment, container, false)
        val btnBeerLeft = vv.findViewById<ImageView>(R.id.btnBeerLeftImg)
        val btnBeerRight = vv.findViewById<ImageView>(R.id.btnBeerRightImg)
//        val tvtite = vv.findViewById<TextView>(R.id.t_ludisDasaxeleba)

        btnBeerLeft.setOnClickListener {
            beerPos = (snapHelper.getSnapPosition(addOrderBeerRecycler) + beerList.size - 1) % beerList.size
            addOrderBeerRecycler.smoothScrollToPosition(beerPos)
        }
        btnBeerRight.setOnClickListener {
            beerPos = (snapHelper.getSnapPosition(addOrderBeerRecycler) + 1) % beerList.size
            addOrderBeerRecycler.smoothScrollToPosition(beerPos)
        }
        vv.addOrderBeerRecycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        vv.addOrderBeerRecycler.adapter = beerAdapter
        snapHelper.attachToRecyclerView(vv.addOrderBeerRecycler)
        vv.addOrderBeerListIndicator.pager = getIndicatorPager(vv.addOrderBeerRecycler)
        return vv
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initViewModel()

    }

    private fun initViewModel(){
        viewModel.clientLiveData.observe(viewLifecycleOwner, Observer {
            addOrderClientInfo.text = "${it.obieqti.dasaxeleba} N:${it.obieqti.id ?: 0}"
        })
    }

    inner class BeerAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            BeerItemViewHolder(parent.inflate(R.layout.beer_item_view))

        override fun getItemCount() = beerList.size
//            viewModel.advertisements.value?.size ?: 0

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            holder.itemView.tBeerNameItm.text = beerList[position]
        }

    }

    inner class BeerItemViewHolder(view: View) : RecyclerView.ViewHolder(view)


    private fun getIndicatorPager(rv: RecyclerView): BaseDotsIndicator.Pager {
        return object : BaseDotsIndicator.Pager {
            var onPageChangeListener: SnapOnScrollListener? = null

            override val isNotEmpty: Boolean
                get() = beerAdapter.itemCount > 0
            override val currentItem: Int
                get() = snapHelper.getSnapPosition(rv)
            override
            val isEmpty: Boolean
                get() = beerAdapter.itemCount == 0
            override val count: Int
                get() = beerAdapter.itemCount

            override fun setCurrentItem(item: Int, smoothScroll: Boolean) {
                rv.smoothScrollToPosition(item)
            }

            override fun removeOnPageChangeListener() {
                onPageChangeListener?.let { rv.removeOnScrollListener(it) }
            }

            override fun addOnPageChangeListener(
                onPageChangeListenerHelper: OnPageChangeListenerHelper
            ) {
                onPageChangeListener = SnapOnScrollListener(
                    snapHelper,
                    SnapOnScrollListener.Behavior.NOTIFY_ON_SCROLL,
                    object : OnSnapPositionChangeListener {
                        override fun onSnapPositionChange(position: Int) {
                            onPageChangeListenerHelper.onPageScrolled(position, 0f)
                        }
                    }
                )
                rv.addOnScrollListener(onPageChangeListener!!)
            }
        }
    }
}
