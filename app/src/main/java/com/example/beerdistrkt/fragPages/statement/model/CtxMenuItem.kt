package com.example.beerdistrkt.fragPages.statement.model

import androidx.annotation.StringRes
import com.example.beerdistrkt.R

enum class CtxMenuItem(@StringRes val title: Int, val itemID: Int) {
    // sale by barrel & bottle
    Edit(R.string.m_edit, 201),
    // empty barrel
    EditBarrel(R.string.m_edit, 211),
    History(R.string.history, 202),
    Delete(R.string.delete, 203),
    DeleteBarrel(R.string.delete, 213)
}