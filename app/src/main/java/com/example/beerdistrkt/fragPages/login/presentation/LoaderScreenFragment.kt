package com.example.beerdistrkt.fragPages.login.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.beerdistrkt.BaseFragment
import com.example.beerdistrkt.R
import com.example.beerdistrkt.collectLatest
import com.example.beerdistrkt.databinding.FragmentLoadingScreenBinding
import com.example.beerdistrkt.fragPages.login.presentation.LoaderScreenViewModel.Action
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoaderScreenFragment : BaseFragment<LoaderScreenViewModel>() {

    override val viewModel: LoaderScreenViewModel by viewModels()

    private val binding by viewBinding(FragmentLoadingScreenBinding::bind)


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_loading_screen, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.openHomeScreenFlow.collectLatest(viewLifecycleOwner) {
            when (it) {
                Action.OpenHomePage -> findNavController()
                    .navigate(LoaderScreenFragmentDirections.actionLoaderScreenFragmentToHomeFragment())

                Action.OpenLoginPage -> findNavController()
                    .navigate(LoaderScreenFragmentDirections.actionLoaderScreenFragmentToLoginFragment())
            }
        }
    }

}