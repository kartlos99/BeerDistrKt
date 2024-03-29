package com.example.beerdistrkt.fragPages.addBeer

import android.content.res.ColorStateList
import android.graphics.Color
import android.util.Log
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.example.beerdistrkt.R
import com.example.beerdistrkt.adapters.CtxMenuItem
import com.example.beerdistrkt.databinding.BeerRowBinding
import com.example.beerdistrkt.models.BeerModelBase

class BeerListAdapter(
    private val beers: List<BeerModelBase>,
    private val onEditClick: (beer: BeerModelBase) -> Unit,
    private val onDeleteClick: (beerID: Int) -> Unit
) :
    RecyclerView.Adapter<BeerListAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: BeerRowBinding) : RecyclerView.ViewHolder(binding.root)
    {
        fun bind(item: BeerModelBase) {
            binding.beerName.text = item.dasaxeleba
            binding.beerPrice.text = item.fasi.toString()
            binding.beerMoreIcon.tag = item
            binding.beerMoreIcon.setOnClickListener {
                popupMenus(it)
            }
            item.displayColor?.let {
                binding.root.backgroundTintList =
                    ColorStateList.valueOf(Color.parseColor(it))
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