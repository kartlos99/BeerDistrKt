package com.example.beerdistrkt.fragPages.statement.presentation.barrels.mapper

import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import com.example.beerdistrkt.common.model.BarrelEnum
import com.example.beerdistrkt.fragPages.statement.di.StatementModule
import com.example.beerdistrkt.fragPages.statement.domain.model.BarrelIo
import com.example.beerdistrkt.fragPages.statement.domain.model.BarrelStatementItem
import com.example.beerdistrkt.fragPages.statement.presentation.model.BarrelStatementUiModel
import javax.inject.Inject
import javax.inject.Named

class BarrelUiMapper @Inject constructor(
    @Named(StatementModule.INPUT_COLOR)
    private val inputColor: Int,
    @Named(StatementModule.OUTPUT_COLOR)
    private val outputColor: Int,
) {

    fun map(item: BarrelStatementItem) = BarrelStatementUiModel(
        dateStr = item.dateStr,
        comment = item.comment,
        recordIds = item.ioItems.map { it.recId },
        balance50 = item.balance.balance50,
        balance30 = item.balance.balance30,
        balance20 = item.balance.balance20,
        balance10 = item.balance.balance10,
        inOut50 = formatIo(item.ioItems, BarrelEnum.BARREL_50),
        inOut30 = formatIo(item.ioItems, BarrelEnum.BARREL_30),
        inOut20 = formatIo(item.ioItems, BarrelEnum.BARREL_20),
        inOut10 = formatIo(item.ioItems, BarrelEnum.BARREL_10),
    )

    private fun formatIo(ioItems: List<BarrelIo>, barrelEnum: BarrelEnum): Spannable {
        var input = 0
        var output = 0
        ioItems
            .filter { it.barrel == barrelEnum }
            .forEach {
                input += it.countIn
                output += it.countOut
            }
        return when {
            input > 0 && output > 0 -> formatPair("+$input", "-$output")
            input > 0 -> formatSingle("+$input", inputColor)
            output > 0 -> formatSingle("-$output", outputColor)
            else -> SpannableString("-")
        }
    }

    private fun formatPair(input: String, output: String): Spannable {
        val sp = SpannableString("$input/$output")
        sp.setSpan(
            ForegroundColorSpan(inputColor),
            0,
            input.length,
            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
        )
        sp.setSpan(
            ForegroundColorSpan(outputColor),
            input.length + 1,
            input.length + 1 + output.length,
            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
        )
        return sp
    }

    private fun formatSingle(text: String, spanColor: Int): Spannable =
        SpannableString(text).apply {
            setSpan(
                ForegroundColorSpan(spanColor),
                0,
                text.length,
                Spannable.SPAN_INCLUSIVE_EXCLUSIVE
            )
        }
}