package com.example.beerdistrkt.fragPages.orders

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import com.example.beerdistrkt.R
import com.example.beerdistrkt.animateThis

class AddOrdersFragment : Fragment() {

    companion object {
        fun newInstance() = AddOrdersFragment()
    }

    private lateinit var viewModel: AddOrdersViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val beerList = listOf("ლაგერი", "გაუფილტრავი", "თაფილი")
        var beerPos = 0

        val vv = inflater.inflate(R.layout.add_orders_fragment, container, false)
        val btnBeerLeft = vv.findViewById<ImageView>(R.id.btnBeerLeftImg)
        val btnBeerRight = vv.findViewById<ImageView>(R.id.btnBeerRightImg)
        val tvtite = vv.findViewById<TextView>(R.id.t_ludisDasaxeleba)

        btnBeerLeft.setOnClickListener {
            tvtite.animateThis(R.anim.slide_to_right){
                beerPos = (beerList.size + beerPos - 1) % beerList.size
                tvtite.text = beerList[beerPos]
                tvtite.animateThis(R.anim.slide_from_left){}
            }
        }
        btnBeerRight.setOnClickListener {
            tvtite.animateThis(R.anim.slide_to_left){
                beerPos = (beerPos + 1) % beerList.size
                tvtite.text = beerList[beerPos]
                tvtite.animateThis(R.anim.slide_from_right){}
            }
        }
        return vv
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(AddOrdersViewModel::class.java)

        val argsBundle = arguments ?: Bundle()
        val args = AddOrdersFragmentArgs.fromBundle(argsBundle)
        Log.d("arg", "objID ${args.clientObjectID}")
        viewModel.logObjPr(args.clientObjectID)
    }

}
