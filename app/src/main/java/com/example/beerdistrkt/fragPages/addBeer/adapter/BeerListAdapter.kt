package com.example.beerdistrkt.fragPages.addBeer.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.beerdistrkt.R
import com.example.beerdistrkt.databinding.BeerRowBinding
import com.example.beerdistrkt.models.BeerModelBase
import com.example.beerdistrkt.utils.DefaultDiffItemCallback

class BeerListAdapter(
    private val beers: List<BeerModelBase>,
    private val onEditClick: (beer: BeerModelBase) -> Unit,
    private val onDeleteClick: (beerID: Int) -> Unit
) :
    ListAdapter<BeerModelBase, BeerListAdapter.ViewHolder>(DefaultDiffItemCallback<BeerModelBase>()) {

    inner class ViewHolder(val binding: BeerRowBinding) : RecyclerView.ViewHolder(binding.root)
    {
        fun bind(item: BeerModelBase) = with(binding) {
            beerName.text = item.dasaxeleba
            beerPrice.text = item.fasi.toString()
            beerMoreIcon.tag = item
            beerMoreIcon.setOnClickListener {
                popupMenus(it)
            }
            item.displayColor?.let {
                root.backgroundTintList = ColorStateList.valueOf(Color.parseColor(it))
            }
        }

        private fun popupMenus(view: View) {
            val popupMenu = PopupMenu(binding.root.context, view)
            val beer = view.tag as BeerModelBase
            popupMenu.setOnMenuItemClickListener {
                when(it.itemId) {
                    R.id.cm_edit -> onEditClick.invoke(beer)
                    R.id.cm_delete -> onDeleteClick.invoke(beer.id)
                }
                true
            }
            popupMenu.inflate(R.menu.context_menu_beer)
            popupMenu.show()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = BeerRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(beers[position])
    }

    override fun getItemCount() = beers.size

}