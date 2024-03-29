package com.example.beerdistrkt.fragPages.amonaweri

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.beerdistrkt.BaseFragment
import com.example.beerdistrkt.R
import com.example.beerdistrkt.adapters.MyPagesAdapter
import com.example.beerdistrkt.common.fragments.ClientDebtFragment
import com.example.beerdistrkt.databinding.AmonaweriFragmentBinding
import com.example.beerdistrkt.fragPages.showHistory.SalesHistoryFragment.Companion.KEY_HISTORY_OF
import com.example.beerdistrkt.fragPages.showHistory.SalesHistoryFragment.Companion.KEY_RECORD_ID
import com.example.beerdistrkt.getViewModel
import java.text.SimpleDateFormat

class AmonaweriFragment : BaseFragment<AmonaweriViewModel>() {

    companion object {
        fun newInstance() = AmonaweriFragment()
        const val TAG = "AmonaweriFragment"
    }

    val frag = this

    override val viewModel by lazy {
        getViewModel { AmonaweriViewModel(clientID) }
    }
    private val clientID by lazy {
        val args = AmonaweriFragmentArgs.fromBundle(arguments ?: Bundle())
        args.clientObjectID
    }

    private val vBinding by viewBinding(AmonaweriFragmentBinding::bind)
    lateinit var simpleDateFormat: SimpleDateFormat

    var pagesAdapter: MyPagesAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.amonaweri_fragment, container, false)
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
                    showDebt()
                    pagesAdapter?.fragmentK?.updateData()
                }
                pagesAdapter?.fragmentK?.updateAnotherPage = {
                    showDebt()
                    pagesAdapter?.fragmentM?.updateData()
                }
            }
        }

        viewModel.clientLiveData.observe(viewLifecycleOwner, Observer {
            vBinding.fragStatementClientInfo.text = it.obieqti.dasaxeleba
        })
        showDebt()
    }

    private fun showDebt() {
        val debtFragment = ClientDebtFragment.getInstance(clientID)
        childFragmentManager.beginTransaction()
            .replace(R.id.fragStatementDebtContainer, debtFragment)
            .commit()
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
            .actionAmonaweriFragmentToAddDeliveryFragment(clientID, operation, 0, recordID)
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
