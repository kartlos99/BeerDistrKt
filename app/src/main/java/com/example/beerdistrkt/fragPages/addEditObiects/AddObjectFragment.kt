package com.example.beerdistrkt.fragPages.addEditObiects

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.beerdistrkt.BaseFragment
import com.example.beerdistrkt.R
import com.example.beerdistrkt.getViewModel

class AddObjectFragment : BaseFragment<AddObjectViewModel>() {

    companion object {
        fun newInstance() = AddObjectFragment()
    }

    override val viewModel by lazy {
        getViewModel { AddObjectViewModel() }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.add_object_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // TODO: Use the ViewModel
    }

}
