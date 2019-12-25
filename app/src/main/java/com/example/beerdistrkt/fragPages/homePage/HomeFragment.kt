package com.example.beerdistrkt.fragPages.homePage

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController

import com.example.beerdistrkt.R
import com.example.beerdistrkt.databinding.HomeFragmentBinding
import com.example.beerdistrkt.db.ApeniDataBase
import com.example.beerdistrkt.fragPages.homePage.HomeFragmentDirections
import com.example.beerdistrkt.utils.AMONAWERI
import com.example.beerdistrkt.utils.MDEBAREOBA
import com.example.beerdistrkt.utils.MITANA

class HomeFragment : Fragment(), View.OnClickListener {

    companion object {
        fun newInstance() = HomeFragment()
    }

    private lateinit var vBinding: HomeFragmentBinding
    private lateinit var viewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        vBinding = HomeFragmentBinding.inflate(inflater)
//        val binding: HomeFragmentBinding = DataBindingUtil.inflate(
//            inflater, R.layout.home_fragment, container, false)

        val application = requireNotNull(this.activity).application

        val dataSource = ApeniDataBase.getInstance(application).apeniDataBaseDao

        val viewModelFactory = HomeViewModelFactory(dataSource, application)

        val HomeViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(HomeViewModel::class.java)

        vBinding.setLifecycleOwner(this)

//        vBinding.sleepTrackerViewModel = sleepTrackerViewModel

        vBinding.btnShekvetebi.setOnClickListener(this)
        vBinding.btnMitana.setOnClickListener(this)
        vBinding.btnRealizDge.setOnClickListener(this)
        vBinding.btnRealizObj.setOnClickListener(this)
        return vBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onClick(view: View?) {
        when(view?.id){
            R.id.btn_shekvetebi -> {
                view.findNavController().navigate(R.id.action_homeFragment_to_ordersFragment)
            }
            vBinding.btnMitana.id -> {
                view.findNavController().navigate(
                    HomeFragmentDirections.actionHomeFragmentToObjListFragment(
                    MITANA))
            }
            vBinding.btnRealizDge.id -> {
                view.findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToSalesFragment())
            }
            vBinding.btnRealizObj.id -> {
                view.findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToObjListFragment(
                    AMONAWERI))
            }
        }
    }


}
