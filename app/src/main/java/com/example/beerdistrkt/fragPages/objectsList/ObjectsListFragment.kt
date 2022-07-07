package com.example.beerdistrkt.fragPages.objectsList

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.beerdistrkt.R
import com.example.beerdistrkt.getViewModel

class ObjectsListFragment : Fragment() {

    companion object {
        fun newInstance() = ObjectsListFragment()
    }

    private val viewModel by lazy {
        getViewModel { ObjectsListViewModel() }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.objects_list_fragment, container, false)
    }

}
