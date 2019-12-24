package com.example.beerdistrkt.fragPages.amonaweri

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.beerdistrkt.R

class AmonaweriFragment : Fragment() {

    companion object {
        fun newInstance() = AmonaweriFragment()
    }

    private lateinit var viewModel: AmonaweriViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.amonaweri_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(AmonaweriViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
