package com.example.beerdistrkt.fragPages.sawyobi

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.beerdistrkt.BaseFragment
import com.example.beerdistrkt.R
import com.example.beerdistrkt.adapters.SimpleListAdapter
import com.example.beerdistrkt.customView.TempBeerRowView
import com.example.beerdistrkt.customView.TempBottleRowView
import com.example.beerdistrkt.databinding.SawyobiFragmentBinding
import com.example.beerdistrkt.fragPages.login.models.Permission
import com.example.beerdistrkt.fragPages.orders.view.OrderBottleItemView
import com.example.beerdistrkt.fragPages.realisation.RealisationType
import com.example.beerdistrkt.fragPages.sawyobi.Event.DuplicateBarrelItem
import com.example.beerdistrkt.fragPages.sawyobi.Event.DuplicateBottleItem
import com.example.beerdistrkt.fragPages.sawyobi.adapters.SimpleBeerRowAdapter
import com.example.beerdistrkt.fragPages.sawyobi.models.SimpleBeerRowModel
import com.example.beerdistrkt.fragPages.sawyobi.models.SimpleBottleRowModel
import com.example.beerdistrkt.fragPages.sawyobi.models.StoreInsertRequestModel
import com.example.beerdistrkt.getViewModel
import com.example.beerdistrkt.models.TempBeerItemModel
import com.example.beerdistrkt.models.bottle.TempBottleItemModel
import com.example.beerdistrkt.utils.ApiResponseState
import com.example.beerdistrkt.utils.Session
import com.example.beerdistrkt.utils.goAway
import kotlinx.coroutines.flow.collectLatest
import java.util.Calendar

class StoreHouseFragment : BaseFragment<StoreHouseViewModel>(), View.OnClickListener {

    private val binding by viewBinding(SawyobiFragmentBinding::bind)

    override val viewModel by lazy {
        getViewModel { StoreHouseViewModel() }
    }
    private var dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
        viewModel.onDateSelected(year, month, day)

