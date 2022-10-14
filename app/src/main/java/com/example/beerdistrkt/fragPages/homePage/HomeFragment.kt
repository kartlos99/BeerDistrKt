package com.example.beerdistrkt.fragPages.homePage

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.beerdistrkt.*
import com.example.beerdistrkt.databinding.HomeFragmentBinding
import com.example.beerdistrkt.fragPages.homePage.adapter.CommentsAdapter
import com.example.beerdistrkt.fragPages.homePage.models.AddCommentModel
import com.example.beerdistrkt.fragPages.homePage.models.CommentModel
import com.example.beerdistrkt.fragPages.login.models.UserType
import com.example.beerdistrkt.fragPages.login.models.WorkRegion
import com.example.beerdistrkt.fragPages.orders.view.CounterLinearProgressView.Companion.BOLD_STYLE_NON_POSITIVE
import com.example.beerdistrkt.fragPages.sawyobi.StoreHouseListFragment
import com.example.beerdistrkt.fragPages.sawyobi.adapters.SimpleBeerRowAdapter
import com.example.beerdistrkt.fragPages.sawyobi.models.SimpleBeerRowModel
import com.example.beerdistrkt.utils.*

class HomeFragment : BaseFragment<HomeViewModel>(), View.OnClickListener {

    companion object {
        const val emptyBarrelTitle = "ცარიელი"
    }

    private val binding by viewBinding(HomeFragmentBinding::bind)

    override val viewModel: HomeViewModel by lazy {
        getViewModel { HomeViewModel() }
    }
    lateinit var actViewModel: MainActViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

//        val binding: HomeFragmentBinding = DataBindingUtil.inflate(
//            inflater, R.layout.home_fragment, container, false)
//        val application = requireNotNull(this.activity).application
//        lifecycleOwner = this

        return inflater.inflate(R.layout.home_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        actViewModel = (activity as MainActivity).viewModel

        with(binding) {
            btnOrder.setOnClickListener(this@HomeFragment)
            btnOrder.setOnClickListener(this@HomeFragment)
            btnSaleResult.setOnClickListener(this@HomeFragment)
            btnSalesByClient.setOnClickListener(this@HomeFragment)
            homeHideStoreHouse.setOnClickListener(this@HomeFragment)
            homeAddComment.setOnClickListener(this@HomeFragment)
            homeStoreHouseBkg.setOnClickListener(this@HomeFragment)
        }


        showStoreHouseData(Session.get().userType == UserType.ADMIN)
        getComments()
        initViewModel()
        StoreHouseListFragment.editingGroupID = ""

    }

    override fun onResume() {
        super.onResume()
        setHasOptionsMenu(Session.get().regions.size > 1)
        if (Session.get().isAccessTokenValid() && Session.get().region == null)
            if (Session.get().regions.size == 1)
                onRegionChange(Session.get().regions.first())
            else
                showRegionSelectorDialog(
                    Session.get().regions,
                    null
                ) {
                    onRegionChange(it)
                }

        binding.btnOrder.text = getString(if (Session.get().region?.regionID?.toInt() == 3) R.string.delivery else R.string.orders)
    }

    private fun showStoreHouseData(shouldShow: Boolean) {
        binding.homeStoreHouseRecycler.visibleIf(shouldShow)
        binding.homeHideStoreHouse.visibleIf(shouldShow)
        binding.homeStoreHouseTitle.visibleIf(shouldShow)
        binding.homeStoreHouseBkg.visibleIf(shouldShow)
    }

    private fun initViewModel() {
        viewModel.mainLoaderLiveData.observe(viewLifecycleOwner, Observer {
            it?.let {
                binding.homeMainProgressBar.visibleIf(it)
                if (!it)
                    viewModel.getStoreBalance()
            }
        })
        viewModel.barrelsListLiveData.observe(viewLifecycleOwner, Observer {
            when (it) {
                is ApiResponseState.Loading -> {
                    binding.homeMainStoreHouseLoader.visibleIf(it.showLoading)
                }
                is ApiResponseState.Success -> initStoreHouseRecycler(it.data)
                else -> showToast(R.string.some_error)
            }

        })
        viewModel.commentsListLiveData.observe(viewLifecycleOwner, Observer {
            initCommentsRecycler(it)
            setPageTitle(Session.get().region?.name)
        })
        viewModel.addCommentLiveData.observe(viewLifecycleOwner, Observer {
            when (it) {
                is ApiResponseState.Loading -> {
                }
                is ApiResponseState.Success -> {
                    notifyNewComment(it.data)
                    showToast(R.string.data_saved)
                    viewModel.stopAddCommentObserving()
                }
                else -> {
                }
            }
        })
    }

