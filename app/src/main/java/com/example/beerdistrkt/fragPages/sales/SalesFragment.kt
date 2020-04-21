package com.example.beerdistrkt.fragPages.sales

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.beerdistrkt.BaseFragment
import com.example.beerdistrkt.R
import com.example.beerdistrkt.adapters.SalesAdapter
import com.example.beerdistrkt.customView.XarjiRowView
import com.example.beerdistrkt.databinding.SalesFragmentBinding
import com.example.beerdistrkt.dialogs.XarjebiDialog
import com.example.beerdistrkt.getViewModel
import com.example.beerdistrkt.models.DeleteRequest
import com.example.beerdistrkt.utils.*
import java.lang.String
import java.text.SimpleDateFormat
import java.util.*

class SalesFragment : BaseFragment<SalesViewModel>() {

    companion object {
        fun newInstance() = SalesFragment()
        const val TAG = "Sales Frag"
    }

    private lateinit var vBinding: SalesFragmentBinding
    override val viewModel: SalesViewModel by lazy {
        getViewModel<SalesViewModel>()
//        ViewModelProviders.of(this)[SalesViewModel::class.java]
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

        vBinding.btnTarigi.setOnClickListener {
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

        vBinding.btnXarjExpand.setOnClickListener {
            viewModel.btnXarjExpandClick()
        }

        context?.let {
            vBinding.btnXarjebi.setOnClickListener {
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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.salesLiveData.observe(viewLifecycleOwner, Observer {
            val adapter = SalesAdapter(context, it)
            vBinding.salesList1.adapter = adapter
            fillPageData()
        })

        viewModel.usersLiveData.observe(viewLifecycleOwner, Observer {
            viewModel.formUserMap(it)
        })

        viewModel.deleteXarjiLiveData.observe(viewLifecycleOwner, Observer {
            when (it.result) {
                SUCCESS -> {
                    showToast(getString(R.string.msg_record_deleted))
                    showXarjList(true)
                    fillPageData()
                    viewModel.deleteXarjiComplited()
                }
                ERROR -> {
                    showToast(getString(R.string.msg_record_not_deleted))
                }
            }
        })

        viewModel.xarjiListExpandedLiveData.observe(viewLifecycleOwner, Observer {
            showXarjList(it)
        })

        viewModel.addXarjiLiveData.observe(viewLifecycleOwner, Observer {
            when (it.result) {
                SUCCESS -> {
                    showXarjList(true)
                    fillPageData()
                    showToast(getString(R.string.msg_record_added))
                    viewModel.addXarjiComplited()
                }
                ERROR -> {
                    showToast(getString(R.string.msg_record_not_added))
                }
            }
        })
    }

    fun fillPageData() {

        vBinding.tP3K30Count.text = String.format(
            "%s\n%s",
            MyUtil.floatToSmartStr(viewModel.k3r.toFloat()),
            MyUtil.floatToSmartStr(viewModel.k30empty)
        )

        vBinding.tP3K50Count.text = String.format(
            "%s\n%s",
            MyUtil.floatToSmartStr(viewModel.k5r.toFloat()),
            MyUtil.floatToSmartStr(viewModel.k50empty)
        )

        val xarjiSum = viewModel.xarjebi.sumByDouble { it.amount.toDouble() }
        vBinding.tP3LariCount.text = resources.getString(R.string.format_gel, viewModel.priceSum)
        vBinding.tXarjSum.text = resources.getString(R.string.format_gel, xarjiSum)
        vBinding.tXelze.text = resources.getString(
            R.string.format_gel,
            viewModel.realizationDayLiveData.value?.output?.money?.minus(xarjiSum)
        )

    }

    private fun showXarjList(expanded: Boolean) {
        vBinding.btnXarjExpand.setImageDrawable(resources.getDrawable(if (expanded) R.drawable.ic_arrow_up_24dp else R.drawable.ic_arrow_down_24dp))
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
}
