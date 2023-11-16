package com.app.common.ui.util

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import java.text.DecimalFormat
import java.text.ParseException


/**
 * Created by Anonim date on 12/08/2022.
 * Bengkulu, Indonesia.
 * Copyright (c) Company. All rights reserved.
 **/
class NumberTextWatcher(private val et: EditText) : TextWatcher {
    private var df: DecimalFormat = DecimalFormat("#,###.##")
    private var dfnd: DecimalFormat = DecimalFormat("#,###")
    private var hasFractionalPart = false

    init {
        df = DecimalFormat("#,###.##")
        df.isDecimalSeparatorAlwaysShown = true
        dfnd = DecimalFormat("#,###")
        hasFractionalPart = false
    }

    private val TAG = "NumberTextWatcher"

    override fun afterTextChanged(s: Editable) {
        et.removeTextChangedListener(this)
        try {
            val inilen: Int = et.text.length
            val v: String = s.toString().replace(
                java.lang.String.valueOf(
                    df.decimalFormatSymbols.groupingSeparator
                ), ""
            )
            val n: Number = df.parse(v) as Number
            val cp = et.selectionStart
            if (hasFractionalPart) {
                et.setText(df.format(n))
            } else {
                et.setText(dfnd.format(n))
            }
            val endlen: Int = et.text.length
            val sel = cp + (endlen - inilen)
            if (sel > 0 && sel <= et.text.length) {
                et.setSelection(sel)
            } else {
                // place cursor at the end?
                et.setSelection(et.text.length - 1)
            }
        } catch (nfe: NumberFormatException) {
            // do nothing?
        } catch (e: ParseException) {
            // do nothing?
        }
        et.addTextChangedListener(this)
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        hasFractionalPart =
            s.toString().contains(
                java.lang.String.valueOf(
                    df.decimalFormatSymbols.decimalSeparator
                )
            )
    }
}