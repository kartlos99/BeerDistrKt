package com.example.beerdistrkt.fragPages.homePage

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.beerdistrkt.*
import com.example.beerdistrkt.databinding.HomeFragmentBinding
import com.example.beerdistrkt.fragPages.homePage.adapter.CommentsAdapter
import com.example.beerdistrkt.fragPages.homePage.models.AddCommentModel
import com.example.beerdistrkt.fragPages.homePage.models.CommentModel
import com.example.beerdistrkt.fragPages.login.models.UserType
import com.example.beerdistrkt.fragPages.login.models.WorkRegion
import com.example.beerdistrkt.fragPages.sawyobi.StoreHouseListFragment
import com.example.beerdistrkt.utils.*
import com.google.android.material.bottomsheet.BottomSheetBehavior

class HomeFragment : BaseFragment<HomeViewModel>(), View.OnClickListener {

    companion object {
        const val emptyBarrelTitle = "ცარიელი"
    }

    private val binding by viewBinding(HomeFragmentBinding::bind)

    override val viewModel: HomeViewModel by lazy {
        getActCtxViewModel()
    }
    lateinit var actViewModel: MainActViewModel

    private lateinit var storeHouseBottomSheet: BottomSheetBehavior<*>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.home_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        actViewModel = (activity as MainActivity).viewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            btnOrder.setOnClickListener(this@HomeFragment)
            btnOrder.setOnClickListener(this@HomeFragment)
            btnSaleResult.setOnClickListener(this@HomeFragment)
            btnSalesByClient.setOnClickListener(this@HomeFragment)
            homeAddComment.setOnClickListener(this@HomeFragment)
        }

        showStoreHouseData()
        getComments()
        initViewModel()
        StoreHouseListFragment.editingGroupID = ""
    }

    override fun onResume() {
        super.onResume()
        viewModel.checkVersionUpdates()
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

        binding.btnOrder.text =
            getString(if (Session.get().region?.regionID?.toInt() == 3) R.string.delivery else R.string.orders)
    }

    private fun showStoreHouseData() {
        if (Session.get().userType == UserType.ADMIN) {
            createInfoFragment()
            initBottomSheet()
        } else
            binding.storeHouseInfoContainer.isVisible = false
    }

    private fun initViewModel() {
        viewModel.mainLoaderLiveData.observe(viewLifecycleOwner) {
            it?.let {
                binding.homeMainProgressBar.visibleIf(it)
                if (!it)
                    viewModel.getStoreBalance()
            }
        }
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
                else -> {}
            }
        })
    }

    private fun initCommentsRecycler(data: List<CommentModel>) {
        binding.homeCommentsRecycler.layoutManager = LinearLayoutManager(context)
        binding.homeCommentsRecycler.adapter = CommentsAdapter(data)
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
        binding.btnOrder.text =
            getString(if (Session.get().region?.regionID?.toInt() == 3) R.string.delivery else R.string.orders)
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

    private fun initBottomSheet() {
        storeHouseBottomSheet = BottomSheetBehavior.from(binding.storeHouseInfoContainer).apply {
            peekHeight = context?.getDimenPixelOffset(R.dimen.gr_size_48) ?: 0
            isHideable = Session.get().userType != UserType.ADMIN
            state = if (Session.get().userType == UserType.ADMIN)
                BottomSheetBehavior.STATE_EXPANDED
            else
                BottomSheetBehavior.STATE_HIDDEN
        }
        storeHouseBottomSheet.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                viewModel.updateBottomSheetState(newState)
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }
        })
        viewModel.updateBottomSheetState(storeHouseBottomSheet.state)
    }

    private fun createInfoFragment() {
        val fragment = StorehouseInfoFragment().apply {
            onToggle = {
                if (storeHouseBottomSheet.state == BottomSheetBehavior.STATE_EXPANDED)
                    storeHouseBottomSheet.state = BottomSheetBehavior.STATE_COLLAPSED
                else
                    storeHouseBottomSheet.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
        childFragmentManager
            .beginTransaction()
            .replace(R.id.storeHouseInfoContainer, fragment)
            .commit()
    }
}
