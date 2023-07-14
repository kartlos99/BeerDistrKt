package com.example.beerdistrkt.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

const val API_DATE_FORMAT_PATTERN = "yyyy-MM-dd HH:mm:ss"
const val BASE_DISPLAY_DATE_PATTERN = "yyyy.MM.dd HH:mm"
const val BASE_SHORT_DISPLAY_DATE_PATTERN = "MM.dd HH:mm"
const val BASE_DATE_PATTERN = "yyyy-MM-dd HH:mm"
const val DATE_WITH_DOTS_PATTERN = "yyyy.MM.dd"

fun String.changeDatePattern(
    fromPattern: String = API_DATE_FORMAT_PATTERN,
    toPattern: String = BASE_DISPLAY_DATE_PATTERN
): String = if (isNotBlank())
    toDate(fromPattern)?.toPatternString(toPattern) ?: this
else this


fun String.toDate(datePattern: String = API_DATE_FORMAT_PATTERN): Date? =
    SimpleDateFormat(datePattern, Locale.getDefault()).parse(this)

fun Date.toPatternString(datePattern: String = BASE_DISPLAY_DATE_PATTERN): String =
    SimpleDateFormat(datePattern, Locale.getDefault()).format(this)

fun Date.daysBetween(compareDate: Date = Date()): Long {
    val diff = compareDate.time - this.time
    return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS)
}

fun String.changeDatePatternToShort(
    fromPattern: String = API_DATE_FORMAT_PATTERN,
): String = if (isNotBlank())
    toDate(fromPattern)?.toShortPatternString() ?: this
else this

fun Date.toShortPatternString(datePattern: String = BASE_DISPLAY_DATE_PATTERN): String {
    val format = if (daysBetween() > 365 ) datePattern else BASE_SHORT_DISPLAY_DATE_PATTERN
    return SimpleDateFormat(format, Locale.getDefault()).format(this)
}
