package com.example.beerdistrkt.fragPages.objList.adapters

import android.content.Context
import android.view.*
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.beerdistrkt.R
import com.example.beerdistrkt.fragPages.login.models.Permission
import com.example.beerdistrkt.models.Obieqti
import com.example.beerdistrkt.utils.Session
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
        holder.itemView.tag = showingList[position]
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

    fun getClientObject(position: Int): Obieqti {
        return showingList[position]
    }

    private class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnCreateContextMenuListener {

        init {
            itemView.setOnCreateContextMenuListener(this)
        }

        override fun onCreateContextMenu(
            menu: ContextMenu?,
            v: View?,
            menuInfo: ContextMenu.ContextMenuInfo?
        ) {
            val title = (itemView.tag as Obieqti).dasaxeleba
            menu?.setHeaderTitle(title)
            menu?.add(adapterPosition, R.id.cm_call, 0, R.string.call)
            menu?.add(adapterPosition, R.id.cm_info, 1, R.string.info)
            menu?.add(adapterPosition, R.id.cm_edit_obj, 2, R.string.m_edit)
                ?.isEnabled = Session.get().hasPermission(Permission.AddEditClient)
//            menu?.add(adapterPosition, R.id.cm_del, 3, R.string.remove)
        }

    }
}