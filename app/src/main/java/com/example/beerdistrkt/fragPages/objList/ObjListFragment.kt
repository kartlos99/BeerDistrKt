package com.example.beerdistrkt.fragPages.objList

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.beerdistrkt.BaseFragment
import com.example.beerdistrkt.databinding.ObjListFragmentBinding
import com.example.beerdistrkt.fragPages.objList.adapters.ClientsListAdapter
import com.example.beerdistrkt.getViewModel
import com.example.beerdistrkt.models.Obieqti
import com.example.beerdistrkt.utils.ADD_ORDER
import com.example.beerdistrkt.utils.AMONAWERI
import com.example.beerdistrkt.utils.MITANA
import java.util.*

class ObjListFragment : BaseFragment<ObjListViewModel>() {

    companion object {
        fun newInstance() = ObjListFragment()
    }

    override val viewModel by lazy {
        getViewModel { ObjListViewModel() }
    }

    private lateinit var vBinding: ObjListFragmentBinding

    private var clientListAdapter = ClientsListAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        vBinding = ObjListFragmentBinding.inflate(inflater)
        vBinding.lifecycleOwner = this

        vBinding.viewModel = viewModel

        return vBinding.root
    }

    fun navigateTo(clientID: Int) {
        val argsBundle = arguments ?: Bundle()
        val args = ObjListFragmentArgs.fromBundle(argsBundle)
        when (args.directionTo) {
            ADD_ORDER -> vBinding.root.findNavController().navigate(ObjListFragmentDirections.actionObjListFragmentToAddOrdersFragment(clientID))
            MITANA -> vBinding.root.findNavController().navigate(ObjListFragmentDirections.actionObjListFragmentToAddDeliveryFragment(clientID,null))
            AMONAWERI -> vBinding.root.findNavController().navigate(ObjListFragmentDirections.actionObjListFragmentToAmonaweriFragment(clientID))
//                    else -> // show toast
        }

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
                clientListAdapter.filter(newText)
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
        vBinding.clientsRecycler.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        vBinding.clientsRecycler.setHasFixedSize(true)

        clientListAdapter.setData(list)
        clientListAdapter.onItemClick = ::navigateTo
        vBinding.clientsRecycler.adapter = clientListAdapter

        val touchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val sourcePosition = viewHolder.adapterPosition
                val targetPosition = target.adapterPosition
                Log.d("drag size2", "${list.size}")
                Collections.swap(clientListAdapter.showingList, sourcePosition, targetPosition)
                clientListAdapter.notifyItemMoved(sourcePosition, targetPosition)
                Log.d("drag", "$sourcePosition to $targetPosition")
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

            }

        })

        touchHelper.attachToRecyclerView(vBinding.clientsRecycler)
    }
}
