package com.example.beerdistrkt.fragPages.sawyobi

import android.app.DatePickerDialog
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import android.view.*
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.beerdistrkt.BaseFragment
import com.example.beerdistrkt.R
import com.example.beerdistrkt.customView.TempBeerRowView
import com.example.beerdistrkt.fragPages.mitana.AddDeliveryFragment
import com.example.beerdistrkt.fragPages.sawyobi.adapters.SimpleBeerRowAdapter
import com.example.beerdistrkt.fragPages.sawyobi.models.SimpleBeerRowModel
import com.example.beerdistrkt.getViewModel
import com.example.beerdistrkt.utils.ApiResponseState
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

    override fun onStart() {
        super.onStart()

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
                storeHouseEmptyBarrelsAtHouse.setData(it[0].title, it[0].values)
                storeHouseEmptyBarrelsAtClients.setData(it[1].title, it[1].values)
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
                storeHouseSelectedBeerContainer.addView(
                    TempBeerRowView(context = requireContext(), rowData = receivedItem)
                )
            }
        })
        viewModel.doneLiveData.observe(viewLifecycleOwner, Observer {
            when (it) {
                is ApiResponseState.Loading -> storeHouseProgress.visibleIf(it.showLoading)
                is ApiResponseState.Success -> {
                    showToast(it.data)
                    resetPage()
                }
            }
        })
    }

    private fun resetPage() {
        storeHouseReceiveBeerSelector.resetForm()
        storeHouseBarrelOutputCount1.amount = 0
        storeHouseBarrelOutputCount2.amount = 0
        storeHouseBarrelOutputCount3.amount = 0
        storeHouseBarrelOutputCount4.amount = 0
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
                    collectEmptyBarrels()
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

    fun initFullRecycler(data: List<SimpleBeerRowModel>) {
        storeHouseFullBarrelsRecycler.layoutManager = LinearLayoutManager(context)
        storeHouseFullBarrelsRecycler.adapter = SimpleBeerRowAdapter(data)
    }
}
