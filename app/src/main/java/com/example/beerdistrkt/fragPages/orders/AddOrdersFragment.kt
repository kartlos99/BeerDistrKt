package com.example.beerdistrkt.fragPages.orders

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView

import com.example.beerdistrkt.R
import com.example.beerdistrkt.animateThis
import com.example.beerdistrkt.getSnapPosition
import com.example.beerdistrkt.utils.OnSnapPositionChangeListener
import com.example.beerdistrkt.utils.SnapOnScrollListener
import com.example.beerdistrkt.utils.inflate
import com.tbuonomo.viewpagerdotsindicator.BaseDotsIndicator
import com.tbuonomo.viewpagerdotsindicator.OnPageChangeListenerHelper
import kotlinx.android.synthetic.main.add_orders_fragment.*
import kotlinx.android.synthetic.main.add_orders_fragment.view.*
import kotlinx.android.synthetic.main.beer_item_view.view.*
import kotlinx.android.synthetic.main.orders_fragment.*

class AddOrdersFragment : Fragment() {

    companion object {
        fun newInstance() = AddOrdersFragment()
    }
    val beerList = listOf("ლაგერი", "გაუფილტრავი", "თაფილი")
    var beerPos = 0
    private val snapHelper = PagerSnapHelper()
    private val beerAdapter = BeerAdapter()

    private lateinit var viewModel: AddOrdersViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {




        val vv = inflater.inflate(R.layout.add_orders_fragment, container, false)
        val btnBeerLeft = vv.findViewById<ImageView>(R.id.btnBeerLeftImg)
        val btnBeerRight = vv.findViewById<ImageView>(R.id.btnBeerRightImg)
//        val tvtite = vv.findViewById<TextView>(R.id.t_ludisDasaxeleba)

        btnBeerLeft.setOnClickListener {
            beerPos = (snapHelper.getSnapPosition(beerRecycler) + beerList.size - 1) % beerList.size
            beerRecycler.smoothScrollToPosition(beerPos)
        }
        btnBeerRight.setOnClickListener {
            beerPos = (snapHelper.getSnapPosition(beerRecycler) + 1) % beerList.size
            beerRecycler.smoothScrollToPosition(beerPos)
        }
        vv.beerRecycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        vv.beerRecycler.adapter = beerAdapter
        snapHelper.attachToRecyclerView(vv.beerRecycler)
        vv.beerListIndicatior.pager = getIndicatorPager(vv.beerRecycler)
        return vv
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(AddOrdersViewModel::class.java)

        val argsBundle = arguments ?: Bundle()
        val args = AddOrdersFragmentArgs.fromBundle(argsBundle)
        Log.d("arg", "objID ${args.clientObjectID}")
        viewModel.logObjPr(args.clientObjectID)


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
