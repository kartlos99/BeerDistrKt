package com.example.beerdistrkt.fragPages.addEditObiects

import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.Observer
import com.example.beerdistrkt.BaseFragment
import com.example.beerdistrkt.R
import com.example.beerdistrkt.getViewModel
import com.example.beerdistrkt.models.BeerModel
import com.example.beerdistrkt.models.ObiectWithPrices
import com.example.beerdistrkt.models.Obieqti
import com.example.beerdistrkt.models.ObjToBeerPrice
import kotlinx.android.synthetic.main.add_object_fragment.*
import java.lang.String

class AddObjectFragment : BaseFragment<AddObjectViewModel>() {

    override val viewModel by lazy {
        getViewModel { AddObjectViewModel(clientID) }
    }

    private val clientID by lazy {
        194
//        val args =
//            AddObjeOrdersFragmentArgs.fromBundle(arguments ?: Bundle())
//        args.mode
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.add_object_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewModel()

        addEditClientDoneBtn.setOnClickListener {
            val client = Obieqti(addEditClientName.editText?.text.toString()).apply {
                id = viewModel.clientID
                adress = addEditClientAdress.editText?.text.toString()
                tel = addEditClientPhone.editText?.text.toString()
                comment = addEditComment.editText?.text.toString()
                sk = addEditClientSK.editText?.text.toString()
                sakpiri = addEditClientPerson.editText?.text.toString()
                chek = if (addEditClientChek.isChecked) "1" else "0"
            }

            val priceList = mutableListOf<ObjToBeerPrice>()

            for (i in 0 until addEditClientPricesContainer.childCount ) {
                val priceRow = addEditClientPricesContainer.getChildAt(i) as LinearLayout
                val eText = priceRow.getChildAt(1) as EditText
                val price = if (eText.text.isEmpty())
                    eText.hint
                else
                    eText.text

                val beerID = eText.tag as Int

                priceList.add(
                    ObjToBeerPrice(
                        viewModel.clientID,
                        beerID,
                        price.toString().toFloat()
                    )
                )
            }

            viewModel.addClient(ObiectWithPrices(client, priceList))
        }
    }


    private fun initViewModel() {
        viewModel.beersLiveData.observe(viewLifecycleOwner, Observer {
            if (it.isNotEmpty()) drawBeerPrices(it)
        })
    }

    private fun drawBeerPrices(beerList: List<BeerModel>) {
        for (i in beerList.indices) {
            val priceItemView = LinearLayout(context).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.END
            }
            val eText = EditText(context).apply {
                id = i + 100
                gravity = Gravity.CENTER
                width = 350
                hint = beerList[i].fasi.toString()
                inputType = InputType.TYPE_NUMBER_FLAG_DECIMAL or InputType.TYPE_CLASS_NUMBER
                tag = beerList[i].id
            }
            val textView = TextView(context).apply {
                id = i + 200
                text = beerList[i].dasaxeleba
            }
            if (viewModel.clientID > 0)
                eText.setText("objPrice")
            priceItemView.addView(textView, 0)
            priceItemView.addView(eText, 1)
            addEditClientPricesContainer.addView(priceItemView, i)
        }
    }

    companion object {
        const val INSERT = "insert"
        const val EDIT = "edit"
    }
}
