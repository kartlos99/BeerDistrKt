package com.example.beerdistrkt.fragPages.objList

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.beerdistrkt.*
import com.example.beerdistrkt.databinding.ObjListFragmentBinding
import com.example.beerdistrkt.fragPages.objList.adapters.ClientsListAdapter
import com.example.beerdistrkt.utils.ADD_ORDER
import com.example.beerdistrkt.utils.AMONAWERI
import com.example.beerdistrkt.utils.MITANA
import com.example.beerdistrkt.utils.onTextChanged

class ObjListFragment : BaseFragment<ObjListViewModel>() {

    override val viewModel by lazy {
        getViewModel { ObjListViewModel() }
    }

    private lateinit var vBinding: ObjListFragmentBinding

    private var clientListAdapter = ClientsListAdapter()

    var clientPhone: String? = null

    private lateinit var searchView: SearchView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        vBinding = ObjListFragmentBinding.inflate(inflater)
        return vBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)
        initClientsRecycler()
        setupObservers()
    }

    private fun setupObservers() {

        viewModel.customersLiveData.observe(viewLifecycleOwner, Observer {
            clientListAdapter.submitList(it)
        })
    }

    private fun navigateTo(clientID: Int) {
        searchView.setOnQueryTextListener(null)
        val argsBundle = arguments ?: Bundle()
        val args = ObjListFragmentArgs.fromBundle(argsBundle)
        when (args.directionTo) {
            ADD_ORDER -> vBinding.root.findNavController().navigate(
                ObjListFragmentDirections
                    .actionObjListFragmentToAddOrdersFragment(clientID)
            )
            MITANA -> vBinding.root.findNavController().navigate(
                ObjListFragmentDirections
                    .actionObjListFragmentToAddDeliveryFragment(clientID, null, 0, 0)
            )
            AMONAWERI -> vBinding.root.findNavController().navigate(
                ObjListFragmentDirections
                    .actionObjListFragmentToAmonaweriFragment(clientID)
            )
//                    else -> // show toast
        }

    }

    private fun initClientsRecycler() = with(vBinding.clientsRecycler) {
        layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        clientListAdapter.onItemClick = ::navigateTo
        adapter = clientListAdapter

/*
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
*/

//        touchHelper.attachToRecyclerView(vBinding.clientsRecycler)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        myCallInterface = context as CallPermissionInterface
    }

    override fun onDetach() {
        super.onDetach()
        myCallInterface = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.client_list_page_menu, menu)

        val searchItem = menu.findItem(R.id.action_search)
        searchView = searchItem.actionView as SearchView

        val pendingQuery = viewModel.searchQuery.value
        if (pendingQuery != null && pendingQuery.isNotEmpty()) {
            searchItem.expandActionView()
            searchView.setQuery(pendingQuery, false)
            viewModel.onNewQuery(pendingQuery)
        }

        searchView.onTextChanged { query ->
            viewModel.searchQuery.value = query
            viewModel.onNewQuery(query)
        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        // item groupID -ში არის პოზიცია
        val selectedClient = clientListAdapter.getClientObject(item.groupId)
        when (item.itemId) {
            R.id.cm_call -> {
                selectedClient.tel?.let {
                    clientPhone = it
                    dialTo()
                }
            }
            R.id.cm_info -> {
                val arr = listOf(
                    String.format(
                        "ობიექტი: %s\n    %s\n    %s",
                        selectedClient.dasaxeleba,
                        selectedClient.sk ?: "-",
                        selectedClient.adress ?: "-"
                    ),
                    String.format(
                        "საკ.პირი: %s\n    %s",
                        selectedClient.sakpiri,
                        selectedClient.tel
                    ),
                    String.format(selectedClient.comment ?: "")
                )
                context?.showListDialog(R.string.info, arr.toTypedArray()) {}
            }
            R.id.cm_edit_obj -> {
                searchView.setOnQueryTextListener(null)
                val direction = ObjListFragmentDirections
                    .actionObjListFragmentToAddObjectFragment(selectedClient.id ?: 0)
                vBinding.root.findNavController().navigate(direction)
            }
            R.id.cm_del -> {
                requireContext().showAskingDialog(
                    null,
                    R.string.confirm_delete_text,
                    R.string.yes,
                    R.string.no,
                    R.style.ThemeOverlay_MaterialComponents_Dialog
                ) {
                    viewModel.deactivateClient(selectedClient.id)
                }
            }
        }
        return true
    }

    fun dialTo() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CALL_PHONE)
            != PackageManager.PERMISSION_GRANTED
        ) {
//            ActivityCompat.requestPermissions(
//                (activity as AppCompatActivity),
//                arrayOf(Manifest.permission.CALL_PHONE),
//                CALL_PERMISSION_REQUEST)
            myCallInterface?.askForCallPermission()
        } else {
            val callIntent = Intent(Intent.ACTION_CALL)
            callIntent.data = Uri.parse("tel:$clientPhone")
            startActivity(callIntent)
        }
    }

    companion object {
        const val CALL_PERMISSION_REQUEST = 101
    }

    var myCallInterface: CallPermissionInterface? = null

    interface CallPermissionInterface {
        fun askForCallPermission()
    }
}
