package com.example.beerdistrkt.fragPages.homePage.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.beerdistrkt.R
import com.example.beerdistrkt.fragPages.homePage.models.CommentModel
import com.example.beerdistrkt.utils.goAway
import com.example.beerdistrkt.utils.show
import kotlinx.android.synthetic.main.comment_view.view.*

class CommentsAdapter(
    private val dataList: List<CommentModel>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.comment_view, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = dataList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder.itemView.commentClientName.text = dataList[position].dasaxeleba
        holder.itemView.commentText.text = dataList[position].comment
        if (dataList[position].op == 'E') {
            holder.itemView.commentAuthor.text = dataList[position].username
            holder.itemView.commentImg.goAway()
        } else {
            holder.itemView.commentAuthor.text = ""
            holder.itemView.commentImg.show()
            holder.itemView.commentImg.setImageResource(
                if (dataList[position].op == 'O')
                    R.drawable.ic_order_list
                else
                    R.drawable.ic_delivery
            )
        }
    }

    private class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}