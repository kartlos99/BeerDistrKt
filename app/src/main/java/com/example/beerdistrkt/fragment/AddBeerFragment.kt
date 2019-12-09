package com.example.beerdistrkt.fragment

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.beerdistrkt.R
import com.example.beerdistrkt.viewmodel.AddBeerViewModel

class AddBeerFragment : Fragment() {

    companion object {
        fun newInstance() = AddBeerFragment()
    }

    private lateinit var viewModel: AddBeerViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.add_beer_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(AddBeerViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
