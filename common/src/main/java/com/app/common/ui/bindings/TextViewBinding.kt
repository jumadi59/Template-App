package com.app.common.ui.bindings

import android.annotation.SuppressLint
import android.graphics.Paint
import android.text.method.LinkMovementMethod
import android.util.Log
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.core.text.HtmlCompat
import androidx.databinding.BindingAdapter
import com.app.common.R
import com.app.common.ui.extensions.gone
import com.app.common.ui.extensions.invisible
import com.app.common.ui.extensions.visible
import com.app.core.extensions.formatCardNumber
import com.app.core.extensions.toRupiah
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.ln
import kotlin.math.pow


/**
 * Created by Anonim date on 28/06/2022.
 * Bengkulu, Indonesia.
 * Copyright (c) Company. All rights reserved.
 **/
object TextViewBinding {

    @JvmStatic
    @BindingAdapter("textHtml")
    fun textHtml(textView: TextView, html: String?) {
        html?.let {
            textView.linksClickable = true
            textView.movementMethod = LinkMovementMethod.getInstance()
            textView.text = HtmlCompat.fromHtml(it, HtmlCompat.FROM_HTML_MODE_LEGACY)
        }
    }
    @SuppressLint("SimpleDateFormat")
    @JvmStatic
    @BindingAdapter("toDateString", "defaultText", "toFormat", "fromFormat", requireAll = false)
    fun toDateString(
        textView: TextView,
        date: String?,
        defaultText: String? = null,
        toFormat: String? = null,
        fromFormat: String? = null
    ) {
        if (!date.isNullOrEmpty() && date != "0000-00-00") {
            val dateFormat = SimpleDateFormat(fromFormat?:"yyyy-MM-dd")
            //if (fromFormat?.contains("Z") == true) dateFormat.timeZone = TimeZone.getTimeZone("GMT")
            val dateFormatNew = SimpleDateFormat(toFormat ?: "dd MMMM yyyy", Locale.getDefault()
            )
            try {
                val today = dateFormat.parse(date)
                textView.text = if (today != null) {
                    dateFormatNew.format(today)
                } else {
                    "{date error}"
                }
            } catch (e: ParseException) {
                Log.e(this::class.java.simpleName, "toDateString error $e")
            }
        } else if (defaultText != null) textView.text = defaultText
    }

