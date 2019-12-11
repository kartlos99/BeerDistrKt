package com.example.beerdistrkt.addEditObiects

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.beerdistrkt.R

class AddObjectFragment : Fragment() {

    companion object {
        fun newInstance() = AddObjectFragment()
    }

    private lateinit var viewModel: AddObjectViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.add_object_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(AddObjectViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
