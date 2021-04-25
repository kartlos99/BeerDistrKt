package com.example.beerdistrkt.fragPages.amonaweri

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.beerdistrkt.BaseFragment
import com.example.beerdistrkt.R
import com.example.beerdistrkt.adapters.MyPagesAdapter
import com.example.beerdistrkt.databinding.AmonaweriFragmentBinding
import com.example.beerdistrkt.fragPages.showHistory.SalesHistoryFragment
import com.example.beerdistrkt.fragPages.showHistory.SalesHistoryFragment.Companion.KEY_HISTORY_OF
import com.example.beerdistrkt.fragPages.showHistory.SalesHistoryFragment.Companion.KEY_RECORD_ID
import com.example.beerdistrkt.getViewModel
import java.text.SimpleDateFormat

class AmonaweriFragment : BaseFragment<AmonaweriViewModel>() {

    companion object {
        fun newInstance() = AmonaweriFragment()
        val TAG = "AmonaweriFragment"
    }

    val frag = this

    override val viewModel by lazy {
        getViewModel { AmonaweriViewModel(clientID) }
    }
    private val clientID by lazy {
        val args = AmonaweriFragmentArgs.fromBundle(arguments ?: Bundle())
        args.clientObjectID
    }

    private lateinit var vBinding: AmonaweriFragmentBinding
    lateinit var simpleDateFormat: SimpleDateFormat

    var pagesAdapter: MyPagesAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        vBinding = AmonaweriFragmentBinding.inflate(inflater)
        vBinding.lifecycleOwner = this //viewLifecycleOwner

        return vBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pagesAdapter = MyPagesAdapter(childFragmentManager, clientID)
        vBinding.viewpagerAmonaweri.adapter = pagesAdapter
        vBinding.tabsAmonaweri.setupWithViewPager(vBinding.viewpagerAmonaweri)

        vBinding.chkGrAmonaweri.setOnCheckedChangeListener { buttonView, isChecked ->
            pagesAdapter?.fragmentM?.chengeAmonaweriAppearance(isChecked)
            pagesAdapter?.fragmentK?.chengeAmonaweriAppearance(isChecked)
            if (pagesAdapter?.fragmentM?.action == null) {
                pagesAdapter?.fragmentM?.action = ::goEditing
                pagesAdapter?.fragmentK?.action = ::goEditing
                pagesAdapter?.fragmentM?.onShowHistory = ::showHistory
            }
            if (pagesAdapter?.fragmentM?.updateAnotherPage == null) {
                pagesAdapter?.fragmentM?.updateAnotherPage = {
                    pagesAdapter?.fragmentK?.updateData()
                }
                pagesAdapter?.fragmentK?.updateAnotherPage = {
                    pagesAdapter?.fragmentM?.updateData()
                }
            }
        }

        viewModel.clientLiveData.observe(viewLifecycleOwner, Observer {
            (activity as AppCompatActivity).supportActionBar?.title = it.obieqti.dasaxeleba
        })
    }

    private fun showHistory(recordID: Int, historyOf: String) {
        val args = Bundle().apply {
            putInt(KEY_RECORD_ID, recordID)
            putString(KEY_HISTORY_OF, historyOf)
        }
        frag.findNavController()
            .navigate(R.id.action_amonaweriFragment_to_salesHistoryFragment, args)
    }

    private fun goEditing(operation: String, recordID: Int) {
        val action = AmonaweriFragmentDirections
            .actionAmonaweriFragmentToAddDeliveryFragment(clientID, operation)
        action.recordID = recordID
        frag.findNavController().navigate(action)
    }

    private fun setTabsTitle(title_0: String, title_1: String) {
//        val tab0: TabLayout.Tab = tabLayout.getTabAt(0)
//        val tab1: TabLayout.Tab = tabLayout.getTabAt(1)
//        if (tab0 != null && tab1 != null) {
//            tab0.setText(title_0)
//            tab1.setText(title_1)
//        }
    }

}