    @SuppressLint("SimpleDateFormat")
    @JvmStatic
    @BindingAdapter("toTimeString", "timeFormat", requireAll = false)
    fun toTimeString(textView: TextView, timestamp: Long, newDateFormat: String = "dd MMMM yyyy HH:mm") {
        try {
            if (timestamp != 0L) {
                val dateStr = Date(if (timestamp < 1000000000000L) timestamp * 1000 else timestamp)
                val format = SimpleDateFormat(newDateFormat)
                val date1 = format.format(dateStr)
                textView.text = date1.toString()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    @BindingAdapter("toRatingString")
    fun toRatingString(textView: TextView, rating: Float) {
        textView.text= when(rating) {
            1.0f -> "Peculiarity $rating"
            2.0f -> "Bad $rating"
            3.0f -> "Pretty $rating"
            4.0f -> "Good $rating"
            5.0f -> "Very good $rating"
            else -> ""
        }
    }

    @JvmStatic
    @BindingAdapter("formatRupiah", "priceDiscount", requireAll = false)
    fun formatRupiah(textView: TextView, price: Int, discount: Int?) {
        textView.text = if (discount == null) price.toRupiah() else price.toRupiah(discount)
        if (price < 0) textView.invisible()
    }

    @JvmStatic
    @BindingAdapter("formatRupiah", "priceDiscount", requireAll = false)
    fun formatRupiah(textView: TextView, price: Double, discount: Int?) {
        textView.text = if (discount == null) price.toRupiah() else price.toRupiah(discount)
        if (price < 0) textView.invisible()
    }


    @JvmStatic
    @BindingAdapter("priceBefore", "princeNew", requireAll = true)
    fun formatDiscount(textView: TextView, priceBefore: Double, princeNew: Double) {
        val discount = (100 * (priceBefore - princeNew) / priceBefore).toInt()
        textView.text = textView.context.getString(R.string.discount, "$discount%")
    }

    @JvmStatic
    @BindingAdapter("formatCardNumber")
    fun formatCardNumber(textView: TextView, cardNumber: String?) {
        textView.text = cardNumber?.formatCardNumber()
    }

    @JvmStatic
    @BindingAdapter("number", "textStar", "textEnd", requireAll = false)
    fun textNumberFormat(textView: TextView, number: Int, textStar: String?, textEnd: String?) {
        textView.text = if (textStar != null) "$textStar $number" else if (textEnd != null) "$number $textEnd" else "$textStar $number $textEnd"
    }

    @JvmStatic
    @BindingAdapter("number", "textStar", "textEnd", requireAll = false)
    fun textNumberFormat(textView: TextView, number: Int,@StringRes textStar: Int?, @StringRes textEnd: Int?) {
        textView.text = if (textStar != null && textEnd != null) "${textView.resources.getString(
            textStar
        )} $number ${textView.resources.getString(textEnd)}"
                else if (textStar != null) "${textView.resources.getString(textStar)} $number" else
                    if (textEnd != null) "$number ${textView.resources.getString(textEnd)}" else ""
    }

    @JvmStatic
    @BindingAdapter("toPercentage", "total")
    fun percentage(textView: TextView, sold: Int, total: Int) {
        if (sold > 0) {
            textView.text = ((sold * 100) / total).toString()
        }
    }

    @JvmStatic
    @BindingAdapter("priceStrike")
    fun priceStrike(textView: TextView, number: Int) {
        textView.text = number.toRupiah()
        textView.paintFlags = textView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
    }

    @JvmStatic
    @BindingAdapter("priceStrike")
    fun priceStrike(textView: TextView, number: Double) {
        textView.text = number.toRupiah()
        textView.paintFlags = textView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
    }

    @JvmStatic
    @BindingAdapter("prettyCount", "android:text")
    fun prettyCount(textView: TextView, count: Long, text: String) {
        textView.text = if (count < 1000) "$text $count"
        else {
            val exp = (ln(count.toDouble()) / ln(1000.0)).toInt()
            "$text ${String.format("%.1f %c", count / 1000.0.pow(exp.toDouble()), "kMGTPE"[exp - 1])}"
        }
    }

    @JvmStatic
    @BindingAdapter("lastTime")
    fun lastTime(textView: TextView, mTime: Long?) {
        textView.text = lastTime(mTime?:0)
        textView.visible()
    }

    @SuppressLint("SimpleDateFormat")
    @JvmStatic
    @BindingAdapter("lastTime", "format", requireAll = false)
    fun lastTime(textView: TextView, date: String?, format: String? = null) {
        try {
            if (date!!.isNotEmpty() && date != "0000-00-00") {
                val dateFormat = SimpleDateFormat(format?:"yyyy-MM-dd")
                if (format?.contains("Z") == true) dateFormat.timeZone = TimeZone.getTimeZone("GMT")
                val today = dateFormat.parse(date)
                textView.text = lastTime(today!!.time)
                textView.visible()
            }
        } catch (e: Exception) {

        }
    }

    private fun lastTime(mTime: Long) : String {
        val secondMillis = 1000L
        val minuteMillis = 60 * secondMillis
        val hourMillis = 60 * minuteMillis
        val dayMillis = 24 * hourMillis
        val weekMillis = 7 * dayMillis
        val monthMillis = 4 * weekMillis
        val yearMillis = 12 * monthMillis
        val now = Date().time
        val time = if (mTime < 1000000000000L) mTime * 1000 else mTime
        if (time > now || time <= 0) return ""

        val diff = now - time
        return when {
            diff < minuteMillis -> "${diff / secondMillis} detik yang lalu"
            diff < 60 * minuteMillis -> "${diff / minuteMillis} menit yang lalu"
            diff < 24 * hourMillis -> "${diff / hourMillis} jam yang lalu"
            diff < 7 * dayMillis -> "${diff / dayMillis} Hari yang lalu"
            diff < 4 * weekMillis -> "${diff / weekMillis} Minggu yang lalu"
            diff < 12 * monthMillis -> "${diff / monthMillis} Bulan yang lalu"
            else -> "${diff / yearMillis} Tahun yang lalu"
        }
    }
}