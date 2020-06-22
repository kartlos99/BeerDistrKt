package com.example.beerdistrkt.fragPages.mitana

import android.app.DatePickerDialog
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
import com.example.beerdistrkt.databinding.AddDeliveryFragmentBinding
import com.example.beerdistrkt.getSnapPosition
import com.example.beerdistrkt.getViewModel
import com.example.beerdistrkt.models.BeerModel
import com.example.beerdistrkt.models.TempBeerItemModel
import com.example.beerdistrkt.utils.OnSnapPositionChangeListener
import com.example.beerdistrkt.utils.SnapOnScrollListener
import com.example.beerdistrkt.utils.inflate
import com.tbuonomo.viewpagerdotsindicator.BaseDotsIndicator
import com.tbuonomo.viewpagerdotsindicator.OnPageChangeListenerHelper
import kotlinx.android.synthetic.main.beer_item_view.view.*
import kotlinx.android.synthetic.main.numeric_edittext_view.view.*
import java.util.*

class AddDeliveryFragment : BaseFragment<AddDeliveryViewModel>(), View.OnClickListener {

    companion object {
        fun newInstance() = AddDeliveryFragment()
    }

    override val viewModel by lazy {
        getViewModel { AddDeliveryViewModel(clientID, orderID) }
    }

    private val clientID by lazy {
        val args = AddDeliveryFragmentArgs.fromBundle(arguments ?: Bundle())
        args.clientObjectID
    }
    private val orderID by lazy {
        val args = AddDeliveryFragmentArgs.fromBundle(arguments ?: Bundle())
        args.orderID
    }

