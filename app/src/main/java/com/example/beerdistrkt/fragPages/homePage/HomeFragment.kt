package com.example.beerdistrkt.fragPages.homePage

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.beerdistrkt.BaseFragment

import com.example.beerdistrkt.R
import com.example.beerdistrkt.databinding.HomeFragmentBinding
import com.example.beerdistrkt.utils.AMONAWERI
import com.example.beerdistrkt.utils.MITANA
import com.example.beerdistrkt.utils.Session
import com.example.beerdistrkt.utils.visibleIf

class HomeFragment : BaseFragment<HomeViewModel>(), View.OnClickListener {

    companion object {
        fun newInstance() = HomeFragment()
    }

    private lateinit var vBinding: HomeFragmentBinding
    override val viewModel: HomeViewModel by lazy { ViewModelProviders.of(this)[HomeViewModel::class.java] }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        vBinding = HomeFragmentBinding.inflate(inflater)
//        val binding: HomeFragmentBinding = DataBindingUtil.inflate(
//            inflater, R.layout.home_fragment, container, false)

//        val application = requireNotNull(this.activity).application

        vBinding.lifecycleOwner = this

        vBinding.btnShekvetebi.setOnClickListener(this)
        vBinding.btnMitana.setOnClickListener(this)
        vBinding.btnRealizDge.setOnClickListener(this)
        vBinding.btnRealizObj.setOnClickListener(this)
        return vBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.location_ge)

        viewModel.mainLoaderLiveData.observe(viewLifecycleOwner, Observer {
            vBinding.homeMainProgressBar?.visibleIf(it)
            if (it)
                viewModel.mainLoaderLiveData.value = false
        })
    }

    override fun onResume() {
        super.onResume()
        if (!Session.get().isUserLogged())
            findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
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
