package com.example.beerdistrkt.customView

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.HorizontalScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.beerdistrkt.R
import com.example.beerdistrkt.getSnapPosition
import com.example.beerdistrkt.models.BeerModel
import com.example.beerdistrkt.models.CanModel

import com.example.beerdistrkt.models.TempBeerItemModel
import com.example.beerdistrkt.utils.OnSnapPositionChangeListener
import com.example.beerdistrkt.utils.SnapOnScrollListener
import com.example.beerdistrkt.utils.inflate
import com.tbuonomo.viewpagerdotsindicator.BaseDotsIndicator
import com.tbuonomo.viewpagerdotsindicator.OnPageChangeListenerHelper
import kotlinx.android.synthetic.main.beer_item_view.view.*
import kotlinx.android.synthetic.main.numeric_edittext_view.view.*
import kotlinx.android.synthetic.main.view_beer_selector.view.*

class BeerSelectorView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), View.OnClickListener {

    private var beerPos = 0
    private val snapHelper = PagerSnapHelper()
    private val beerAdapter = BeerAdapter()

    private lateinit var beerList: List<BeerModel>
    private lateinit var cansList: List<CanModel>
    var selectedCan: CanModel? = null
    var onFormUpdate: (() -> Unit)? = null


    init {
        View.inflate(context, R.layout.view_beer_selector, this)
        layoutParams = LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        beerSelectorCanCountControl.editCount.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkForm()
            }
        })

        beerSelectorBtnLeftImg.setOnClickListener {
            beerPos =
                (snapHelper.getSnapPosition(beerSelectorBeerRecycler) + beerList.size - 1) % beerList.size
            beerSelectorBeerRecycler.smoothScrollToPosition(beerPos)
        }
        beerSelectorBtnRightImg.setOnClickListener {
            beerPos = (snapHelper.getSnapPosition(beerSelectorBeerRecycler) + 1) % beerList.size
            beerSelectorBeerRecycler.smoothScrollToPosition(beerPos)
        }
        beerSelectorCanChip0.setOnClickListener(this)
        beerSelectorCanChip1.setOnClickListener(this)
        beerSelectorCanChip2.setOnClickListener(this)
        beerSelectorCanChip3.setOnClickListener(this)

        beerSelectorCansScroll.postDelayed(Runnable {
            beerSelectorCansScroll.fullScroll(HorizontalScrollView.FOCUS_RIGHT)
        }, 100L)
    }

    fun initView(beerList: List<BeerModel>, barrelsList: List<CanModel>, onFormUpdate: () -> Unit) {
        this.beerList = beerList
        this.onFormUpdate = onFormUpdate
        cansList = barrelsList
        initBeerRecycler()
    }

    private fun setCan(pos: Int) {
        selectedCan = if (pos >= 0)
            cansList[pos]
        else
            null
    }

    fun formIsValid(): Boolean {
        return beerSelectorCanCountControl.amount > 0 && selectedCan != null
    }

    fun resetForm() {
        beerSelectorChipGr.clearCheck()
        selectedCan = null
        beerSelectorCanCountControl.amount = 0
    }

    fun checkForm() {
        onFormUpdate?.invoke()
    }

    fun getTempBeerItem(): TempBeerItemModel {
        return TempBeerItemModel(0,
            beerList[snapHelper.getSnapPosition(beerSelectorBeerRecycler)],
            selectedCan!!,
            beerSelectorCanCountControl.amount,
            {
//                showToast("del")
//                viewModel.removeOrderItemFromList(it)
            })
    }

    private fun fillBeerItemForm(orderItem: TempBeerItemModel) {
        beerPos = beerList.indexOf(orderItem.beer)
        beerSelectorBeerRecycler.smoothScrollToPosition(beerPos)
        beerSelectorChipGr.clearCheck()
        selectedCan = orderItem.canType
        when (orderItem.canType.id) {
            1 -> beerSelectorCanChip3.isChecked = true
            2 -> beerSelectorCanChip2.isChecked = true
            3 -> beerSelectorCanChip1.isChecked = true
            4 -> beerSelectorCanChip0.isChecked = true
        }

        beerSelectorCanCountControl.amount = orderItem.count
    }

    private fun initBeerRecycler() {
        beerSelectorBeerRecycler.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        beerSelectorBeerRecycler.adapter = beerAdapter
        snapHelper.attachToRecyclerView(beerSelectorBeerRecycler)
        beerSelectorBeerListIndicator.pager = getIndicatorPager(beerSelectorBeerRecycler)
    }

    private fun onStopScroll(pos: Int) {
        beerPos = pos
    }

    inner class BeerAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            BeerItemViewHolder(parent.inflate(R.layout.beer_item_view))

        override fun getItemCount() = beerList.size

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            holder.itemView.tBeerNameItm.text = beerList[position].dasaxeleba
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
            R.id.beerSelectorCanChip0 -> setCan(3)
            R.id.beerSelectorCanChip1 -> setCan(2)
            R.id.beerSelectorCanChip2 -> setCan(1)
            R.id.beerSelectorCanChip3 -> setCan(0)
        }

        if (beerSelectorChipGr.checkedChipId == View.NO_ID)
            setCan(-1)
        checkForm()
    }
}