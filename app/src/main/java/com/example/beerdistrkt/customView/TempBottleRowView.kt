package com.example.beerdistrkt.customView

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.example.beerdistrkt.R
import com.example.beerdistrkt.databinding.ViewTempBottleRowBinding
import com.example.beerdistrkt.fragPages.bottle.presentation.model.TempBottleItemModel

class TempBottleRowView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0,
    rowData: TempBottleItemModel? = null
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding =
        ViewTempBottleRowBinding.bind(inflate(context, R.layout.view_temp_bottle_row, this))

    init {
        layoutParams =
            LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        if (rowData != null)
            fillData(rowData)
    }

    private fun fillData(data: TempBottleItemModel) = with(binding) {
        editBtn.isVisible = data.onEditClick != null
        tempBottleInfo.text =
            String.format("%s x %s", data.bottle.name, data.count)

        removeBtn.setOnClickListener {
            data.onRemoveClick.invoke(data)
        }
        editBtn.setOnClickListener {
            data.onEditClick?.invoke(data)
        }
    }

}