    private fun initCommentsRecycler(data: List<CommentModel>) {
        binding.homeCommentsRecycler.layoutManager = LinearLayoutManager(context)
        binding.homeCommentsRecycler.adapter = CommentsAdapter(data)
    }

    private fun initStoreHouseRecycler(data: List<SimpleBeerRowModel>) {
        binding.homeStoreHouseRecycler.layoutManager = LinearLayoutManager(context)
        val adapter = SimpleBeerRowAdapter(data.filter {
            it.values.values.any { barrelCount ->
                barrelCount > 0
            }
                    || it.title == emptyBarrelTitle
        }).apply {
            barrelsAmountBoldStyle = BOLD_STYLE_NON_POSITIVE
        }
        adapter.onClick = View.OnClickListener {
            if (Session.get().region?.hasOwnStorage() == true)
                findNavController().navigate(R.id.action_homeFragment_to_sawyobiFragment)
        }
        binding.homeStoreHouseRecycler.adapter = adapter
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btnOrder -> {
                if (Session.get().region?.regionID?.toInt() == 3)
                    view.findNavController().navigate(
                        HomeFragmentDirections.actionHomeFragmentToObjListFragment(MITANA)
                    )
                else
                    view.findNavController().navigate(R.id.action_homeFragment_to_ordersFragment)
            }
            R.id.btnSaleResult -> {
                view.findNavController()
                    .navigate(HomeFragmentDirections.actionHomeFragmentToSalesFragment())
            }
            R.id.btnSalesByClient -> {
                view.findNavController()
                    .navigate(HomeFragmentDirections.actionHomeFragmentToObjListFragment(AMONAWERI))
            }
            R.id.homeStoreHouseBkg,
            R.id.homeHideStoreHouse -> with(binding) {
                if (homeStoreHouseRecycler.visibility == View.VISIBLE) {
                    homeStoreHouseRecycler.goAway()
                    homeHideStoreHouse.rotation = 180f
                } else {
                    if (homeStoreHouseRecycler.adapter == null) {
                        initStoreHouseRecycler(viewModel.storeHouseData)
                    } else {
                        homeStoreHouseRecycler.show()
                        homeHideStoreHouse.rotation = 0f
                    }
                }
            }
            R.id.homeAddComment -> {
                context?.showTextInputDialog(
                    R.string.add_comment,
                    R.style.ThemeOverlay_MaterialComponents_Dialog
                ) {
                    if (it.isNotEmpty())
                        viewModel.addComment(AddCommentModel(it, Session.get().userID!!))
                    else
                        showToast(R.string.msg_empty_not_saved)
                }
            }
        }
    }

    fun getComments() {
        if (Session.get().isAccessTokenValid())
            viewModel.getComments()
    }

    private fun onRegionChange(region: WorkRegion) {
        if (Session.get().region == region) return
        Session.get().region = region
        viewModel.changeRegion(region)
        getComments()
        StoreHouseListFragment.editingGroupID = ""
        setPageTitle(region.name)
        binding.btnOrder.text = getString(if (Session.get().region?.regionID?.toInt() == 3) R.string.delivery else R.string.orders)
        actViewModel.updateNavHeader()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.home_manu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.mHomeSwitchRegion -> {
                showRegionSelectorDialog(
                    Session.get().regions,
                    Session.get().region,
                    true
                ) {
                    onRegionChange(it)
                }
                return true
            }
        }
        return false
    }

    private fun showRegionSelectorDialog(
        regions: List<WorkRegion>,
        currentRegion: WorkRegion?,
        cancelable: Boolean = false,
        onComplete: (selectedRegion: WorkRegion) -> Unit
    ) {
        var selectedRegion: WorkRegion? = currentRegion
        val builder = AlertDialog.Builder(requireContext())
        builder
            .setTitle(getString(R.string.choose_region_title))
            .setCancelable(cancelable)
            .setSingleChoiceItems(
                regions.map { it.name }.toTypedArray(),
                regions.indexOfFirst { it == currentRegion }
            ) { _, i ->
                selectedRegion = regions[i]
            }
            .setPositiveButton(R.string.ok) { _, _ -> }

        val alertDialog = builder.create()
        alertDialog.show()
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            if (selectedRegion == null)
                showToast(R.string.choose_region_request)
            else {
                onComplete.invoke(selectedRegion!!)
                alertDialog.dismiss()
            }
        }
    }
}
