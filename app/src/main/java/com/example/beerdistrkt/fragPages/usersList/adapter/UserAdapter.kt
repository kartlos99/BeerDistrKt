package com.example.beerdistrkt.fragPages.usersList.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.beerdistrkt.R
import com.example.beerdistrkt.models.User
import kotlinx.android.synthetic.main.user_row.view.*

class UserAdapter(
private val dataList: List<User>
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var onClick: ((userID: String) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_row, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = dataList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder.itemView.userRowUserName.text = dataList[position].username
        holder.itemView.userRowDisplayName.text = dataList[position].name
        holder.itemView.setOnClickListener {
            onClick?.invoke(dataList[position].id)
        }
    }

    private class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}