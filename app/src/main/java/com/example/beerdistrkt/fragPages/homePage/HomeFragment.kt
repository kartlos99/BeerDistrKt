package com.example.beerdistrkt.fragPages.homePage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.beerdistrkt.*
import com.example.beerdistrkt.fragPages.homePage.adapter.CommentsAdapter
import com.example.beerdistrkt.fragPages.homePage.models.AddCommentModel
import com.example.beerdistrkt.fragPages.homePage.models.CommentModel
import com.example.beerdistrkt.fragPages.sawyobi.adapters.SimpleBeerRowAdapter
import com.example.beerdistrkt.fragPages.sawyobi.models.SimpleBeerRowModel
import com.example.beerdistrkt.utils.*
import kotlinx.android.synthetic.main.home_fragment.*

class HomeFragment : BaseFragment<HomeViewModel>(), View.OnClickListener {

    companion object {
        fun newInstance() = HomeFragment()
    }

    override val viewModel: HomeViewModel by lazy {
        getViewModel { HomeViewModel() }
    }

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
        btnOrder.setOnClickListener(this)
        btnSaleResult.setOnClickListener(this)
        btnSalesByClient.setOnClickListener(this)
        homeHideStoreHouse.setOnClickListener(this)
        homeAddComment.setOnClickListener(this)

        showStoreHouseData(Session.get().userType == UserType.ADMIN)
        getComments()
        initViewModel()
    }

    private fun showStoreHouseData(shouldShow: Boolean) {
        homeStoreHouseRecycler.visibleIf(shouldShow)
        homeHideStoreHouse.visibleIf(shouldShow)
        homeStoreHouseTitle.visibleIf(shouldShow)
        homeStoreHouseBkg.visibleIf(shouldShow)
    }

    private fun initViewModel() {
        viewModel.mainLoaderLiveData.observe(viewLifecycleOwner, Observer {
            it?.let {
                homeMainProgressBar?.visibleIf(it)
                if (!it)
                    viewModel.getStoreBalance()
            }
        })
        viewModel.barrelsListLiveData.observe(viewLifecycleOwner, Observer {
            initStoreHouseRecycler(it)
        })
        viewModel.commentsListLiveData.observe(viewLifecycleOwner, Observer {
            initCommentsRecycler(it)
            (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.location_ge)
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
            }
        })
    }

    private fun initCommentsRecycler(data: List<CommentModel>) {
        homeCommentsRecycler.layoutManager = LinearLayoutManager(context)
        homeCommentsRecycler.adapter = CommentsAdapter(data)
    }

    private fun initStoreHouseRecycler(data: List<SimpleBeerRowModel>) {
        homeStoreHouseRecycler.layoutManager = LinearLayoutManager(context)
        val adapter = SimpleBeerRowAdapter(data)
        adapter.onClick = View.OnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_sawyobiFragment)
        }
        homeStoreHouseRecycler.adapter = adapter
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btnOrder -> {
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
            R.id.homeHideStoreHouse -> {
                if (homeStoreHouseRecycler.visibility == View.VISIBLE) {
                    homeStoreHouseRecycler.goAway()
                    homeHideStoreHouse.rotation = 180f
                } else {
                    if (homeStoreHouseRecycler.adapter == null) {
                        initStoreHouseRecycler(viewModel.barrelsListLiveData.value ?: listOf())
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
        viewModel.getComments()
    }
}
