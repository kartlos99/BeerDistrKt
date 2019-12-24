package com.example.beerdistrkt.fragPages.objectsList

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.beerdistrkt.R

class ObjectsListFragment : Fragment() {

    companion object {
        fun newInstance() = ObjectsListFragment()
    }

    private lateinit var viewModel: ObjectsListViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.objects_list_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ObjectsListViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
