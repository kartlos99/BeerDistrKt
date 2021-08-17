package com.example.beerdistrkt.fragPages.objList.adapters

import android.view.*
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.beerdistrkt.R
import com.example.beerdistrkt.databinding.ObjListRowBinding
import com.example.beerdistrkt.fragPages.login.models.Permission
import com.example.beerdistrkt.models.Obieqti
import com.example.beerdistrkt.utils.Session

class ClientsListAdapter :
    ListAdapter<Obieqti, ClientsListAdapter.ClientViewHolder>(ClientListDiffUtilCallBack()) {

    var onItemClick: (clientID: Int) -> Unit = {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClientViewHolder {
        val binding = ObjListRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ClientViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ClientViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun getClientObject(position: Int): Obieqti {
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

        fun bind(client: Obieqti) {
            binding.clientNameTv.text = client.dasaxeleba
            itemView.tag = getItem(adapterPosition)
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
            menu?.add(adapterPosition, R.id.cm_del, 3, R.string.remove)
                ?.isEnabled = Session.get().hasPermission(Permission.DeleteClient)
        }
    }

    class ClientListDiffUtilCallBack : DiffUtil.ItemCallback<Obieqti>() {
        override fun areItemsTheSame(oldItem: Obieqti, newItem: Obieqti): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Obieqti, newItem: Obieqti): Boolean {
            return oldItem == newItem
        }
    }
}