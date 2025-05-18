package com.example.beerdistrkt.fragPages.homePage.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.beerdistrkt.R
import com.example.beerdistrkt.databinding.CommentViewBinding
import com.example.beerdistrkt.fragPages.homePage.domain.model.CommentModel
import com.example.beerdistrkt.utils.changeDatePattern
import com.example.beerdistrkt.utils.hide
import com.example.beerdistrkt.utils.show

class CommentsAdapter(
    private val dataList: List<CommentModel>
) : RecyclerView.Adapter<CommentsAdapter.CommentsViewHolder>() {

    inner class CommentsViewHolder(val binding: CommentViewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CommentModel) {
            with(binding) {
                commentDate.text = item.commentDate.changeDatePattern()
                commentClientName.text = item.dasaxeleba
                commentText.text = item.comment
                if (item.op == 'E') {
                    commentAuthor.text = item.username
                    commentImg.hide()
                } else {
                    commentAuthor.text = ""
                    commentImg.show()
                    commentImg.setImageResource(
                        if (item.op == 'O')
                            R.drawable.ic_order_list
                        else
                            R.drawable.ic_delivery
                    )
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentsViewHolder {
        return CommentsViewHolder(
            CommentViewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = dataList.size

    override fun onBindViewHolder(holder: CommentsViewHolder, position: Int) {
        holder.bind(dataList[position])
    }
}