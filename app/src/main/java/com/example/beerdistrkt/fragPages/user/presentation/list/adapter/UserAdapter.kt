package com.example.beerdistrkt.fragPages.user.presentation.list.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.beerdistrkt.databinding.UserRowBinding
import com.example.beerdistrkt.fragPages.user.domain.model.User

class UserAdapter(
    private val dataList: List<User>
) : RecyclerView.Adapter<UserAdapter.ViewHolder>() {
    var onClick: ((userID: String) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            UserRowBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = dataList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = with(holder.binding) {
        val user = dataList[position]
        userRowUserName.text = user.username
        userRowDisplayName.text = user.name
        userType.text = user.type.name
        root.setOnClickListener {
            onClick?.invoke(dataList[position].id)
        }
    }

    class ViewHolder(val binding: UserRowBinding) : RecyclerView.ViewHolder(binding.root)
}