package com.example.beerdistrkt.fragPages.homePage

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

        vBinding.btnOrder.setOnClickListener(this)
        vBinding.btnSaleResult.setOnClickListener(this)
        vBinding.btnSalesByClient.setOnClickListener(this)
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
            R.id.btnOrder -> {
                view.findNavController().navigate(R.id.action_homeFragment_to_ordersFragment)
            }
            R.id.btnSaleResult -> {
                view.findNavController()
                    .navigate(HomeFragmentDirections.actionHomeFragmentToSalesFragment())
            }
            R.id.btnSalesByClient -> {
                view.findNavController()
                    .navigate(HomeFragmentDirections.actionHomeFragmentToObjListFragment(AMONAWERI))
            }
        }
    }


}
