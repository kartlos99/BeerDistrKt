package com.example.beerdistrkt.common.helper

import android.R
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration


class LinearDividerItemDecoration : ItemDecoration {
    private var mDivider: Drawable?
    private var mShowFirstDivider = false
    private var mShowLastDivider = false

    constructor(context: Context, attrs: AttributeSet?) {
        val a: TypedArray = context
            .obtainStyledAttributes(attrs, intArrayOf(R.attr.listDivider))
        mDivider = a.getDrawable(0)
        a.recycle()
    }

    constructor(
        context: Context,
        attrs: AttributeSet?,
        showFirstDivider: Boolean,
        showLastDivider: Boolean
    ) : this(context, attrs) {
        mShowFirstDivider = showFirstDivider
        mShowLastDivider = showLastDivider
    }

    constructor(divider: Drawable?) {
        mDivider = divider
    }

    constructor(
        divider: Drawable?,
        showFirstDivider: Boolean,
        showLastDivider: Boolean
    ) : this(divider) {
        mShowFirstDivider = showFirstDivider
        mShowLastDivider = showLastDivider
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        if (mDivider == null) {
            return
        }
        if (parent.getChildPosition(view) < 1) {
            return
        }

        if (getOrientation(parent) == LinearLayoutManager.VERTICAL) {
            outRect.top = mDivider!!.intrinsicHeight
        } else {
            outRect.left = mDivider!!.intrinsicWidth
        }
    }

    override fun onDrawOver(
        canvas: Canvas,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        if (mDivider == null) {
            super.onDrawOver(canvas, parent, state)
            return
        }

        // Initialization needed to avoid compiler warning
        var left = 0
        var right = 0
        var top = 0
        var bottom = 0
        val size: Int
        val orientation = getOrientation(parent)
        val childCount = parent.childCount

        if (orientation == LinearLayoutManager.VERTICAL) {
            size = mDivider!!.intrinsicHeight
            left = parent.paddingLeft
            right = parent.width - parent.paddingRight
        } else { //horizontal
            size = mDivider!!.intrinsicWidth
            top = parent.paddingTop
            bottom = parent.height - parent.paddingBottom
        }

        val startIndex = if (mShowFirstDivider) 0 else 1
        (0 until childCount).forEach { index ->
            val child: View = parent.getChildAt(index)
            val params = child.layoutParams as RecyclerView.LayoutParams

            if (orientation == LinearLayoutManager.VERTICAL) {
                bottom = child.top + params.topMargin
                top = bottom - size
            } else { //horizontal
                right = child.left - params.leftMargin
                left = right - size
            }
            mDivider!!.setBounds(left, top, right, bottom)
            mDivider!!.draw(canvas)
        }

        // show last divider
        if (mShowLastDivider && childCount > 0) {
            val child: View = parent.getChildAt(childCount - 1)
            val params = child.layoutParams as RecyclerView.LayoutParams
            if (orientation == LinearLayoutManager.VERTICAL) {
                bottom = child.bottom + params.bottomMargin
                top = bottom - size
            } else { // horizontal
                right = child.right + params.rightMargin
                left = right - size
            }
            mDivider!!.setBounds(left, top, right, bottom)
            mDivider!!.draw(canvas)
        }
    }

    private fun getOrientation(parent: RecyclerView): Int {
        if (parent.layoutManager is LinearLayoutManager) {
            val layoutManager = parent.layoutManager as LinearLayoutManager?
            return layoutManager!!.orientation
        } else {
            throw IllegalStateException(
                "LinearDividerItemDecoration can only be used with a LinearLayoutManager."
            )
        }
    }
}