package com.example.beerdistrkt.fragPages.sales

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.beerdistrkt.BaseFragment
import com.example.beerdistrkt.R
import com.example.beerdistrkt.adapters.SalesAdapter
import com.example.beerdistrkt.customView.XarjiRowView
import com.example.beerdistrkt.databinding.SalesFragmentBinding
import com.example.beerdistrkt.dialogs.XarjebiDialog
import com.example.beerdistrkt.fragPages.sales.adapter.BarrelsIOAdapter
import com.example.beerdistrkt.getViewModel
import com.example.beerdistrkt.models.BarrelIO
import com.example.beerdistrkt.models.DeleteRequest
import com.example.beerdistrkt.utils.*
import java.util.*

class SalesFragment : BaseFragment<SalesViewModel>(), AdapterView.OnItemSelectedListener {

    companion object {
        fun newInstance() = SalesFragment()
        const val TAG = "Sales Frag"
    }

    private lateinit var vBinding: SalesFragmentBinding
    override val viewModel: SalesViewModel by lazy {
        getViewModel<SalesViewModel>()
    }

    var dateSetListener = OnDateSetListener { _, year, month, day ->
        viewModel.onDataSelected(year, month, day)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        vBinding = SalesFragmentBinding.inflate(inflater)

        vBinding.viewModel = viewModel
        vBinding.lifecycleOwner = this

        vBinding.salesSetDateBtn.setOnClickListener {
            context?.let {
                val datePickerDialog = DatePickerDialog(
                    it,
                    dateSetListener,
                    viewModel.calendar.get(Calendar.YEAR),
                    viewModel.calendar.get(Calendar.MONTH),
                    viewModel.calendar.get(Calendar.DAY_OF_MONTH)
                )
                datePickerDialog.setCancelable(false)
                datePickerDialog.show()
            }
        }

        vBinding.salesExpandXarjiBtn.setOnClickListener {
            viewModel.btnXarjExpandClick()
        }

        context?.let {
            vBinding.salesAddXarjiBtn.setOnClickListener {
                val xarjebiDialog = XarjebiDialog(it.context) { comment, amount ->
                    if (amount.isEmpty()) {
                        showToast(getString(R.string.msg_empty_not_saved))
                    } else {
                        try {
                            val am = amount.toFloat()
                            viewModel.addXarji(comment, am.toString())
                        } catch (e: NumberFormatException) {
                            Log.d(TAG, e.toString())
                            showToast(getString(R.string.msg_invalid_format))
                        }
                    }
                }
                xarjebiDialog.show(childFragmentManager, "xarjidialogTag")
            }
        }

        return vBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.title =
            resources.getString(R.string.day_realizacia)

        vBinding.salesDistributorsSpinner.adapter = ArrayAdapter(
            requireContext(),
            R.layout.simple_dropdown_item,
            viewModel.usersList.map { it.name }
        )
        vBinding.salesDistributorsSpinner.onItemSelectedListener = this

        initViewModel()
    }

    fun initViewModel() {
        viewModel.salesLiveData.observe(viewLifecycleOwner, Observer {
            val adapter = SalesAdapter(context, it)
            vBinding.salesList1.adapter = adapter
            fillPageData()
        })

        viewModel.usersLiveData.observe(viewLifecycleOwner, Observer {
            viewModel.formUserMap(it)
        })

        viewModel.deleteXarjiLiveData.observe(viewLifecycleOwner, Observer {
            when (it) {
                is ApiResponseState.Success -> {
                    showToast(getString(R.string.msg_record_deleted))
                    showXarjList(true)
                    fillPageData()
                }
                is ApiResponseState.ApiError -> {
                    showToast(getString(R.string.msg_record_not_deleted))
                }
            }
            viewModel.deleteXarjiComplited()
        })

        viewModel.xarjiListExpandedLiveData.observe(viewLifecycleOwner, Observer {
            showXarjList(it)
        })

        viewModel.addXarjiLiveData.observe(viewLifecycleOwner, Observer {
            when (it) {
                is ApiResponseState.Success -> {
                    showXarjList(true)
                    fillPageData()
                    showToast(getString(R.string.msg_record_added))
                }
                is ApiResponseState.ApiError -> {
                    showToast(getString(R.string.msg_record_not_added))
                }
            }
            viewModel.addXarjiComplited()
        })

        viewModel.barrelsLiveData.observe(viewLifecycleOwner, Observer {
            initBarrelBlock(it)
        })
    }

    fun fillPageData() {


        val xarjiSum = viewModel.xarjebi.sumByDouble { it.amount.toDouble() }
        vBinding.salesSumPrice.text = resources.getString(R.string.format_gel, viewModel.priceSum)
        vBinding.salesSumXarji.text = resources.getString(R.string.format_gel, xarjiSum)
        vBinding.salesAmountAtHand.text = resources.getString(
            R.string.format_gel,
            viewModel.realizationDayLiveData.value?.takenMoney?.minus(xarjiSum)
        )

    }

    fun initBarrelBlock(data: List<BarrelIO>) {
        vBinding.salesBarrelRecycler.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )
        vBinding.salesBarrelRecycler.adapter = BarrelsIOAdapter(data)
    }

    private fun showXarjList(expanded: Boolean) {
        vBinding.salesExpandXarjiBtn.setImageDrawable(resources.getDrawable(if (expanded) R.drawable.ic_arrow_up_24dp else R.drawable.ic_arrow_down_24dp))
        vBinding.linearXarjebi.removeAllViews()
        var canDel = false
        if (Session.get().userType == UserType.ADMIN ||
            viewModel.selectedDayLiveData.value == dateFormatDash.format(Date())
        ) {
            canDel = true
        }

        Log.d("XARJEBI", viewModel.xarjebi.toString())
        if (expanded) {
            for (xarji in viewModel.xarjebi) {
                val row = XarjiRowView(
                    context,
                    xarji,
                    viewModel.userMap[xarji.distrID]!![0].username,
                    viewModel.xarjebi,
                    vBinding.linearXarjebi,
                    canDel
                ) {
                    viewModel.deleteXarji(
                        DeleteRequest(
                            it,
                            "xarjebi",
                            Session.get().userID!!
                        )
                    )
                }
                vBinding.linearXarjebi.addView(row)
            }
            vBinding.scrollMain.post {
                vBinding.scrollMain.smoothScrollTo(
                    0,
                    vBinding.scrollMain.bottom
                )
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        viewModel.selectedDistributorID = viewModel.usersList[position].id.toInt()
        viewModel.prepareData()
    }
}
