package com.example.beerdistrkt.fragPages.orders

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.beerdistrkt.BaseFragment
import com.example.beerdistrkt.R
import com.example.beerdistrkt.customView.TempBeerRowView
import com.example.beerdistrkt.databinding.AddOrdersFragmentBinding
import com.example.beerdistrkt.getSnapPosition
import com.example.beerdistrkt.getViewModel
import com.example.beerdistrkt.models.TempOrderItemModel
import com.example.beerdistrkt.utils.OnSnapPositionChangeListener
import com.example.beerdistrkt.utils.SnapOnScrollListener
import com.example.beerdistrkt.utils.inflate
import com.google.android.material.chip.Chip
import com.tbuonomo.viewpagerdotsindicator.BaseDotsIndicator
import com.tbuonomo.viewpagerdotsindicator.OnPageChangeListenerHelper
import kotlinx.android.synthetic.main.add_orders_fragment.*
import kotlinx.android.synthetic.main.beer_item_view.view.*
import kotlinx.android.synthetic.main.numeric_edittext_view.view.*

class AddOrdersFragment : BaseFragment<AddOrdersViewModel>(), View.OnClickListener {

    companion object {
        fun newInstance() = AddOrdersFragment()
    }

    override val viewModel by lazy {
        getViewModel { AddOrdersViewModel(clientID) }
    }

    private var beerPos = 0
    private val snapHelper = PagerSnapHelper()
    private val beerAdapter = BeerAdapter()

    private lateinit var vBinding: AddOrdersFragmentBinding

    private val clientID by lazy {
        val args = AddOrdersFragmentArgs.fromBundle(arguments ?: Bundle())
        args.clientObjectID
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        vBinding = AddOrdersFragmentBinding.inflate(inflater)
        vBinding.lifecycleOwner = this

        initBeerRecycler()
        return vBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewModel()

        vBinding.btnBeerLeftImg.setOnClickListener {
            beerPos =
                (snapHelper.getSnapPosition(vBinding.addOrderBeerRecycler) + viewModel.beerList.size - 1) % viewModel.beerList.size
            vBinding.addOrderBeerRecycler.smoothScrollToPosition(beerPos)
        }
        vBinding.btnBeerRightImg.setOnClickListener {
            beerPos =
                (snapHelper.getSnapPosition(vBinding.addOrderBeerRecycler) + 1) % viewModel.beerList.size
            vBinding.addOrderBeerRecycler.smoothScrollToPosition(beerPos)
        }
        vBinding.addOrderAddItemBtn.setOnClickListener {
            if (isFormValid()) {
                viewModel.addOrderItemToList(getTempOrderItem())
                resetForm()
            } else
                showToast("no data")
        }
        vBinding.addOrdersCanChip0.setOnClickListener(this)
        vBinding.addOrdersCanChip1.setOnClickListener(this)
        vBinding.addOrdersCanChip2.setOnClickListener(this)
        vBinding.addOrdersCanChip3.setOnClickListener(this)

        vBinding.addOrderCanCountControl.editCount.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkForm()
            }

        })
    }

    fun getTempOrderItem(): TempOrderItemModel {
        return TempOrderItemModel(
            viewModel.beerList[beerPos],
            viewModel.selectedCan!!,
            vBinding.addOrderCanCountControl.amount,
            {
                showToast("del")
                viewModel.removeOrderItemFromList(it)
            })
    }

    fun isFormValid(): Boolean {
        return vBinding.addOrderCanCountControl.amount > 0 && viewModel.selectedCan != null
    }

    fun resetForm() {
        vBinding.addOrdersChipGr.clearCheck()
        viewModel.selectedCan = null
        vBinding.addOrderCanCountControl.amount = 0
    }

    fun checkForm() {
        vBinding.addOrderAddItemBtn.backgroundTintList = if (isFormValid())
            ColorStateList.valueOf(Color.GREEN)
        else
            ColorStateList.valueOf(Color.RED)
    }

    private fun initViewModel() {
        viewModel.clientLiveData.observe(viewLifecycleOwner, Observer {
            addOrderClientInfo.text = "${it.obieqti.dasaxeleba} N:${it.obieqti.id ?: 0}"
        })
        viewModel.orderItemsLiveData.observe(viewLifecycleOwner, Observer{
            addOrderItemsContainer.removeAllViews()
            it.forEach {orderItem ->
                addOrderItemsContainer.addView(TempBeerRowView(context = requireContext(), rowData = orderItem))
            }
        })
    }

    private fun initBeerRecycler() {
        vBinding.addOrderBeerRecycler.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        vBinding.addOrderBeerRecycler.adapter = beerAdapter
        snapHelper.attachToRecyclerView(vBinding.addOrderBeerRecycler)
        vBinding.addOrderBeerListIndicator.pager = getIndicatorPager(vBinding.addOrderBeerRecycler)
    }

    private fun onStopScroll(pos: Int) {
        beerPos = pos
    }

    inner class BeerAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            BeerItemViewHolder(parent.inflate(R.layout.beer_item_view))

        override fun getItemCount() = viewModel.beerList.size

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            holder.itemView.tBeerNameItm.text = viewModel.beerList[position].dasaxeleba
        }
    }

    inner class BeerItemViewHolder(view: View) : RecyclerView.ViewHolder(view)


    private fun getIndicatorPager(rv: RecyclerView): BaseDotsIndicator.Pager {
        return object : BaseDotsIndicator.Pager {
            var onPageChangeListener: SnapOnScrollListener? = null

            override val isNotEmpty: Boolean
                get() = rv.adapter?.itemCount ?: 0 > 0
            override val currentItem: Int
                get() = snapHelper.getSnapPosition(rv)
            override val isEmpty: Boolean
                get() = rv.adapter?.itemCount == 0
            override val count: Int
                get() = rv.adapter?.itemCount ?: 0

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
                    SnapOnScrollListener.Behavior.NOTIFY_ON_SCROLL_STATE_IDLE,
                    object : OnSnapPositionChangeListener {
                        override fun onSnapPositionChange(position: Int) {
                            onPageChangeListenerHelper.onPageScrolled(position, 0f)
                            onStopScroll(position)
                        }
                    }
                )
                rv.addOnScrollListener(onPageChangeListener!!)
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.addOrdersCanChip0 -> viewModel.setCan(3)
            R.id.addOrdersCanChip1 -> viewModel.setCan(2)
            R.id.addOrdersCanChip2 -> viewModel.setCan(1)
            R.id.addOrdersCanChip3 -> viewModel.setCan(0)
        }
        if (vBinding.addOrdersChipGr.checkedChipId == View.NO_ID)
            viewModel.setCan(-1)
        checkForm()
    }
}