    private var dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
        viewModel.onSaleDateSelected(year, month, day)
    }

    private var beerPos = 0
    private val snapHelper = PagerSnapHelper()
    private val beerAdapter = BeerAdapter()

    private lateinit var vBinding: AddDeliveryFragmentBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        vBinding = AddDeliveryFragmentBinding.inflate(inflater)
        vBinding.lifecycleOwner = this
        initBeerRecycler()
        return vBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vBinding.addDeliverysCanChip0.setOnClickListener(this)
        vBinding.addDeliverysCanChip1.setOnClickListener(this)
        vBinding.addDeliverysCanChip2.setOnClickListener(this)
        vBinding.addDeliverysCanChip3.setOnClickListener(this)
        vBinding.addDeliveryDoneBtn.setOnClickListener(this)
        vBinding.addDeliveryDateBtn.setOnClickListener(this)
        vBinding.btnBeerLeftImg.setOnClickListener(this)
        vBinding.btnBeerRightImg.setOnClickListener(this)
        vBinding.addDeliveryAddSaleItemBtn.setOnClickListener(this)

        vBinding.addDeliveryCanCountControl.editCount.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkForm()
            }
        })
        vBinding.addDeliveryCheckGift.setOnCheckedChangeListener { _, isChecked ->
            viewModel.isGift = isChecked
        }

        initViewModel()
        checkForm()
    }

    private fun initViewModel() {
        viewModel.clientLiveData.observeForever {
            vBinding.addDeliveryClientInfo.text = it.obieqti.dasaxeleba
        }
        viewModel.beerListLiveData.observe(viewLifecycleOwner, Observer {
            beerAdapter.setData(it)
        })
        viewModel.saleItemsLiveData.observe(viewLifecycleOwner, Observer {
            vBinding.addDeliveryTempContainer.removeAllViews()
            it.forEach { saleItem ->
                vBinding.addDeliveryTempContainer.addView(
                    TempBeerRowView(context = requireContext(), rowData = saleItem)
                )
            }
        })
        viewModel.saleDayLiveData.observe(viewLifecycleOwner, Observer {
            vBinding.addDeliveryDateBtn.text = it
        })
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.addDeliverysCanChip0 -> viewModel.setCan(3)
            R.id.addDeliverysCanChip1 -> viewModel.setCan(2)
            R.id.addDeliverysCanChip2 -> viewModel.setCan(1)
            R.id.addDeliverysCanChip3 -> viewModel.setCan(0)
            R.id.addDeliveryDoneBtn -> {
                collectEmptyBarrels()
                viewModel.setMoney(vBinding.addDeliveryMoneyEt.text)
                viewModel.addDelivery(
                    vBinding.addDeliveryComment.editText?.text.toString()
                )
            }
            R.id.addDeliveryDateBtn -> {
                val datePickerDialog = DatePickerDialog(
                    requireContext(),
                    dateSetListener,
                    viewModel.saleDateCalendar.get(Calendar.YEAR),
                    viewModel.saleDateCalendar.get(Calendar.MONTH),
                    viewModel.saleDateCalendar.get(Calendar.DAY_OF_MONTH)
                )
                datePickerDialog.setCancelable(false)
                datePickerDialog.show()
            }
            R.id.btnBeerLeftImg -> {
                beerPos =
                    (snapHelper.getSnapPosition(vBinding.addDeliveryBeerRecycler) + viewModel.beerList.size - 1) % viewModel.beerList.size
                vBinding.addDeliveryBeerRecycler.smoothScrollToPosition(beerPos)
            }
            R.id.btnBeerRightImg -> {
                beerPos =
                    (snapHelper.getSnapPosition(vBinding.addDeliveryBeerRecycler) + 1) % viewModel.beerList.size
                vBinding.addDeliveryBeerRecycler.smoothScrollToPosition(beerPos)
            }
            R.id.addDeliveryAddSaleItemBtn -> {
                if (isFormValid()) {
                    viewModel.addSaleItemToList(getTempSaleItem())
                    resetForm()
                } else
                    showToast("no data")
            }
        }

        if (vBinding.addDeliverysChipGr.checkedChipId == View.NO_ID)
            viewModel.setCan(-1)
        checkForm()
    }

    private fun collectEmptyBarrels() {
        if (vBinding.addDeliveryBarrelOutputCount1.amount > 0)
            viewModel.addBarrelToList(1, vBinding.addDeliveryBarrelOutputCount1.amount)
        if (vBinding.addDeliveryBarrelOutputCount2.amount > 0)
            viewModel.addBarrelToList(2, vBinding.addDeliveryBarrelOutputCount2.amount)
        if (vBinding.addDeliveryBarrelOutputCount3.amount > 0)
            viewModel.addBarrelToList(3, vBinding.addDeliveryBarrelOutputCount3.amount)
        if (vBinding.addDeliveryBarrelOutputCount4.amount > 0)
            viewModel.addBarrelToList(4, vBinding.addDeliveryBarrelOutputCount4.amount)
    }

    fun getTempSaleItem(): TempBeerItemModel {
        return TempBeerItemModel(0,
//            viewModel.beerList[beerPos],
            viewModel.beerListLiveData.value?.get(beerPos)!!,
            viewModel.selectedCan!!,
            vBinding.addDeliveryCanCountControl.amount,
            {
                showToast("del")
                viewModel.removeSaleItemFromList(it)
            })
    }

    fun isFormValid(): Boolean {
        return vBinding.addDeliveryCanCountControl.amount > 0 && viewModel.selectedCan != null
    }

    fun resetForm() {
        vBinding.addDeliverysChipGr.clearCheck()
        viewModel.selectedCan = null
        vBinding.addDeliveryCanCountControl.amount = 0
    }

    fun checkForm() {
        vBinding.addDeliveryAddSaleItemBtn.backgroundTintList = if (isFormValid())
            ColorStateList.valueOf(Color.GREEN)
        else
            ColorStateList.valueOf(Color.RED)
    }


    private fun initBeerRecycler() {
        vBinding.addDeliveryBeerRecycler.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        beerAdapter.setData(viewModel.beerList)
        vBinding.addDeliveryBeerRecycler.adapter = beerAdapter
        snapHelper.attachToRecyclerView(vBinding.addDeliveryBeerRecycler)
        vBinding.addDeliveryBeerListIndicator.pager =
            getIndicatorPager(vBinding.addDeliveryBeerRecycler)
    }

    private fun onStopScroll(pos: Int) {
        beerPos = pos
    }

    inner class BeerAdapter(private var bList: List<BeerModel> = mutableListOf()) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            BeerItemViewHolder(parent.inflate(R.layout.beer_item_view))

        override fun getItemCount() = bList.size

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            holder.itemView.tBeerNameItm.text =
                "${bList[position].dasaxeleba}\n${bList[position].fasi} ₾"
        }

        fun setData(beerList: List<BeerModel>) {
            bList = beerList
            notifyDataSetChanged()
        }
    }

    inner class BeerItemViewHolder(view: View) : RecyclerView.ViewHolder(view)

    private fun getIndicatorPager(rv: RecyclerView): BaseDotsIndicator.Pager {
        return object : BaseDotsIndicator.Pager {
            var onPageChangeListener: SnapOnScrollListener? = null

            override val isNotEmpty: Boolean
                get() = viewModel.beerList.size > 0
            override val currentItem: Int
                get() = snapHelper.getSnapPosition(rv)
            override val isEmpty: Boolean
                get() = viewModel.beerList.size == 0
            override val count: Int
                get() = viewModel.beerList.size

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
}
