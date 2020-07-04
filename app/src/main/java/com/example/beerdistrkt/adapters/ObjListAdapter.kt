package com.example.beerdistrkt.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.beerdistrkt.R
import com.example.beerdistrkt.models.Obieqti

class ObjListAdapter(
    context: Context?,
    objList: List<Obieqti>
) :
    BaseAdapter() {

    private var myObjList: ArrayList<Obieqti>
    private val objListOriginal: ArrayList<Obieqti>
    private val inflater: LayoutInflater
    var viewHolder: ViewHolder? = null

    init {
        myObjList = ArrayList()
        myObjList.addAll(objList)
        objListOriginal = ArrayList()
        objListOriginal.addAll(objList)
        inflater = context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    override fun getCount(): Int {
        return myObjList.size
    }

    override fun getItem(i: Int): Obieqti {
        return myObjList[i]
    }

    override fun getItemId(i: Int): Long {
        return i.toLong()
    }

    override fun getView(
        i: Int,
        convertView: View?,
        viewGroup: ViewGroup
    ): View {
        val listRowView: View

        if (convertView == null) {
            listRowView = inflater.inflate(R.layout.obj_list_row,null)
            viewHolder = ViewHolder()
            viewHolder!!.clientNameTv = listRowView.findViewById(R.id.clientNameTv) as TextView
            listRowView.tag = viewHolder
        } else {
            listRowView = convertView
            viewHolder = listRowView.tag as ViewHolder
        }
        val obieqti = getItem(i)
        viewHolder!!.clientNameTv?.text = obieqti.dasaxeleba
        return listRowView
    }

    fun filter(query: String?) {
        myObjList.clear()
        if (query.isNullOrEmpty()) {
            myObjList.addAll(objListOriginal)
        } else {
            myObjList.addAll(objListOriginal.filter {
                it.dasaxeleba.contains(query)
            })
        }
        notifyDataSetChanged()
    }

    inner class ViewHolder {
        var clientNameTv: TextView? = null
    }
}