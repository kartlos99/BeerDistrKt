package com.example.beerdistrkt.fragPages.homePage.presentation

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.beerdistrkt.BaseFragment
import com.example.beerdistrkt.MainActViewModel
import com.example.beerdistrkt.MainActivity
import com.example.beerdistrkt.R
import com.example.beerdistrkt.databinding.HomeFragmentBinding
import com.example.beerdistrkt.fragPages.homePage.domain.model.AddCommentModel
import com.example.beerdistrkt.fragPages.homePage.domain.model.CommentModel
import com.example.beerdistrkt.fragPages.homePage.presentation.adapter.CommentsAdapter
import com.example.beerdistrkt.fragPages.login.domain.model.UserType
import com.example.beerdistrkt.fragPages.user.domain.model.WorkRegion
import com.example.beerdistrkt.fragPages.sawyobi.StoreHouseListFragment
import com.example.beerdistrkt.getDimenPixelOffset
import com.example.beerdistrkt.notifyNewComment
import com.example.beerdistrkt.showTextInputDialog
import com.example.beerdistrkt.utils.AMONAWERI
import com.example.beerdistrkt.utils.ApiResponseState
import com.example.beerdistrkt.utils.MITANA
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment<HomeViewModel>(), View.OnClickListener {

    private val binding by viewBinding(HomeFragmentBinding::bind)

    override val viewModel: HomeViewModel by viewModels()

    lateinit var actViewModel: MainActViewModel

    private lateinit var storeHouseBottomSheet: BottomSheetBehavior<*>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
            homeAddComment.setOnClickListener(this@HomeFragment)
        }

        showStoreHouseData()
        initViewModel()
        StoreHouseListFragment.editingGroupID = ""
    }

    override fun onResume() {
        super.onResume()
        setHasOptionsMenu(viewModel.session.regions.size > 1)
        if (viewModel.session.isAccessTokenValid() && viewModel.session.region == null) {
            if (viewModel.session.regions.size == 1)
                onRegionChange(viewModel.session.regions.first())
            else
                showRegionSelectorDialog(
                    viewModel.session.regions,
                    null
                ) {
                    onRegionChange(it)
                }
        } else {
            viewModel.getStoreBalance()
        }

        binding.btnOrder.text = getString(
            if (viewModel.session.region?.id == 3) R.string.delivery else R.string.orders
        )
    }

    private fun showStoreHouseData() {
        if (viewModel.session.userType == UserType.ADMIN) {
            createInfoFragment()
            initBottomSheet()
        } else
            binding.storeHouseInfoContainer.isVisible = false
    }

    private fun initViewModel() {
        viewModel.mainLoaderLiveData.observe(viewLifecycleOwner) {
            it?.let { isLoading ->
                binding.homeMainProgressBar.isVisible = isLoading
            }
        }
        viewModel.commentsListLiveData.observe(viewLifecycleOwner, Observer { comments ->
            initCommentsRecycler(comments)
            updateCommentsState(comments.isEmpty().not())
            setPageTitle(viewModel.session.region?.name)
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

    private fun updateCommentsState(hasComments: Boolean) = with(binding) {
        homeCommentsRecycler.isVisible = hasComments
        noCommentsTv.isVisible = !hasComments
    }

    private fun initCommentsRecycler(data: List<CommentModel>) {
        binding.homeCommentsRecycler.layoutManager = LinearLayoutManager(context)
        binding.homeCommentsRecycler.adapter = CommentsAdapter(data)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btnOrder -> {
                if (viewModel.session.region?.id == 3)
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
                        viewModel.addComment(AddCommentModel(it, viewModel.session.userID!!))
                    else
                        showToast(R.string.msg_empty_not_saved)
                }
            }
        }
    }

    fun getComments() {
        if (viewModel.session.isAccessTokenValid())
            viewModel.getComments()
    }

    private fun onRegionChange(region: WorkRegion) {
        if (viewModel.session.region == region) return
        viewModel.session.region = region
        viewModel.setRegion(region)
        StoreHouseListFragment.editingGroupID = ""
        setPageTitle(region.name)
        binding.btnOrder.text = getString(
            if (viewModel.session.region?.id == 3) R.string.delivery else R.string.orders
        )
        actViewModel.updateNavHeader()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.home_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.mHomeSwitchRegion -> {
                showRegionSelectorDialog(
                    viewModel.session.regions,
                    viewModel.session.region,
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
            isHideable = viewModel.session.userType != UserType.ADMIN
            state = if (viewModel.session.userType == UserType.ADMIN)
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

    companion object {
        const val emptyBarrelTitle = "ცარიელი"
    }
}
