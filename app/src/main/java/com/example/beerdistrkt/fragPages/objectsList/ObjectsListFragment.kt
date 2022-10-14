package com.example.beerdistrkt.fragPages.objectsList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.beerdistrkt.R
import com.example.beerdistrkt.getViewModel

class ObjectsListFragment : Fragment() {

    companion object {
        fun newInstance() = ObjectsListFragment()
    }

    val viewModel by lazy {
        getViewModel { ObjectsListViewModel() }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.objects_list_fragment, container, false)
    }

}
