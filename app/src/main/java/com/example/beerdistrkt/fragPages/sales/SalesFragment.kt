package com.example.beerdistrkt.fragPages.sales

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.beerdistrkt.R
import com.example.beerdistrkt.adapters.SalesAdapter
import com.example.beerdistrkt.customView.XarjiRowView
import com.example.beerdistrkt.databinding.SalesFragmentBinding
import com.example.beerdistrkt.models.DeleteRequest
import com.example.beerdistrkt.utils.MyUtil
import com.example.beerdistrkt.utils.SUCCESS
import com.example.beerdistrkt.utils.Session
import com.example.beerdistrkt.utils.UserType
import java.lang.String
import java.text.SimpleDateFormat
import java.util.*

class SalesFragment : Fragment() {
    val dateFormat_desh = SimpleDateFormat("yyyy-MM-dd")

    companion object {
        fun newInstance() = SalesFragment()
    }

    private lateinit var vBinding: SalesFragmentBinding
    private lateinit var viewModel: SalesViewModel

    private var xarjListExpanded = false

    var dateSetListener = OnDateSetListener { _, year, month, day ->
        viewModel.onDataSelected(year, month, day)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        vBinding = SalesFragmentBinding.inflate(inflater)
        viewModel = ViewModelProviders.of(this).get(SalesViewModel::class.java)

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
            if (it.result == SUCCESS){
                Toast.makeText(context, "waisala", Toast.LENGTH_SHORT).show()
                showXarjList(true)
                fillPageData()
            }else{
                Toast.makeText(context, "ver waiSala!! ${it.error}", Toast.LENGTH_SHORT).show()
            }
        })

        viewModel.xarjiListExpandedLiveData.observe(viewLifecycleOwner, Observer {
            vBinding.btnXarjExpand.setImageDrawable(resources.getDrawable(if (it) R.drawable.ic_arrow_up_24dp else R.drawable.ic_arrow_down_24dp))
            showXarjList(it)
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
        vBinding.linearXarjebi.removeAllViews()
        var canDel = false
        if (Session.get().userType == UserType.ADMIN || viewModel.selectedDayLiveData.value == dateFormat_desh.format(Date())) {
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
                ){
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
