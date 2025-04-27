package com.example.beerdistrkt.customView

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.beerdistrkt.R
import com.example.beerdistrkt.databinding.BottleItemViewBinding
import com.example.beerdistrkt.databinding.ViewBottleSelectorBinding
import com.example.beerdistrkt.getSnapPosition
import com.example.beerdistrkt.fragPages.bottle.domain.model.Bottle
import com.example.beerdistrkt.fragPages.bottle.presentation.model.TempBottleItemModel
import com.example.beerdistrkt.utils.OnSnapPositionChangeListener
import com.example.beerdistrkt.utils.SnapOnScrollListener
import com.tbuonomo.viewpagerdotsindicator.BaseDotsIndicator
import com.tbuonomo.viewpagerdotsindicator.OnPageChangeListenerHelper

class BottleSelectorView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr), View.OnClickListener {

    private var bottleIndex = 0
    private val snapHelper = PagerSnapHelper()

    private lateinit var allBottles: List<Bottle>
    private lateinit var visibleBottles: List<Bottle>
    var onFormUpdate: (() -> Unit)? = null

    var onDeleteClick: ((bottleItem: TempBottleItemModel) -> Unit)? = null
    var onEditClick: ((bottleItem: TempBottleItemModel) -> Unit)? = null

    private var itemID = 0
    var withPrices = false

    private var selectedBottle: Bottle? = null

    private val binding =
        ViewBottleSelectorBinding.bind(inflate(context, R.layout.view_bottle_selector, this))

    init {
        layoutParams = LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        with(binding) {
            bottleCountControl.getEditTextView()
                .addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {}

                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {
                    }

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                        checkForm()
                    }
                })

            leftSelectorImg.setOnClickListener {
                if (visibleBottles.isEmpty()) return@setOnClickListener
                bottleIndex =
                    (snapHelper.getSnapPosition(bottleRecycler) + visibleBottles.size - 1) % visibleBottles.size
                bottleRecycler.smoothScrollToPosition(bottleIndex)
            }
            rightSelectorImg.setOnClickListener {
                if (visibleBottles.isEmpty()) return@setOnClickListener
                bottleIndex =
                    (snapHelper.getSnapPosition(bottleRecycler) + 1) % visibleBottles.size
                bottleRecycler.smoothScrollToPosition(bottleIndex)
            }
        }
    }

    // mandatory
    fun initView(
        bottles: List<Bottle>,
        onFormUpdate: () -> Unit
    ) {
        selectedBottle = null
        this.onFormUpdate = onFormUpdate
        updateBottles(bottles)
    }

    fun updateBottles(bottles: List<Bottle>) {
        allBottles = bottles
        visibleBottles = bottles.getVisibleBeers(selectedBottle)
        initBottleRecycler()
        selectedBottle?.let { scrollToItem(it.id) }
        checkForm()
    }

    private fun List<Bottle>.getVisibleBeers(
        currentBottleItem: Bottle? = null
    ): List<Bottle> = this.filter {
        it.isActive || (currentBottleItem?.id == it.id)
    }

    fun isFormValid(): Boolean {
        return binding.bottleCountControl.amount > 0
    }

    fun resetForm() {
        binding.bottleCountControl.amount = 0
    }

    private fun checkForm() {
        onFormUpdate?.invoke()
    }

    fun getTempBottleItem(): TempBottleItemModel {
        return TempBottleItemModel(
            itemID,
            visibleBottles[snapHelper.getSnapPosition(binding.bottleRecycler)],
            binding.bottleCountControl.amount,
            {
                onDeleteClick?.invoke(it)
            },
            itemID
        )
    }

    private fun scrollToItem(itemID: Int) {
        bottleIndex = visibleBottles.map { it.id }.indexOf(itemID)
        if (bottleIndex >= 0)
            binding.bottleRecycler.smoothScrollToPosition(bottleIndex)
    }

    fun fillBottleItemForm(item: TempBottleItemModel) = with(binding) {
        selectedBottle = item.bottle
        visibleBottles = allBottles.getVisibleBeers(item.bottle)
        initBottleRecycler()
        itemID = item.orderItemID //?? why we need it
        scrollToItem(item.bottle.id)
        bottleCountControl.amount = item.count
    }

    fun getBottleCount() = binding.bottleCountControl.amount

    private fun initBottleRecycler() = with(binding) {
        bottleRecycler.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        bottleRecycler.adapter = BottleAdapter()
        snapHelper.attachToRecyclerView(bottleRecycler)
        bottleSelectorIndicator.pager = getIndicatorPager(bottleRecycler)
    }

    private fun onStopScroll(pos: Int) {
        bottleIndex = pos
        selectedBottle = null
    }

    fun bottleGroupVisibleIf(isVisible: Boolean) {
        binding.bottleGroup.isVisible = isVisible
    }

    inner class BottleAdapter : RecyclerView.Adapter<BottleItemViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            BottleItemViewHolder(
                BottleItemViewBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )

        override fun getItemCount() = visibleBottles.size

        override fun onBindViewHolder(holder: BottleItemViewHolder, position: Int) {
            val bottle = visibleBottles[position]
            holder.itemBinding.bottleTitleTv.text = bottle.name
            holder.itemBinding.bottleSizeTv.text = "${bottle.volume} lt."
            bottle.imageLink?.let {
                // load images
            }
            holder.itemBinding.bottlePriceTv.text = "${bottle.price}₾"
            holder.itemBinding.bottlePriceTv.isVisible = withPrices
        }
    }

    inner class BottleItemViewHolder(val itemBinding: BottleItemViewBinding) :
        RecyclerView.ViewHolder(itemBinding.root)


    private fun getIndicatorPager(rv: RecyclerView): BaseDotsIndicator.Pager {
        return object : BaseDotsIndicator.Pager {
            var onPageChangeListener: SnapOnScrollListener? = null

            override val isNotEmpty: Boolean
                get() = (rv.adapter?.itemCount ?: 0) > 0
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

        checkForm()
    }
}