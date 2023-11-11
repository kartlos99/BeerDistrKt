package com.example.beerdistrkt.customView

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.beerdistrkt.R
import com.example.beerdistrkt.utils.setVisibleWithText

class LabeledListItemView(private val mContext: Context, attrs: AttributeSet?) : BaseListItem(mContext, attrs) {

    private lateinit var labelListItemLabelText: TextView
    private lateinit var textListitemLabelText: TextView
    private lateinit var itemContainerLabelText: FrameLayout
    private lateinit var bottomDividerLabelText: View

    init {
        initView()
    }

    fun initView() {
        val view: FrameLayout = LayoutInflater.from(mContext).inflate(
            R.layout.view_label_text_listitem, this, true
        ) as FrameLayout
        onViewInflated(view)
    }

//    override fun getViewLayout() = R.layout.view_label_text_listitem

    private fun onViewInflated(view: View) {
        labelListItemLabelText = view.findViewById(R.id.listItemLabel)
        textListitemLabelText = view.findViewById(R.id.listItemText)
        itemContainerLabelText = view.findViewById(R.id.listItemRoot)
        bottomDividerLabelText = view.findViewById(R.id.listItemBottomDivider)

    }

/*
    override fun applyAttributes(context: Context, attrs: AttributeSet?) {
        super.applyAttributes(context, attrs)

        val attributes = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.RegularLabelTextListItem,
            0,
            0
        )

        try {
            val isTextMultilineMode =
                attributes.getBoolean(R.styleable.RegularLabelTextListItem_isTextMultilineMode, false)

            val showBottomDivider =
                attributes.getBoolean(R.styleable.RegularLabelTextListItem_showBottomDivider, false)

            val bottomDividerColor =
                attributes.getColor(R.styleable.RegularLabelTextListItem_bottomDividerColor, ContextCompat.getColor(context, R.color.browser_actions_bg_grey))

            if (showBottomDivider) {
                bottomDividerLabelText.visibility = VISIBLE
                bottomDividerLabelText.setBackgroundColor(bottomDividerColor)
            }

            if (isTextMultilineMode) {
                setTextMultilineMode()
            }
        } finally {
            attributes.recycle()
        }
    }
*/

    override fun enable() {
        super.enable()
//        labelListItemLabelText.setTextAppearanceWithFont(R.style.DefaultListItem_Label)
    }

    override fun disable() {
        super.disable()
//        labelListItemLabelText.setTextAppearanceWithFont(R.style.DefaultListItem_Label_Inactive)
    }

    override fun setLabel(label: String) {
        labelListItemLabelText.setVisibleWithText(label)
    }

    fun setLabel(resId: Int) {
        labelListItemLabelText.setVisibleWithText(resId)
    }


/*    fun setStandardLabelText(
        data: LabelTextData?
    ) {
        visibility = if (data != null) {
            labelListItemLabelText.setText(data.labelResId)
            textListitemLabelText.setText(data.textResId)
            View.VISIBLE
        } else {
            View.GONE
        }
    }*/

    override fun setRowItemClickListener(listener: View.OnClickListener?) {
        itemContainerLabelText.setOnClickListener(listener)
    }

    fun setText(text: String) {
        textListitemLabelText.setVisibleWithText(text)
    }

    fun setTextWithColor(text: String, color: Int) {
        textListitemLabelText.text = text
        textListitemLabelText.setTextColor(color)
    }

    fun setTextMultilineMode() {
        textListitemLabelText.setTextAppearance(R.style.DefaultListItem_MultilineText)
    }

    fun setTextBreakStrategy(breakStrategy: Int) {
        textListitemLabelText.breakStrategy = breakStrategy
    }
}
