package com.example.beerdistrkt.fragPages.sawyobi

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI

import com.example.beerdistrkt.R

class SawyobiFragment : Fragment() {

    companion object {
        fun newInstance() = SawyobiFragment()
    }

    private lateinit var viewModel: SawyobiViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.sawyobi_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(SawyobiViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.sawyobi_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.sawyobiDetail -> view!!.findNavController().navigate(SawyobiFragmentDirections.actionSawyobiFragmentToSawyobiListFragment())
        }
        return super.onOptionsItemSelected(item)
    }
}
