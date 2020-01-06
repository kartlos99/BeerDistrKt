package com.example.beerdistrkt.fragPages.orders

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.beerdistrkt.R
import com.example.beerdistrkt.db.ApeniDataBase
import com.example.beerdistrkt.db.ApeniDatabaseDao
import com.example.beerdistrkt.network.ApeniApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class AddOrdersFragment : Fragment() {

    companion object {
        fun newInstance() = AddOrdersFragment()
    }

    private lateinit var viewModel: AddOrdersViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.add_orders_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(AddOrdersViewModel::class.java)

        val argsBundle = arguments ?: Bundle()
        val args = AddOrdersFragmentArgs.fromBundle(argsBundle)
        Log.d("arg", "objID ${args.clientObjectID}")
        viewModel.logObjPr(args.clientObjectID)
    }

}
