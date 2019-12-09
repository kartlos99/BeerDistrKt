package com.example.beerdistrkt.fragment

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI

import com.example.beerdistrkt.R
import com.example.beerdistrkt.databinding.HomeFragmentBinding
import com.example.beerdistrkt.viewmodel.HomeViewModel

class HomeFragment : Fragment() {

    companion object {
        fun newInstance() = HomeFragment()
    }

    private lateinit var viewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val vBinding = HomeFragmentBinding.inflate(inflater)
//        val binding: HomeFragmentBinding = DataBindingUtil.inflate(
//            inflater, R.layout.home_fragment, container, false)

        vBinding.btnShekvetebi.setOnClickListener { view: View ->
            view.findNavController().navigate(R.id.action_homeFragment_to_ordersFragment)
//            Navigation.createNavigateOnClickListener(R.id.action_homeFragment_to_ordersFragment)
        }
        vBinding.btnMitana.setOnClickListener {
            it.findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToOrdersFragment())
        }
        
        return vBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
