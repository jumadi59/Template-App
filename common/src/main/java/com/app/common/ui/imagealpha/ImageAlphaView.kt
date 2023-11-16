package com.app.common.ui.imagealpha

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet


/**
 * Created by Anonim date on 06/09/2022.
 * Bengkulu, Indonesia.
 * Copyright (c) Company. All rights reserved.
 **/
class ImageAlphaView : androidx.appcompat.widget.AppCompatImageView {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun setImageDrawable(drawable: Drawable?) {
        if (drawable is BitmapDrawable) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                drawable.bitmap = replaceColor(drawable.bitmap)
                super.setImageDrawable(drawable)
            } else {
                try {
                    super.setImageDrawable(BitmapDrawable(resources, replaceColor(drawable.bitmap)))
                } catch (e: Exception) {
                    super.setImageDrawable(drawable)
                }
            }
        } else {
            super.setImageDrawable(drawable)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

    }

    private fun replaceColor(src: Bitmap?): Bitmap? {
        if (src == null) return null
        val width = src.width
        val height = src.height
        val pixels = IntArray(width * height)
        src.getPixels(pixels, 0, 1 * width, 0, 0, width, height)
        for (x in pixels.indices) {
            if (pixels[x] == Color.WHITE) pixels[x] = Color.TRANSPARENT
        }
        return Bitmap.createBitmap(pixels, width, height, Bitmap.Config.ARGB_8888)
    }

}