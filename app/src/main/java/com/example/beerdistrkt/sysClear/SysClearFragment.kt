package com.example.beerdistrkt.sysClear

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.beerdistrkt.R

class SysClearFragment : Fragment() {

    companion object {
        fun newInstance() = SysClearFragment()
    }

    private lateinit var viewModel: SysClearViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.sys_clear_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(SysClearViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
