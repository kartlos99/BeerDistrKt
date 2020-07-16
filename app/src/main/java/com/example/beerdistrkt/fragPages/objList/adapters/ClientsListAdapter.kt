package com.example.beerdistrkt.fragPages.objList.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.beerdistrkt.R
import com.example.beerdistrkt.models.Obieqti
import kotlinx.android.synthetic.main.obj_list_row.view.*

class ClientsListAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var showingList: MutableList<Obieqti> = mutableListOf()
    var originalList: MutableList<Obieqti> = mutableListOf()
    var onItemClick: (clientID: Int) -> Unit = {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.obj_list_row, null)
        view.layoutParams = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return showingList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder.itemView.clientNameTv.text = showingList[position].dasaxeleba
        holder.itemView.setOnClickListener {
            onItemClick.invoke(showingList[position].id ?: -1)
        }
    }

    fun setData(data: List<Obieqti>) {
        showingList.clear()
        showingList.addAll(data)
        originalList.clear()
        originalList.addAll(data)
        notifyDataSetChanged()
    }

    fun filter(query: String?) {
        showingList.clear()
        if (query.isNullOrEmpty()) {
            showingList.addAll(originalList)
        } else {
            showingList.addAll(originalList.filter {
                it.dasaxeleba.contains(query)
            })
        }
        notifyDataSetChanged()
    }

    private class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}