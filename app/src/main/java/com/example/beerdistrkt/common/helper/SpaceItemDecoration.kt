package com.example.beerdistrkt.common.helper

import android.graphics.Rect
import android.view.View
import androidx.annotation.DimenRes
import androidx.recyclerview.widget.RecyclerView
import com.example.beerdistrkt.R

class SpaceItemDecoration(
    private val edgeOffset: Int,
    private val middleOffset: Int = edgeOffset,
    @RecyclerView.Orientation
    private val orientation: Int
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        val isFirstItem = position == 0
        val isLastItem = position == (parent.adapter?.itemCount?.minus(1) ?: -1)

        if (orientation == RecyclerView.HORIZONTAL) {
            when {
                isFirstItem -> outRect.left = edgeOffset
                isLastItem -> {
                    outRect.left = middleOffset
                    outRect.right = edgeOffset
                }

                else -> outRect.left = middleOffset
            }
        } else {
            when {
                isFirstItem -> outRect.top = edgeOffset
                isLastItem -> {
                    outRect.top = middleOffset
                    outRect.bottom = edgeOffset
                }

                else -> outRect.top = middleOffset
            }
        }
    }
}

fun RecyclerView.addSpacingDecoration(
    @DimenRes spacing: Int,
    @RecyclerView.Orientation orientation: Int,
    includeEdges: Boolean = false,
) {
    val offset = context.resources.getDimensionPixelSize(spacing)
    val edges = if (includeEdges) offset
    else context.resources.getDimensionPixelSize(R.dimen.gr_size_0)
    addItemDecoration(SpaceItemDecoration(edges, (offset / 2f).toInt(), orientation))
}