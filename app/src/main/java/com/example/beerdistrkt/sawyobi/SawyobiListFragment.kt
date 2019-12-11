package com.example.beerdistrkt.sawyobi

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.beerdistrkt.R

class SawyobiListFragment : Fragment() {

    companion object {
        fun newInstance() = SawyobiListFragment()
    }

    private lateinit var viewModel: SawyobiListViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.sawyobi_list_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(SawyobiListViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