        val timePickerDialog = TimePickerDialog(
            requireContext(),
            timeSetListener,
            viewModel.selectedDate.get(Calendar.HOUR_OF_DAY),
            viewModel.selectedDate.get(Calendar.MINUTE),
            true
        )
        timePickerDialog.setCancelable(false)
        timePickerDialog.show()
    }

    private val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
        viewModel.onSaleTimeSelected(hourOfDay, minute)
    }

    private var pageTitleRes = R.string.store_house

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)

        return inflater.inflate(R.layout.sawyobi_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewModel()

        with(binding) {
            storeHouseSetDateBtn.setOnClickListener(this@StoreHouseFragment)
            storeHouseCheckBox.setOnClickListener(this@StoreHouseFragment)
            storeHouseAddBeerItemBtn.setOnClickListener(this@StoreHouseFragment)
            storeHouseDoneBtn.setOnClickListener(this@StoreHouseFragment)

            storeHouseDoneBtn.isEnabled = Session.get().hasPermission(Permission.AddEditStoreHouse)
            storeHouseAddBeerItemBtn.isVisible =
                Session.get().hasPermission(Permission.AddEditStoreHouse)

            realisationTypeSelector.check(R.id.realizationByBarrel)
            realisationTypeSelector.addOnButtonCheckedListener { _, checkedId, isChecked ->
                if (isChecked) {
                    when (checkedId) {
                        R.id.realizationByBarrel -> viewModel.switchToBarrel()
                        R.id.realizationByBottle -> viewModel.switchToBottle()
                    }
                    onFormUpdate()
                }
            }
        }
    }

    private fun switchToEditMode() {
        pageTitleRes = R.string.m_edit
        binding.storeHouseFullBarrelsRecycler.goAway()
        binding.storeHouseEmptyBarrelDataContainer.goAway()
        viewModel.clearEnteredData()
    }

    override fun onStart() {
        super.onStart()
        pageTitleRes = R.string.store_house
        viewModel.setCurrentTime()
        if (StoreHouseListFragment.editingGroupID.isNotEmpty()) {
            viewModel.getEditingData(StoreHouseListFragment.editingGroupID)
            switchToEditMode()
        }

        clearEmptyBarrels()
        (activity as AppCompatActivity).supportActionBar?.title = resources.getString(pageTitleRes)
        binding.beerSelector.onDeleteClick = {
            showToast("del")
            viewModel.removeReceiveItemFromList(it)
        }
        binding.beerSelector.initView(
            viewModel.beerList,
            viewModel.cansList,
            ::onFormUpdate
        )
        binding.bottleSelector.onDeleteClick = {
            showToast("del")
            viewModel.removeReceiveItemFromList(it)
        }
        binding.bottleSelector.initView(
            viewModel.bottleList,
            ::onFormUpdate
        )
    }

    private fun onFormUpdate() = with(binding) {
        storeHouseAddBeerItemBtn.backgroundTintList = if (
            viewModel.realisationType == RealisationType.BARREL && beerSelector.formIsValid()
            ||
            viewModel.realisationType == RealisationType.BOTTLE && bottleSelector.isFormValid()
        )
            ColorStateList.valueOf(Color.GREEN)
        else
            ColorStateList.valueOf(Color.RED)
    }

    private fun onBeerItemEdit(tempBeerItem: TempBeerItemModel) = with(binding) {
        realisationTypeSelector.check(R.id.realizationByBarrel)
        viewModel.switchToBarrel()
        beerSelector.fillBeerItemForm(tempBeerItem)
        viewModel.editingItemID = tempBeerItem.orderItemID
    }

    private fun onBottleItemEdit(tempBottleItem: TempBottleItemModel) = with(binding) {
        realisationTypeSelector.check(R.id.realizationByBottle)
        viewModel.switchToBottle()
        bottleSelector.fillBottleItemForm(tempBottleItem)
        viewModel.editingItemID = tempBottleItem.orderItemID
    }

    private fun initViewModel() = with(binding) {
        lifecycleScope.launchWhenStarted {
            viewModel.realisationStateFlow.collectLatest {
                when (it) {
                    RealisationType.BARREL -> {
                        beerSelector.isVisible = true
                        bottleSelector.isVisible = false
                    }

                    RealisationType.BOTTLE -> {
                        beerSelector.isVisible = false
                        bottleSelector.isVisible = true
                    }

                    RealisationType.NONE -> {}
                }
            }
        }
        viewModel.setDayLiveData.observe(viewLifecycleOwner) {
            storeHouseSetDateBtn.text = it
        }
        viewModel.fullBarrelsListLiveData.observe(viewLifecycleOwner) {
            initFullRecycler(it)
        }
        viewModel.bottlesLiveData.observe(viewLifecycleOwner) {
            initBottlesRecycler(it)
        }
        viewModel.emptyBarrelsListLiveData.observe(viewLifecycleOwner) {
            if (it.size > 1) {
                storeHouseEmptyBarrelsAtHouse.setData(
                    SimpleBeerRowModel(
                        it[0].title,
                        it[0].values
                    )
                )
                storeHouseEmptyBarrelsAtClients.setData(
                    SimpleBeerRowModel(
                        it[1].title,
                        it[1].values
                    )
                )
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.eventsFlow.collectLatest {
                when (it) {
                    DuplicateBarrelItem -> showToast(R.string.already_in_list)
                    DuplicateBottleItem -> showToast(R.string.bottle_already_in_list)
                }
            }
        }
        viewModel.tempInputItemsLiveData.observe(viewLifecycleOwner) {
            beerSelector.resetForm()
            bottleSelector.resetForm()
            storeHouseSelectedBeerContainer.removeAllViews()
            it.byBarrels.forEach { receivedItem ->
                val itemData = receivedItem.copy(onEditClick = ::onBeerItemEdit)
                storeHouseSelectedBeerContainer.addView(
                    TempBeerRowView(context = requireContext(), rowData = itemData)
                )
            }
            it.byBottles.forEach { receivedItem ->
                val itemData = receivedItem.copy(onEditClick = ::onBottleItemEdit)
                storeHouseSelectedBeerContainer.addView(
                    TempBottleRowView(context = requireContext(), rowData = itemData)
                )
            }
        }
        viewModel.doneLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is ApiResponseState.Loading -> storeHouseProgress.isVisible = it.showLoading
                is ApiResponseState.Success -> {
                    showToast(it.data)
                    resetPage()
                    viewModel.sleepDoneLiveData()
                    if (viewModel.editMode) {
                        StoreHouseListFragment.editingGroupID = ""
                        findNavController()
                            .navigate(StoreHouseFragmentDirections.actionSawyobiFragmentToSawyobiListFragment())
                    }
                }

                else -> {}
            }
        }
        viewModel.editDataReceiveLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is ApiResponseState.Loading -> storeHouseProgress.isVisible = it.showLoading
                is ApiResponseState.Success -> {
                    storeHouseComment.editText?.setText(it.data.comment)
                }

                else -> {}
            }
        }
        viewModel.emptyBarrelsEditingLiveData.observe(viewLifecycleOwner) {
            setEmptyBarrels(it)
        }
    }

    private fun resetPage() {
        binding.beerSelector.resetForm()
        clearEmptyBarrels()
        binding.storeHouseComment.editText?.setText("")
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.sawyobi_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.sawyobiDetail -> {
                if (Session.get().hasPermission(Permission.AddEditStoreHouse))
                    requireView().findNavController()
                        .navigate(StoreHouseFragmentDirections.actionSawyobiFragmentToSawyobiListFragment())
                else
                    showToast(R.string.no_permission_common)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.storeHouseCheckBox -> with(binding) {
                viewModel.isChecked = storeHouseCheckBox.isChecked
                storeHouseEmptyBarrelManageBox.isVisible = !storeHouseCheckBox.isChecked
                storeHouseMainScroll.setBackgroundColor(
                    resources.getColor(
                        if (storeHouseCheckBox.isChecked)
                            R.color.color_chek_bkg
                        else R.color.white
                    )
                )
            }

            R.id.storeHouseSetDateBtn -> {
                val datePickerDialog = DatePickerDialog(
                    requireContext(),
                    dateSetListener,
                    viewModel.selectedDate.get(Calendar.YEAR),
                    viewModel.selectedDate.get(Calendar.MONTH),
                    viewModel.selectedDate.get(Calendar.DAY_OF_MONTH)
                )
                datePickerDialog.setCancelable(false)
                datePickerDialog.show()
            }

            R.id.storeHouseAddBeerItemBtn -> {
                if (Session.get().hasPermission(Permission.AddEditStoreHouse)) {
                    tryCollectInputItem()
                } else
                    showToast(R.string.no_permission_common)
            }

            R.id.storeHouseDoneBtn -> {
                if (Session.get().hasPermission(Permission.AddEditStoreHouse))
                    viewModel.onDoneClick(
                        binding.storeHouseComment.editText?.text.toString(),
                        collectEmptyBarrels(),
                        StoreHouseListFragment.editingGroupID
                    )
                else
                    showToast(R.string.no_permission_common)
            }

        }
    }

    private fun tryCollectInputItem() = when {
        viewModel.realisationType == RealisationType.BARREL && binding.beerSelector.formIsValid() ->
            viewModel.addBeerReceiveItemToList(binding.beerSelector.getTempBeerItem())

        viewModel.realisationType == RealisationType.BOTTLE && binding.bottleSelector.isFormValid() ->
            viewModel.addBottleReceiveItemToList(binding.bottleSelector.getTempBottleItem())

        else -> showToast(R.string.fill_data)
    }

    private fun collectEmptyBarrels(): List<Int> {
        return with(binding) {
            listOf(
                storeHouseBarrelOutputCount1.amount,
                storeHouseBarrelOutputCount2.amount,
                storeHouseBarrelOutputCount3.amount,
                storeHouseBarrelOutputCount4.amount
            )
        }
    }

    private fun clearEmptyBarrels() {
        with(binding) {
            storeHouseBarrelOutputCount1.amount = 0
            storeHouseBarrelOutputCount2.amount = 0
            storeHouseBarrelOutputCount3.amount = 0
            storeHouseBarrelOutputCount4.amount = 0
        }
    }

    private fun setEmptyBarrels(data: List<StoreInsertRequestModel.BarrelOutItem>) {
        with(binding) {
            data.forEach {
                when (it.canTypeID) {
                    1 -> storeHouseBarrelOutputCount1.amount = it.count
                    2 -> storeHouseBarrelOutputCount2.amount = it.count
                    3 -> storeHouseBarrelOutputCount3.amount = it.count
                    4 -> storeHouseBarrelOutputCount4.amount = it.count
                }
            }
        }
    }

    private fun initFullRecycler(data: List<SimpleBeerRowModel>) {
        binding.storeHouseFullBarrelsRecycler.layoutManager = LinearLayoutManager(context)
        binding.storeHouseFullBarrelsRecycler.adapter = SimpleBeerRowAdapter(data)
    }

    private fun initBottlesRecycler(data: List<SimpleBottleRowModel>) =
        with(binding.storeHouseBottlesRecycler) {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = SimpleListAdapter(
                data,
                {
                    return@SimpleListAdapter OrderBottleItemView(requireContext(), null)
                },
                onBind = { item, view ->
                    (view as OrderBottleItemView).apply {
                        fillData(item)
                    }
                }
            )
        }
}
