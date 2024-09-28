package com.example.beerdistrkt.customView

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.beerdistrkt.R
import com.example.beerdistrkt.databinding.BeerItemViewBinding
import com.example.beerdistrkt.databinding.ViewBeerSelectorBinding
import com.example.beerdistrkt.fragPages.realisation.models.BarrelRowModel
import com.example.beerdistrkt.getSnapPosition
import com.example.beerdistrkt.models.BeerModelBase
import com.example.beerdistrkt.models.CanModel
import com.example.beerdistrkt.models.TempBeerItemModel
import com.example.beerdistrkt.utils.OnSnapPositionChangeListener
import com.example.beerdistrkt.utils.SnapOnScrollListener
import com.google.android.material.chip.Chip
import com.tbuonomo.viewpagerdotsindicator.BaseDotsIndicator
import com.tbuonomo.viewpagerdotsindicator.OnPageChangeListenerHelper

class BeerSelectorView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var beerPos = 0
    private val snapHelper = PagerSnapHelper()

    private lateinit var allBeers: List<BeerModelBase>
    private lateinit var visibleBeers: List<BeerModelBase>
    private lateinit var cansList: List<CanModel>

    var onFormUpdate: (() -> Unit)? = null

    var onDeleteClick: ((beerItem: TempBeerItemModel) -> Unit)? = null
    var onEditClick: ((beerItem: TempBeerItemModel) -> Unit)? = null

    private var itemID = 0
    var withPrices = false

    private var selectedBeer: BeerModelBase? = null

    private val binding =
        ViewBeerSelectorBinding.bind(inflate(context, R.layout.view_beer_selector, this))

    init {
        layoutParams = LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        with(binding) {
            beerSelectorCanCountControl.getEditTextView()
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

            beerSelectorBtnLeftImg.setOnClickListener {
                beerPos =
                    (snapHelper.getSnapPosition(beerSelectorBeerRecycler) + visibleBeers.size - 1) % visibleBeers.size
                beerSelectorBeerRecycler.smoothScrollToPosition(beerPos)
            }
            beerSelectorBtnRightImg.setOnClickListener {
                beerPos =
                    (snapHelper.getSnapPosition(beerSelectorBeerRecycler) + 1) % visibleBeers.size
                beerSelectorBeerRecycler.smoothScrollToPosition(beerPos)
            }

            beerSelectorCansScroll.postDelayed({
                beerSelectorCansScroll.fullScroll(HorizontalScrollView.FOCUS_RIGHT)
            }, 100L)
        }
    }

    // mandatory
    fun initView(
        beerList: List<BeerModelBase>,
        barrelsList: List<CanModel>,
        onFormUpdate: () -> Unit
    ) {
        selectedBeer = null
        this.onFormUpdate = onFormUpdate
        cansList = barrelsList
        updateBeers(beerList)
        fillBarrels()
    }

    private fun createChipView(barrel: CanModel): Chip =
        Chip(context, null, R.style.Widget_MaterialComponents_Chip_Choice).apply {
            id = barrel.id
            text = barrel.name
            isCheckedIconVisible = true
            checkedIcon = AppCompatResources.getDrawable(
                context,
                R.drawable.beer_icon
            )
            chipStrokeColor = AppCompatResources.getColorStateList(context, R.color.gray_3)
            chipStrokeWidth = resources.getDimensionPixelSize(R.dimen.gr_size_1).toFloat()
            isCheckable = true
            isClickable = true
        }

    private fun fillBarrels() {
        binding.beerSelectorChipGr.removeAllViews()
        cansList
            .sortedBy { it.sortValue }
            .reversed()
            .forEach { barrel ->
                binding.beerSelectorChipGr.addView(createChipView(barrel))
            }
        binding.beerSelectorChipGr.setOnCheckedStateChangeListener { _, _ ->
            checkForm()
        }
    }

    fun updateBeers(beerList: List<BeerModelBase>) {
        allBeers = beerList
        visibleBeers = beerList.getVisibleBeers(selectedBeer)
        initBeerRecycler()
        selectedBeer?.let { scrollToBeer(it.id) }
        checkForm()
    }

    private fun List<BeerModelBase>.getVisibleBeers(
        currentBeerItem: BeerModelBase? = null
    ): List<BeerModelBase> = this.filter {
        it.isActive || (currentBeerItem?.id == it.id)
    }

    private fun setBarrel(id: Int) = with(binding) {
        beerSelectorChipGr.clearCheck()
        beerSelectorChipGr.children.forEach { view ->
            if (view.id == id)
                (view as Chip).isChecked = true
        }
    }

    fun formIsValid(): Boolean {
        return binding.beerSelectorCanCountControl.amount > 0
                && binding.beerSelectorChipGr.checkedChipId != View.NO_ID
    }

    fun resetForm() {
        binding.beerSelectorChipGr.clearCheck()
        binding.beerSelectorCanCountControl.amount = 0
    }

    private fun checkForm() {
        onFormUpdate?.invoke()
    }

    fun getTempBeerItem(): TempBeerItemModel {
        val barrel = cansList.firstOrNull { it.id == binding.beerSelectorChipGr.checkedChipId }
            ?: throw IllegalArgumentException("No barrel set in BeerSelector!!")
        return TempBeerItemModel(
            itemID,
            visibleBeers[snapHelper.getSnapPosition(binding.beerSelectorBeerRecycler)],
            barrel,
            binding.beerSelectorCanCountControl.amount,
            {
                onDeleteClick?.invoke(it)
            },
            itemID
        )
    }

    private fun scrollToBeer(beerID: Int) {
        beerPos = visibleBeers.map { it.id }.indexOf(beerID)
        if (beerPos >= 0)
            binding.beerSelectorBeerRecycler.smoothScrollToPosition(beerPos)
    }

    fun fillBeerItemForm(item: TempBeerItemModel) = with(binding) {
        selectedBeer = item.beer
        visibleBeers = allBeers.getVisibleBeers(item.beer)
        initBeerRecycler()
        itemID = item.orderItemID
        scrollToBeer(item.beer.id)
        setBarrel(item.canType.id)
        beerSelectorCanCountControl.amount = item.count
    }

    fun fillBarrels(barrelRowModel: BarrelRowModel) = with(binding) {
        setBarrel(barrelRowModel.canTypeID)
        beerSelectorCanCountControl.amount = barrelRowModel.count
    }

    fun getBarrelsCount() = binding.beerSelectorCanCountControl.amount

    fun getBarrelID() = binding.beerSelectorChipGr.checkedChipId

    private fun initBeerRecycler() = with(binding) {
        beerSelectorBeerRecycler.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        beerSelectorBeerRecycler.adapter = BeerAdapter()
        snapHelper.attachToRecyclerView(beerSelectorBeerRecycler)
        beerSelectorBeerListIndicator.pager = getIndicatorPager(beerSelectorBeerRecycler)
    }

    private fun onStopScroll(pos: Int) {
        beerPos = pos
        selectedBeer = null
    }

    fun beerGroupVisibleIf(isVisible: Boolean) {
        binding.beerGroup.isVisible = isVisible
    }

    inner class BeerAdapter : RecyclerView.Adapter<BeerItemViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            BeerItemViewHolder(
                BeerItemViewBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )

        override fun getItemCount() = visibleBeers.size

        override fun onBindViewHolder(holder: BeerItemViewHolder, position: Int) {

            holder.itemBinding.tBeerNameItm.text = if (withPrices)
                "${visibleBeers[position].dasaxeleba}\n${visibleBeers[position].fasi} ₾"
            else
                visibleBeers[position].dasaxeleba
            holder.itemBinding.tBeerNameItm.backgroundTintList = ColorStateList
                .valueOf(Color.parseColor(visibleBeers[position].displayColor))
        }
    }

    inner class BeerItemViewHolder(val itemBinding: BeerItemViewBinding) :
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
}