package com.example.beerdistrkt.customView

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.example.beerdistrkt.R
import com.example.beerdistrkt.databinding.ViewTempBeerRowBinding
import com.example.beerdistrkt.models.TempBeerItemModel

class TempBeerRowView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0,
    val rowData: TempBeerItemModel? = null
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding = ViewTempBeerRowBinding.bind(inflate(context, R.layout.view_temp_beer_row, this))

    init {
        layoutParams =
            LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        if (rowData != null)
            fillData(rowData)
    }

    private fun fillData(data: TempBeerItemModel) {
        with(binding) {
            tempBeerEditBtn.isVisible = data.onEditClick != null
            tempBeerInfo.text =
                String.format("%s : %s x %s", data.beer.name, data.canType.name, data.count)
            tempBeerColor.setBackgroundColor(data.beer.displayColor)

            tempBeerRemoveBtn.setOnClickListener {
                data.onRemoveClick.invoke(data)
            }
            tempBeerEditBtn.setOnClickListener {
                data.onEditClick?.invoke(data)
            }
        }
    }

}