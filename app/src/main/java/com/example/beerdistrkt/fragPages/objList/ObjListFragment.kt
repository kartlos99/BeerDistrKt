package com.example.beerdistrkt.fragPages.objList

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.example.beerdistrkt.BaseFragment
import com.example.beerdistrkt.adapters.ObjListAdapter
import com.example.beerdistrkt.databinding.ObjListFragmentBinding
import com.example.beerdistrkt.getViewModel
import com.example.beerdistrkt.models.Obieqti
import com.example.beerdistrkt.utils.ADD_ORDER
import com.example.beerdistrkt.utils.AMONAWERI
import com.example.beerdistrkt.utils.MITANA

class ObjListFragment : BaseFragment<ObjListViewModel>() {

    companion object {
        fun newInstance() = ObjListFragment()
    }

    override val viewModel by lazy {
        getViewModel { ObjListViewModel() }
    }

    private lateinit var vBinding: ObjListFragmentBinding
    private lateinit var objListAdapter: ObjListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        vBinding = ObjListFragmentBinding.inflate(inflater)
        vBinding.lifecycleOwner = this

        vBinding.viewModel = viewModel

        val argsBundle = arguments ?: Bundle()
        val args = ObjListFragmentArgs.fromBundle(argsBundle)
        Log.d("arg", args.directionTo)

        vBinding.clientListView.setOnItemClickListener { parent, view, position, id ->
            val clientObject= vBinding.clientListView.adapter.getItem(position) as Obieqti
            clientObject.id?.let {
                when(args.directionTo){
                    ADD_ORDER -> vBinding.root.findNavController().navigate(ObjListFragmentDirections.actionObjListFragmentToAddOrdersFragment(it))
                    MITANA -> vBinding.root.findNavController().navigate(ObjListFragmentDirections.actionObjListFragmentToAddDeliveryFragment(it, null))
                    AMONAWERI -> vBinding.root.findNavController().navigate(ObjListFragmentDirections.actionObjListFragmentToAmonaweriFragment(it))
//                    else -> // show toast
                }
            }
        }

        return vBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val objListObserver = Observer<List<Obieqti>> {
            Log.d("_clientList__size__", it.size.toString())
            initClientsList(it)
        }
        viewModel.clientsList.observe(viewLifecycleOwner, objListObserver)

        val filterListener = object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                showToast(query)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                objListAdapter.filter(newText)
                return false
            }
        }

        vBinding.clientSearchView.setOnQueryTextListener(filterListener)

//        objListObserver.onChanged(mutableListOf(Obieqti("rame saxeli")))
//        viewModel.objList.observe(this, objListObserver)

//        viewModel.isUpdating.observe(this, Observer {
//            if(it){
//                vBinding.progresBarObjList.visibility = View.VISIBLE
//            }else{
//                vBinding.progresBarObjList.visibility = View.GONE
//            }
//        })
    }

    private fun initClientsList(list: List<Obieqti>) {
        objListAdapter = ObjListAdapter(activity?.applicationContext, list)
        vBinding.clientListView.adapter = objListAdapter
    }
}
