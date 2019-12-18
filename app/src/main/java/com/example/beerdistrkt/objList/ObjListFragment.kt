package com.example.beerdistrkt.objList

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.beerdistrkt.adapters.ObjListAdapter
import com.example.beerdistrkt.databinding.ObjListFragmentBinding
import com.example.beerdistrkt.db.ApeniDataBase
import com.example.beerdistrkt.models.Obieqti

class ObjListFragment : Fragment() {

    companion object {
        fun newInstance() = ObjListFragment()
    }

    private lateinit var viewModel: ObjListViewModel
    private lateinit var vBinding: ObjListFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        vBinding = ObjListFragmentBinding.inflate(inflater)
        vBinding.lifecycleOwner = this

        val application = requireNotNull(this.activity).application

        val dataSource = ApeniDataBase.getInstance(application).apeniDataBaseDao

        val viewModelFactory = ObjListViewModelFactory(dataSource, application)

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(ObjListViewModel::class.java)
        vBinding.viewModel = viewModel

        return vBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val objListObserver = Observer<List<Obieqti>> {
            Log.d("____size_______", it.size.toString())
            showObieqts(it)
        }
        viewModel.obieqtsList.observe(this, objListObserver)
    }

    private fun showObieqts(list: List<Obieqti>) {
        val listAdapter = ObjListAdapter(activity?.applicationContext, list)
        vBinding.objListView.adapter = listAdapter
    }
}
