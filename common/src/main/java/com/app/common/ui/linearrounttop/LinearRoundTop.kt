package com.app.common.ui.linearrounttop

import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.widget.NestedScrollView
import com.app.common.R


/**
 * Created by Anonim date on 19/01/2023.
 * Bengkulu, Indonesia.
 * Copyright (c) Company. All rights reserved.
 **/
class LinearRoundTop : LinearLayout {

    private var nestedScrollView: NestedScrollView? = null
    private var radius = 0
    private var mScrollY = 0
    private var isSubParent = false

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initialize()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initialize()
    }

    private val initializeView = object : Runnable{
        override fun run() {
            if (nestedScrollView == null) {
                if (parent is NestedScrollView) {
                    initializeNested(parent as NestedScrollView)
                } else if (parent.parent is NestedScrollView) {
                    isSubParent = true
                    initializeNested(parent.parent as NestedScrollView)
                } else postDelayed(this, 60)
            } else {
                postDelayed(this, 60)
            }
        }
    }

    private fun initialize() {
        radius = resources.getDimensionPixelSize(R.dimen.radius_xxxl)
        post(initializeView)
    }

    private fun initializeNested(nestedScrollView: NestedScrollView) {
        this.nestedScrollView = nestedScrollView
        nestedScrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { _, _, scrollY, _, _ ->
            mScrollY = scrollY - this.top
            invalidate()
        })
    }

    override fun draw(canvas: Canvas) {
        val clipPath = Path()
        val corners = floatArrayOf(
            (radius).toFloat(), (radius).toFloat(),  // Top left radius in px
            (radius).toFloat(), (radius).toFloat(),  // Top right radius in px
            0f, 0f,  // Bottom right radius in px
            0f, 0f // Bottom left radius in px
        )
        clipPath.addRoundRect(RectF(0f,
            computeVerticalScrollOffset().toFloat(),
            computeHorizontalScrollExtent().toFloat(),
            computeVerticalScrollRange().toFloat()), corners, Path.Direction.CW)
        canvas.clipPath(clipPath)
        super.draw(canvas)
    }

    override fun computeVerticalScrollOffset(): Int {
        return if (mScrollY < 0) 0 else mScrollY
    }

    override fun computeVerticalScrollRange(): Int {
        return (mScrollY + (nestedScrollView?.height?:height))
    }
}