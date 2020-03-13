package com.example.beerdistrkt.fragPages.mitana

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.beerdistrkt.R

class AddDeliveryFragment : Fragment() {

    companion object {
        fun newInstance() = AddDeliveryFragment()
    }

    private lateinit var viewModel: AddDeliveryViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.add_delivery_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(AddDeliveryViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
