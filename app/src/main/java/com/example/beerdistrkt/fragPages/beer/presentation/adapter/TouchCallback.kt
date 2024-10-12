package com.example.beerdistrkt.fragPages.beer.presentation.adapter

import android.util.Log
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.DOWN
import androidx.recyclerview.widget.ItemTouchHelper.UP
import androidx.recyclerview.widget.RecyclerView

class TouchCallback(
    val onItemMove: (startPosition: Int, endPosition: Int) -> Unit
) : ItemTouchHelper.Callback() {

    private var startPosition = -1

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        return makeMovementFlags(UP or DOWN, 0)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        sourceVH: RecyclerView.ViewHolder,
        targetVH: RecyclerView.ViewHolder
    ): Boolean {
        val sourcePosition = sourceVH.bindingAdapterPosition
        val targetPosition = targetVH.bindingAdapterPosition
        Log.d(TAG, "${sourceVH}")

        recyclerView.adapter?.notifyItemMoved(sourcePosition, targetPosition)
        Log.d(TAG, "$sourcePosition to $targetPosition")
        return false
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        super.onSelectedChanged(viewHolder, actionState)
        viewHolder?.bindingAdapterPosition?.let { position ->
            startPosition = position
        }
        Log.d(TAG, "onSelectedChanged: ${viewHolder?.bindingAdapterPosition}")
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)
        Log.d(TAG, "clearView: ${viewHolder.bindingAdapterPosition}")
        val endPosition = viewHolder.bindingAdapterPosition
        if (startPosition != endPosition) {
            onItemMove(startPosition, endPosition)
        }
    }


    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}

    companion object {
        private const val TAG = "TouchHelperCallBack"
    }
}