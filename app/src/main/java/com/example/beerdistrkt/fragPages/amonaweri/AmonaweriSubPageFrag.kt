package com.example.beerdistrkt.fragPages.amonaweri

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.beerdistrkt.R
import com.example.beerdistrkt.adapters.AmonaweriAdapter
import com.example.beerdistrkt.databinding.AmonaweriSubPageFragmentBinding
import com.example.beerdistrkt.utils.LOCATION
import com.example.beerdistrkt.utils.OBJ_ID
import java.text.SimpleDateFormat
import java.util.*

class AmonaweriSubPageFrag : Fragment() {

    private lateinit var vBinding: AmonaweriSubPageFragmentBinding

    lateinit var simpleDateFormat: SimpleDateFormat
    lateinit var amonaweriListAdapter: AmonaweriAdapter

    private var pagePos: Int = -1

    companion object {
        fun newInstance() = AmonaweriSubPageFrag()

        val TAG = "AmonaweriSubPageFrag"
    }

    lateinit var viewModel: AmonaweriSubPageViewModel

    @SuppressLint("SimpleDateFormat")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewModel = ViewModelProviders.of(this).get(AmonaweriSubPageViewModel::class.java)
        vBinding = AmonaweriSubPageFragmentBinding.inflate(inflater)

        simpleDateFormat = SimpleDateFormat(getString(R.string.patern_date))
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.HOUR, 24 + 4) // es dge rom bolomde chaitvalos
        var gasagzavniTarigi = simpleDateFormat.format(calendar.time)

        pagePos = arguments?.getInt(LOCATION) ?: 0
        val objID = arguments?.getInt(OBJ_ID) ?: 0
        vBinding.progressBarAmonaweri.visibility = View.VISIBLE
        viewModel.requestAmonaweriList(gasagzavniTarigi, objID, pagePos)

        return vBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.amonaweriLiveData.observe(viewLifecycleOwner, Observer {
            vBinding.progressBarAmonaweri.visibility = View.GONE
            amonaweriListAdapter = AmonaweriAdapter(context, it, pagePos, false)
            vBinding.listviewAmonaweri.adapter = amonaweriListAdapter
        })

        viewModel.apiFailureMutableLiveData.observe(viewLifecycleOwner, Observer {
            vBinding.progressBarAmonaweri.visibility = View.GONE
            Toast.makeText(activity, it ?: "sameApiError!" , Toast.LENGTH_LONG).show()
        })
    }

    fun chengeAmonaweriAppearance(grouped: Boolean) {
        viewModel.changeDataStructure(grouped)
    }

}
