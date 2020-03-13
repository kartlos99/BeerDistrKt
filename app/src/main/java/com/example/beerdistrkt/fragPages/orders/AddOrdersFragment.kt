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
import com.example.beerdistrkt.db.ApeniDataBase
import com.example.beerdistrkt.db.ApeniDatabaseDao
import com.example.beerdistrkt.network.ApeniApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class AddOrdersFragment : Fragment() {

    companion object {
        fun newInstance() = AddOrdersFragment()
    }

    private lateinit var viewModel: AddOrdersViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val vv = inflater.inflate(R.layout.add_orders_fragment, container, false)
        val btn1 = vv.findViewById<ImageView>(R.id.btnBeerLeftImg)
        val btn2 = vv.findViewById<ImageView>(R.id.btnBeerRightImg)
        val tvtite = vv.findViewById<TextView>(R.id.t_ludisDasaxeleba)

        btn1.setOnClickListener {
            tvtite.animateThis(R.anim.slide_to_left){
                tvtite.text = "axali"
                tvtite.animateThis(R.anim.slide_from_right){}
            }
        }
        btn2.setOnClickListener {
            tvtite.animateThis(R.anim.slide_to_right){
                tvtite.text = "ძველი ასდ234"
                tvtite.animateThis(R.anim.slide_from_left){}
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
