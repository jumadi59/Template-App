package com.app.common.ui.extensions

import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable


/**
 * Created by Anonim date on 15/09/2022.
 * Bengkulu, Indonesia.
 * Copyright (c) Company. All rights reserved.
 **/

fun Drawable.colorInfoTrain(color: Int) : Drawable {
    if (this is LayerDrawable) {
        val drawable = this.findDrawableByLayerId(android.R.id.icon)
        drawable.colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN)
    }
    return this
}