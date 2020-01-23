package com.example.beerdistrkt.fragPages.objList

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.beerdistrkt.adapters.ObjListAdapter
import com.example.beerdistrkt.databinding.ObjListFragmentBinding
import com.example.beerdistrkt.db.ApeniDataBase
import com.example.beerdistrkt.models.Obieqti
import com.example.beerdistrkt.utils.ADD_ORDER
import com.example.beerdistrkt.utils.AMONAWERI
import com.example.beerdistrkt.utils.MITANA

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

        viewModel = ViewModelProviders.of(this).get(ObjListViewModel::class.java)
        vBinding.viewModel = viewModel

        val argsBundle = arguments ?: Bundle()
        val args = ObjListFragmentArgs.fromBundle(argsBundle)
        Log.d("arg", args.directionTo)

        vBinding.objListView.setOnItemClickListener { parent, view, position, id ->
            val clientObject= vBinding.objListView.adapter.getItem(position) as Obieqti
            clientObject.id?.let {
                when(args.directionTo){
                    ADD_ORDER -> vBinding.root.findNavController().navigate(ObjListFragmentDirections.actionObjListFragmentToAddOrdersFragment(it))
                    MITANA -> vBinding.root.findNavController().navigate(ObjListFragmentDirections.actionObjListFragmentToAddDeliveryFragment(it))
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
            Log.d("____size_______", it.size.toString())
            showObieqts(it)
        }
//        objListObserver.onChanged(mutableListOf(Obieqti("rame saxeli")))
//        viewModel.objList.observe(this, objListObserver)
        viewModel.obieqtsList.observe(this, objListObserver)
//        viewModel.isUpdating.observe(this, Observer {
//            if(it){
//                vBinding.progresBarObjList.visibility = View.VISIBLE
//            }else{
//                vBinding.progresBarObjList.visibility = View.GONE
//            }
//        })

    }

    private fun showObieqts(list: List<Obieqti>) {
        val listAdapter = ObjListAdapter(activity?.applicationContext, list)
        vBinding.objListView.adapter = listAdapter
    }

    fun Context.isNetAvalable(): Boolean{
        return true
    }
}
