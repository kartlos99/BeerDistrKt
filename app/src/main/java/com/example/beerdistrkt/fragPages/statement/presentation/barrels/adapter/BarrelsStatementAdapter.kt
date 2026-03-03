package com.example.beerdistrkt.fragPages.statement.presentation.barrels.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.beerdistrkt.databinding.BarrelStatementItemBinding
import com.example.beerdistrkt.fragPages.statement.presentation.model.BarrelStatementUiModel
import com.example.beerdistrkt.utils.DefaultDiffItemCallback

class BarrelsStatementAdapter(
    private val listener: BarrelStatementActionListener,
) : ListAdapter<BarrelStatementUiModel, RecyclerView.ViewHolder>(
    DefaultDiffItemCallback()
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return BarrelIoItemViewHolder(
            BarrelStatementItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            ),
            listener,
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as BarrelIoItemViewHolder).bind(getItem(position)) {
            notifyItemChanged(position)
        }
    }

    class BarrelIoItemViewHolder(
        private val binding: BarrelStatementItemBinding,
        private val listener: BarrelStatementActionListener,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: BarrelStatementUiModel, updateItem: () -> Unit) = with(binding) {
            statementDate.text = item.dateStr
            data10.text = item.inOut10
            data20.text = item.inOut20
            data30.text = item.inOut30
            data50.text = item.inOut50

            bal10.text = item.balance10.toString()
            bal20.text = item.balance20.toString()
            bal30.text = item.balance30.toString()
            bal50.text = item.balance50.toString()

            comment.isVisible = !item.comment.isNullOrBlank()
            comment.text = item.comment

            infoIcon.setOnClickListener {
                listener.onOptionsClick(item.recordIds)
            }
            root.setOnLongClickListener {
                item.isExpanded = !item.isExpanded
                updateItem()
                true
            }
            balanceContainer.isVisible = item.isExpanded
            balanceTitle.isVisible = item.isExpanded
        }
    }
}