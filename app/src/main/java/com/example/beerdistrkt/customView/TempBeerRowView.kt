package com.example.beerdistrkt.customView

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.beerdistrkt.R
import com.example.beerdistrkt.models.TempOrderItemModel
import com.example.beerdistrkt.utils.visibleIf
import kotlinx.android.synthetic.main.view_temp_beer_row.view.*

class TempBeerRowView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0,
    val rowData: TempOrderItemModel? = null
) : ConstraintLayout(context, attrs, defStyleAttr) {

    init {
        View.inflate(context, R.layout.view_temp_beer_row, this)
        layoutParams =
            LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        if (rowData != null)
            fillData(rowData)
    }

    fun fillData(data: TempOrderItemModel) {

        tempBeerEditBtn.visibleIf(data.onEditClick != null)
        tempBeerInfo.text =
            String.format("%s : %s x %s", data.beer.dasaxeleba, data.canType.name, data.count)
        tempBeerColor.setBackgroundColor(Color.parseColor(data.beer.displayColor ?: "#fff"))

        tempBeerRemoveBtn.setOnClickListener {
            data.onRemoveClick.invoke(data)
        }
    }

}