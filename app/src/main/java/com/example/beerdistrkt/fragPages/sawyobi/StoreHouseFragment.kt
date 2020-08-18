package com.example.beerdistrkt.fragPages.sawyobi

import android.app.DatePickerDialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.beerdistrkt.BaseFragment
import com.example.beerdistrkt.R
import com.example.beerdistrkt.customView.TempBeerRowView
import com.example.beerdistrkt.fragPages.sawyobi.adapters.SimpleBeerRowAdapter
import com.example.beerdistrkt.fragPages.sawyobi.models.SimpleBeerRowModel
import com.example.beerdistrkt.getViewModel
import com.example.beerdistrkt.utils.ApiResponseState
import com.example.beerdistrkt.utils.goAway
import com.example.beerdistrkt.utils.visibleIf
import kotlinx.android.synthetic.main.sawyobi_fragment.*
import java.util.*

class StoreHouseFragment : BaseFragment<StoreHouseViewModel>(), View.OnClickListener {

    companion object {
        fun newInstance() = StoreHouseFragment()
    }

    override val viewModel by lazy {
        getViewModel { StoreHouseViewModel() }
    }
    private var dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
        viewModel.onDateSelected(year, month, day)
    }

    var pageTitleRes = R.string.sawyobi

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

        storeHouseSetDateBtn.setOnClickListener(this)
        storeHouseCheckBox.setOnClickListener(this)
        storeHouseAddBeerItemBtn.setOnClickListener(this)
        storeHouseDoneBtn.setOnClickListener(this)
    }

    private fun switchToEditMode() {
        pageTitleRes = R.string.m_edit
        storeHouseFullBarrelsRecycler.goAway()
        storeHouseEmptyBarrelDataContainer.goAway()
        viewModel.clearEnteredData()
    }

    override fun onStart() {
        super.onStart()

        if (StoreHouseListFragment.editingIoDate.isNotEmpty()) {
            viewModel.getEditingData(StoreHouseListFragment.editingIoDate)
            switchToEditMode()
        }

        clearEmptyBarrels()
        (activity as AppCompatActivity).supportActionBar?.title = resources.getString(pageTitleRes)

        storeHouseReceiveBeerSelector.onDeleteClick = {
            showToast("del")
            viewModel.removeReceiveItemFromList(it)
        }
        storeHouseReceiveBeerSelector.initView(
            viewModel.beerList,
            viewModel.cansList,
            ::onFormUpdate
        )
    }

    private fun onFormUpdate() {
        storeHouseAddBeerItemBtn.backgroundTintList =
            if (storeHouseReceiveBeerSelector.formIsValid())
                ColorStateList.valueOf(Color.GREEN)
            else
                ColorStateList.valueOf(Color.RED)
    }

    private fun initViewModel() {
        viewModel.setDayLiveData.observe(viewLifecycleOwner, Observer {
            storeHouseSetDateBtn.text = it
        })
        viewModel.fullBarrelsListLiveData.observe(viewLifecycleOwner, Observer {
            initFullRecycler(it)
        })
        viewModel.emptyBarrelsListLiveData.observe(viewLifecycleOwner, Observer {
            if (it.size > 1) {
                storeHouseEmptyBarrelsAtHouse.setData(SimpleBeerRowModel(it[0].title, it[0].values))
                storeHouseEmptyBarrelsAtClients.setData(
                    SimpleBeerRowModel(
                        it[1].title,
                        it[1].values
                    )
                )
            }
        })
        viewModel.receivedItemDuplicateLiveData.observe(viewLifecycleOwner, Observer {
            if (it) {
                showToast(R.string.already_in_list)
                viewModel.receivedItemDuplicateLiveData.value = false
            }
        })
        viewModel.receivedItemsLiveData.observe(viewLifecycleOwner, Observer {
            storeHouseReceiveBeerSelector.resetForm()
            storeHouseSelectedBeerContainer.removeAllViews()
            it.forEach { receivedItem ->
                val itemData = receivedItem.copy(onEditClick = { tempBeerItem ->
                    storeHouseReceiveBeerSelector.fillBeerItemForm(tempBeerItem)
                    viewModel.editingItemID = tempBeerItem.orderItemID
                })
                storeHouseSelectedBeerContainer.addView(
                    TempBeerRowView(context = requireContext(), rowData = itemData)
                )
            }
            setEmptyBarrels()
        })
        viewModel.doneLiveData.observe(viewLifecycleOwner, Observer {
            when (it) {
                is ApiResponseState.Loading -> storeHouseProgress.visibleIf(it.showLoading)
                is ApiResponseState.Success -> {
                    showToast(it.data)
                    resetPage()
                    viewModel.sleepDoneLiveData()
                    if (viewModel.editMode) {
                        StoreHouseListFragment.editingIoDate = ""
                        findNavController()
                            .navigate(StoreHouseFragmentDirections.actionSawyobiFragmentToSawyobiListFragment())
                    }
                }
            }
        })
        viewModel.editDataReceiveLiveData.observe(viewLifecycleOwner, Observer {
            when (it) {
                is ApiResponseState.Loading -> storeHouseProgress.visibleIf(it.showLoading)
                is ApiResponseState.Success -> {
                    storeHouseComment.editText?.setText(it.data.comment)
                }
            }
        })
    }

    private fun resetPage() {
        storeHouseReceiveBeerSelector.resetForm()
        clearEmptyBarrels()
        storeHouseComment.editText?.setText("")
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.sawyobi_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.sawyobiDetail -> view!!.findNavController()
                .navigate(StoreHouseFragmentDirections.actionSawyobiFragmentToSawyobiListFragment())
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.storeHouseCheckBox -> {
                viewModel.isChecked = storeHouseCheckBox.isChecked
                storeHouseEmptyBarrelManageBox.visibleIf(!storeHouseCheckBox.isChecked)
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
                if (storeHouseReceiveBeerSelector.formIsValid())
                    viewModel.addBeerReceiveItemToList(storeHouseReceiveBeerSelector.getTempBeerItem())
                else
                    showToast(R.string.fill_data)
            }
            R.id.storeHouseDoneBtn -> {

                viewModel.onDoneClick(
                    storeHouseComment.editText?.text.toString(),
                    collectEmptyBarrels(),
                    StoreHouseListFragment.editingIoDate
                )
            }

        }
    }

    private fun collectEmptyBarrels(): List<Int> {
        return listOf(
            storeHouseBarrelOutputCount1.amount,
            storeHouseBarrelOutputCount2.amount,
            storeHouseBarrelOutputCount3.amount,
            storeHouseBarrelOutputCount4.amount
        )
    }

    private fun clearEmptyBarrels() {
        storeHouseBarrelOutputCount1.amount = 0
        storeHouseBarrelOutputCount2.amount = 0
        storeHouseBarrelOutputCount3.amount = 0
        storeHouseBarrelOutputCount4.amount = 0
    }

    fun setEmptyBarrels() {
        viewModel.barrelOutItems.forEach {
            when (it.canTypeID) {
                1 -> storeHouseBarrelOutputCount1.amount = it.count
                2 -> storeHouseBarrelOutputCount2.amount = it.count
                3 -> storeHouseBarrelOutputCount3.amount = it.count
                4 -> storeHouseBarrelOutputCount4.amount = it.count
            }
        }
    }

    private fun initFullRecycler(data: List<SimpleBeerRowModel>) {
        storeHouseFullBarrelsRecycler.layoutManager = LinearLayoutManager(context)
        storeHouseFullBarrelsRecycler.adapter = SimpleBeerRowAdapter(data)
    }
}
