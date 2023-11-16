package com.app.common.ui.trainline

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.app.common.R
import com.app.core.extensions.dpToPx
import com.app.core.extensions.formatDateTime
import com.app.core.extensions.toDateFormat
import com.app.core.extensions.toTimeLongFormat
import java.util.Date
import kotlin.math.roundToInt


/**
 * Created by Anonim date on 13/09/2022.
 * Bengkulu, Indonesia.
 * Copyright (c) Company. All rights reserved.
 **/
class TrainLineRacyClearView : RecyclerView {

    private var icon: Drawable? = null
    private val maps = HashMap<Int, List<Int>>()

    private var iconPositionTrain: Drawable? = null
    private val trains = HashMap<Int, RealTrain>()
    private var isStart = false

    private val updater = object : Runnable{
        override fun run() {
            if (isVisible) {
                invalidate()
            }
            if (isStart) postDelayed(this, 50)
        }
    }


    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initialize()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initialize()
    }

    fun setTransitDrawable(drawable: Drawable?) {
        icon = drawable
        invalidate()
    }

    fun setPositionDrawable(drawable: Drawable?) {
        iconPositionTrain = drawable
        invalidate()
    }

    fun addLineTransit(position: Int, list: List<Int>?) {
        list?.let { maps[position] = it }
    }

    fun setTrainPosition(position: Int, train: RealTrain?) {
        train?.let {
            trains[position] = it
        }
        if (trains.isEmpty()) {
            isStart = false
        } else {
            if (!isStart) {
                isStart = true
                post(updater)
            }
        }
    }

    fun clear() {
        maps.clear()
        trains.clear()
    }

    private fun initialize() {
        icon = AppCompatResources.getDrawable(context, R.drawable.background_circle)

    }

    override fun onDraw(canvas: Canvas) {
        if (icon != null) {
            transitTrains(canvas)
        }
        if (iconPositionTrain != null) {
            realtimePositionTrains(canvas)
        }
    }

    private fun transitTrains(canvas: Canvas) {
        canvas.save()
        val left: Int
        val right: Int
        if (clipToPadding) {
            left = paddingLeft
            right = width - paddingRight
            canvas.clipRect(left, paddingTop, right, height - paddingBottom)
        } else {
            left = 0
        }

        val childCount = childCount
        var isTransit = false
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            val mBounds = Rect()
            getDecoratedBoundsWithMargins(child, mBounds)
            var bottom = mBounds.top + child.translationY.roundToInt()
            val size = 25f.dpToPx()
            if (maps[i] != null) {
                if (isTransit) {
                    bottom = (mBounds.bottom + child.translationY.roundToInt()) + (size/2) + 4f.dpToPx()
                }
                isTransit = true

                var itemLeft = left
                maps[i]?.forEachIndexed { _, color ->
                    if (color != 0) {
                        if (icon!! is LayerDrawable) {
                            val drawable =(icon as LayerDrawable).findDrawableByLayerId(android.R.id.background)
                            if (drawable is GradientDrawable) {
                                drawable.color = ColorStateList.valueOf(color)
                            }
                        } else if (icon!! is GradientDrawable) {
                            (icon as GradientDrawable).color = ColorStateList.valueOf(color)
                        }
                    }
                    icon!!.setBounds(itemLeft , (bottom - size) + 4f.dpToPx(), itemLeft + size, bottom + 4f.dpToPx())
                    icon!!.draw(canvas)
                    itemLeft += 4f.dpToPx() + size
                }
            } else {
                isTransit = false
            }
        }
        canvas.restore()
    }

    private fun realtimePositionTrains(canvas: Canvas) {
        canvas.save()
        val childCount = childCount
        for (i in 0 until childCount) {
            val posStart = getPosition(i)
            val posEnd = getPosition(i + 1)
            if (posStart !=null && posEnd !=null) {
                val mBounds = Rect()
                if (trains[i] !=null) {
                    val realitme = trains[i]!!.also { it.update() }
                    val startYC = posStart.top + (posStart.height / 2)
                    val endYC = posEnd.top + (posEnd.height / 2)
                    val elapsed = endYC - startYC
                    val translateY = (elapsed * realitme.delta).toInt()

                    val r = posStart.width * 0.29
                    val l = r - iconPositionTrain!!.intrinsicWidth
                    val t = startYC - (iconPositionTrain!!.intrinsicHeight / 2)
                    val b = t - iconPositionTrain!!.intrinsicHeight
                    mBounds.set(l.toInt(), t + translateY, r.toInt(), b + translateY)
                    iconPositionTrain!!.bounds = mBounds
                    iconPositionTrain!!.draw(canvas)

                    val rr = (posStart.width * 0.31) - r
                    val c = mBounds.top - (mBounds.height() /2)
                    canvas.drawLine(r.toFloat() - 2f.dpToPx(), c.toFloat(), r.toFloat() + rr.toFloat(), c.toFloat(), Paint().apply {
                        color = Color.parseColor("#e6251c")
                        strokeWidth = 1.5f.dpToPx().toFloat()
                    })
                }
            }
        }

        canvas.restore()
    }

    private fun getPosition(index: Int) : View? {
        if (index > childCount || index <  0) return null

        return getChildAt(index)
    }

    class RealTrain(var start: Long, var end: Long) {

        val duration: Long
        get() = end - start
        val delta: Float
        get() = mDelta

        private var mDelta: Float = 0f
        private var animated = false
        private var animationStart = 0L

        init {
            start()
            Log.e("RealTrain", "end $duration ${animationStart.toDateFormat("yyyy-MM-dd HH:mm:ss")}")
        }
        fun start() {
            animated = true
            val day = "yyyy-MM-dd".formatDateTime().toTimeLongFormat("yyyy-MM-dd")
            animationStart = day + start + (7 * 60 * 60 * 1000)
        }

        fun end() {
            animated = false
        }

        fun update() {
            if (!animated || animationStart > System.currentTimeMillis()) return

            val elapsed = (System.currentTimeMillis() - animationStart).toFloat()
            mDelta = elapsed / duration
            if (elapsed >= duration) {
                end()
            }
        }
    }

}