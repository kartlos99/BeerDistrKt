package com.example.beerdistrkt.fragPages.customer.presentation.adapters

import android.content.Context
import android.view.*
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.beerdistrkt.R
import com.example.beerdistrkt.databinding.ObjListRowBinding
import com.example.beerdistrkt.fragPages.login.models.Permission
import com.example.beerdistrkt.fragPages.customer.domain.model.Customer
import com.example.beerdistrkt.utils.Session

class ClientsListAdapter(
    val onItemClick: (clientID: Int) -> Unit
) :
    ListAdapter<Customer, ClientsListAdapter.ClientViewHolder>(ClientListDiffUtilCallBack()) {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClientViewHolder {
        val binding = ObjListRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ClientViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ClientViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun getClientObject(position: Int): Customer {
        return getItem(position)
    }

    inner class ClientViewHolder(
        private val binding: ObjListRowBinding
    ) : RecyclerView.ViewHolder(binding.root), View.OnCreateContextMenuListener {

        init {
            binding.root.setOnCreateContextMenuListener(this)
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick.invoke(getItem(position).id ?: -1)
                }
            }
        }

        fun bind(client: Customer) = with(binding) {
            clientNameTv.text = client.name
            tvPassedTime.text = formatPassedTime(root.context, client.warnInfo?.passedDays ?: 0)
            infoIcon.isVisible = client.warnInfo != null
            tvPassedTime.isVisible = client.warnInfo != null
            itemView.tag = getItem(adapterPosition)
        }

        override fun onCreateContextMenu(
            menu: ContextMenu?,
            v: View?,
            menuInfo: ContextMenu.ContextMenuInfo?
        ) {
            val title = (itemView.tag as Customer).name
            menu?.setHeaderTitle(title)
            menu?.add(adapterPosition, R.id.cm_call, 0, R.string.call)
            menu?.add(adapterPosition, R.id.cm_info, 1, R.string.info)
            menu?.add(adapterPosition, R.id.cm_edit_obj, 2, R.string.m_edit)
                ?.isEnabled = Session.get().hasPermission(Permission.AddEditClient)
            menu?.add(adapterPosition, R.id.cm_del, 3, R.string.remove)
                ?.isEnabled = Session.get().hasPermission(Permission.DeleteClient)
        }
    }

    private fun formatPassedTime(context: Context, days: Int): String = when {
        days < 31 -> context.resources.getString(R.string.x_days_ago, days)
        days < 366 -> context.resources.getString(R.string.x_month_ago, days / 30)
        else -> context.resources.getString(R.string.x_year_ago, days / 365)
    }

    class ClientListDiffUtilCallBack : DiffUtil.ItemCallback<Customer>() {
        override fun areItemsTheSame(oldItem: Customer, newItem: Customer): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Customer, newItem: Customer): Boolean {
            return oldItem == newItem
        }
    }
}