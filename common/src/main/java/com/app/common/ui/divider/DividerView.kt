package com.app.common.ui.divider

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.app.common.R


/**
 * Created by Anonim date on 09/09/2022.
 * Bengkulu, Indonesia.
 * Copyright (c) Company. All rights reserved.
 **/
class DividerView : View {

    companion object {
        var ORIENTATION_HORIZONTAL = 0
        var ORIENTATION_VERTICAL = 1
    }
    private var mPaint: Paint = Paint()
    private var orientation = 0
    private var a: TypedArray? = null

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        a = context.obtainStyledAttributes(attrs, R.styleable.DividerView, 0, 0)
        initialize()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        a = context.obtainStyledAttributes(attrs, R.styleable.DividerView, 0, 0)
        initialize()
    }

    private fun initialize() {
        val dashGap: Int
        val dashLength: Float
        val dashThickness: Float
        val color: Int
        try {
            dashGap = a!!.getDimensionPixelSize(R.styleable.DividerView_dashGap, 5)
            dashLength = a!!.getDimensionPixelSize(R.styleable.DividerView_dashLength, 5).toFloat()
            dashThickness = a!!.getDimensionPixelSize(R.styleable.DividerView_dashThickness, 3).toFloat()
            color = a!!.getColor(R.styleable.DividerView_color, -0x1000000)
            orientation = a!!.getInt(R.styleable.DividerView_orientation, ORIENTATION_HORIZONTAL)
        } finally {
            a!!.recycle()
        }

        mPaint = Paint()
        mPaint.isAntiAlias = true
        mPaint.color = color
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeWidth = dashThickness
        mPaint.pathEffect = DashPathEffect(floatArrayOf(dashLength, dashGap.toFloat()), 0f)

    }

    override fun onDraw(canvas: Canvas) {
        if (orientation == ORIENTATION_HORIZONTAL) {
            val center = height * .5f
            canvas.drawLine(0f, center, width.toFloat(), center, mPaint)
        } else {
            val center = width * .5f
            canvas.drawLine(center, 0f, center, height.toFloat(), mPaint)
        }
    }
}