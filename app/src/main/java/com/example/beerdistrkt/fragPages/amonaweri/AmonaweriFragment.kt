package com.example.beerdistrkt.fragPages.amonaweri

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.example.beerdistrkt.adapters.MyPagesAdapter
import com.example.beerdistrkt.databinding.AmonaweriFragmentBinding
import java.text.SimpleDateFormat

class AmonaweriFragment : Fragment() {

    companion object {
        fun newInstance() = AmonaweriFragment()
        val TAG = "AmonaweriFragment"
    }

    val frag = this

    private lateinit var vBinding: AmonaweriFragmentBinding
    private lateinit var viewModel: AmonaweriViewModel
    lateinit var simpleDateFormat: SimpleDateFormat
    var clientID = 0

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
        viewModel = ViewModelProviders.of(this).get(AmonaweriViewModel::class.java)

        val argsBundle = arguments ?: Bundle()
        val args = AmonaweriFragmentArgs.fromBundle(argsBundle)
        clientID = args.clientObjectID

        pagesAdapter = MyPagesAdapter(childFragmentManager, clientID)
        vBinding.viewpagerAmonaweri.adapter = pagesAdapter
        vBinding.tabsAmonaweri.setupWithViewPager(vBinding.viewpagerAmonaweri)

        vBinding.btnP4Tarigi.setOnClickListener {
//            val fr1 = pagesAdapter?.fragmentM

        }

        vBinding.chkGrAmonaweri.setOnCheckedChangeListener { buttonView, isChecked ->
            pagesAdapter?.fragmentM?.chengeAmonaweriAppearance(isChecked)
            pagesAdapter?.fragmentK?.chengeAmonaweriAppearance(isChecked)
            if (pagesAdapter?.fragmentM?.action == null) {
                pagesAdapter?.fragmentM?.action = ::goEditing
                pagesAdapter?.fragmentK?.action = ::goEditing
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
