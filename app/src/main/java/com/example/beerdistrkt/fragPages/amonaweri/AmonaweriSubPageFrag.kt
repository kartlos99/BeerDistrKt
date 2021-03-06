package com.example.beerdistrkt.fragPages.amonaweri

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.AdapterView.AdapterContextMenuInfo
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.beerdistrkt.BaseFragment
import com.example.beerdistrkt.R
import com.example.beerdistrkt.adapters.AmonaweriAdapter
import com.example.beerdistrkt.databinding.AmonaweriSubPageFragmentBinding
import com.example.beerdistrkt.fragPages.mitana.AddDeliveryFragment
import com.example.beerdistrkt.fragPages.mitana.AddDeliveryFragment.Companion.K_OUT
import com.example.beerdistrkt.fragPages.mitana.AddDeliveryFragment.Companion.MITANA
import com.example.beerdistrkt.fragPages.mitana.AddDeliveryFragment.Companion.M_OUT
import com.example.beerdistrkt.models.Amonaweri
import com.example.beerdistrkt.showAskingDialog
import com.example.beerdistrkt.utils.LOCATION
import com.example.beerdistrkt.utils.OBJ_ID
import com.example.beerdistrkt.utils.Session
import com.example.beerdistrkt.utils.UserType
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class AmonaweriSubPageFrag : BaseFragment<AmonaweriSubPageViewModel>() {

    private lateinit var vBinding: AmonaweriSubPageFragmentBinding

    lateinit var simpleDateFormat: SimpleDateFormat
    lateinit var amonaweriListAdapter: AmonaweriAdapter

    private var pagePos: Int = -1

    var action: ((operation: String, recordID: Int) -> Unit)? = null
    var updateAnotherPage: (() -> Unit)? = null

    companion object {
        fun newInstance() = AmonaweriSubPageFrag()

        val TAG = "AmonaweriSubPageFrag"
    }

    override val viewModel: AmonaweriSubPageViewModel by lazy {
        ViewModelProviders.of(this)[AmonaweriSubPageViewModel::class.java]
    }

    @SuppressLint("SimpleDateFormat")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        vBinding = AmonaweriSubPageFragmentBinding.inflate(inflater)

        pagePos = arguments?.getInt(LOCATION) ?: 0
        viewModel.pagePos = pagePos
        viewModel.clientID = arguments?.getInt(OBJ_ID) ?: 0
        vBinding.progressBarAmonaweri.visibility = View.VISIBLE
        viewModel.requestAmonaweriList()

        vBinding.amoColumnTitle1.text = getString(R.string.text_tarigi)
        if (pagePos == 0) {
            vBinding.amoColumnTitle2.text = getString(R.string.price)
            vBinding.amoColumnTitle3.text = getString(R.string.pay)
            vBinding.amoColumnTitle4.text = getString(R.string.davalianeba)
        } else {
            vBinding.amoColumnTitle2.text = getString(R.string.mitanili_kasrebi)
            vBinding.amoColumnTitle3.text = getString(R.string.wamogebuli_kasrebi)
            vBinding.amoColumnTitle4.text = getString(R.string.nashti_at_client)
        }
        registerForContextMenu(vBinding.listviewAmonaweri)

        return vBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.amonaweriLiveData.observe(viewLifecycleOwner, Observer {
            vBinding.progressBarAmonaweri.visibility = View.GONE
            amonaweriListAdapter = AmonaweriAdapter(context, it, pagePos, viewModel.isGrouped)
            vBinding.listviewAmonaweri.adapter = amonaweriListAdapter
            Log.d("sufrObsSize", "${it.size}")
            Log.d("suAdaper size", "${amonaweriListAdapter.count}")
        })
        viewModel.needUpdateLiveData.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                updateAnotherPage?.invoke()
                viewModel.needUpdateLiveData.value = null
            }
        })

        vBinding.listviewAmonaweri.setOnItemClickListener { parent, view, position, id ->
            if (!viewModel.isGrouped) {
                val tvComment = view.findViewById<TextView>(R.id.t_amonaweri_row_comment)
                val amonaweri: Amonaweri = amonaweriListAdapter.getItem(position)
                if (!amonaweri.comment.isNullOrEmpty()) {
                    if (tvComment.visibility == View.VISIBLE) {
                        tvComment.visibility = View.GONE
                    } else {
                        tvComment.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    fun chengeAmonaweriAppearance(grouped: Boolean) {
        viewModel.changeDataStructure(grouped)
    }



    override fun onCreateContextMenu(
        menu: ContextMenu,
        v: View,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        if (viewModel.isGrouped) {
            showToast(R.string.remove_grouping)
        } else {
            val info = menuInfo as AdapterContextMenuInfo

            var adate: Date? = null
            try {
                adate = SimpleDateFormat(getString(R.string.patern_datetime))
                    .parse(amonaweriListAdapter.getItem(info.position).tarigi)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            if (adate != null) {
                val dateFormat = SimpleDateFormat(getString(R.string.patern_date))
                if (Session.get().userType == UserType.ADMIN ||
                    dateFormat.format(adate) == dateFormat.format(Date())
                ) {
                    if (pagePos == 0) {
                        activity!!.menuInflater.inflate(R.menu.context_menu_amonaw_m, menu)
                        menu.setHeaderTitle("--  თანხები  --")
                    }
                    if (pagePos == 1) {
                        activity!!.menuInflater.inflate(R.menu.context_menu_amonaw_k, menu)
                        menu.setHeaderTitle("--  კასრები  --")
                    }
                } else {
                    Toast.makeText(context, R.string.no_edit_access, Toast.LENGTH_SHORT).show()
                }
            }
        }
        super.onCreateContextMenu(menu, v, menuInfo)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {

        val info = item.menuInfo as AdapterContextMenuInfo
//        val amonaweriRow = amonaweriListAdapter.getItem(info.position)

        when (item.itemId) {
            R.id.cm_amonaw_m_edit -> if (pagePos == 0) {
                val amonaweriRow = amonaweriListAdapter.getItem(info.position)
                // tanxis agebis redaqtireba
                if (amonaweriRow.pay != 0F)
                    action?.invoke(M_OUT, amonaweriRow.id)
                else
                    action?.invoke(MITANA, amonaweriRow.id)
            }
            R.id.cm_amonaw_k_edit -> if (pagePos == 1) {
                val amonaweriRow = amonaweriListAdapter.getItem(info.position)
                if (amonaweriRow.k_out != 0F) { // kasris aRebas vakoreqtireb
                    action?.invoke(K_OUT, amonaweriRow.id)
                } else
                    action?.invoke(MITANA, amonaweriRow.id)
            }
            R.id.cm_amonaw_m_del -> if (pagePos == 0) {
                val amonaweriRow = amonaweriListAdapter.getItem(info.position)
                requireContext().showAskingDialog(
                    null,
                    R.string.confirm_delete_text,
                    R.string.yes,
                    R.string.no,
                    R.style.ThemeOverlay_MaterialComponents_Dialog
                ) {
                    val tableName = if (amonaweriRow.pay == 0F) MITANA else M_OUT
                    viewModel.deleteRecord(tableName, amonaweriRow.id)
                }
            }
            R.id.cm_amonaw_k_del -> if (pagePos == 1) {
                val amonaweriRow = amonaweriListAdapter.getItem(info.position)
                requireContext().showAskingDialog(
                    null,
                    R.string.confirm_delete_text,
                    R.string.yes,
                    R.string.no,
                    R.style.ThemeOverlay_MaterialComponents_Dialog
                ) {
                    val tableName = if (amonaweriRow.k_out == 0F) MITANA else K_OUT
                    viewModel.deleteRecord(tableName, amonaweriRow.id)
                }
            }
        }

        amonaweriListAdapter.notifyDataSetChanged()
        return super.onContextItemSelected(item)
    }

    fun updateData() {
        viewModel.requestAmonaweriList()
    }
}
