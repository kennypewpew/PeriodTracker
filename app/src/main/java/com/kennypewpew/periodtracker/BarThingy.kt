package com.kennypewpew.periodtracker

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

class BarThingy (context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr)
{
    private var min = 0
    private var max = 40
    private var range = 7
    private var gap = 28


    var xpad = (paddingLeft + paddingRight).toFloat()
    var ypad = (paddingTop + paddingBottom).toFloat()

    var xMid = 0 as Int
    var yMid = 0 as Int

    var ww = width.toFloat() - xpad
    var hh = height.toFloat() - ypad

    var longRect = Rect()
    var smallRect = Rect()

    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0x101010
        textSize = 20.0F
        style = Paint.Style.FILL
    }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        // Paint styles used for rendering are initialized here. This
        // is a performance optimization, since onDraw() is called
        // for every screen refresh.
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 55.0f
        typeface = Typeface.create("", Typeface.BOLD)
        color = Color.GRAY
    }

    private val paintShort = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        // Paint styles used for rendering are initialized here. This
        // is a performance optimization, since onDraw() is called
        // for every screen refresh.
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 55.0f
        typeface = Typeface.create("", Typeface.BOLD)
        color = Color.RED
    }

    private val paintText = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        // Paint styles used for rendering are initialized here. This
        // is a performance optimization, since onDraw() is called
        // for every screen refresh.
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 55.0f
        typeface = Typeface.create("", Typeface.BOLD)
        color = Color.BLUE
    }


    fun setMin(v: Int) {
        min = v
    }
    fun getMin(v: Int): Int {
        return min
    }

    fun setMax(v: Int) {
        max = v
    }
    fun getMax(v: Int): Int {
        return max
    }

    fun setRange(v: Int) {
        range = v
    }
    fun getRange(v: Int): Int {
        return range
    }

    fun setGap(v: Int) {
        gap = v
    }
    fun getGap(v: Int): Int {
        return gap
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

          // requested width and mode
        val reqWidth = MeasureSpec.getSize(widthMeasureSpec)
        val reqWidthMode = MeasureSpec.getMode(widthMeasureSpec)

        // requested height and mode
        val reqHeight = MeasureSpec.getSize(heightMeasureSpec)
        val reqHeightMode = MeasureSpec.getMode(heightMeasureSpec)

        // your choice
        val desiredWidth: Int =  reqWidth// TODO("Define your desired width")
        val desiredHeight: Int = 100// TODO("Define your desired height")

        val width = when (reqWidthMode) {
            MeasureSpec.EXACTLY -> reqWidth
            MeasureSpec.UNSPECIFIED -> desiredWidth
            else -> Math.min(reqWidth, desiredWidth) // AT_MOST condition
        }

        val height = when (reqHeightMode) {
            MeasureSpec.EXACTLY -> reqHeight
            MeasureSpec.UNSPECIFIED -> desiredHeight
            else -> Math.min(reqHeight, desiredHeight) // AT_MOST condition
        }

        setMeasuredDimension(width, height)
    }

    private fun updateRectangles() {
        xpad = (paddingLeft + paddingRight).toFloat()
        ypad = (paddingTop + paddingBottom).toFloat()

        val xHalfPad = (xpad / 2).toInt()
        val yHalfPad = (ypad / 2).toInt()

        ww = width.toFloat() - xpad
        hh = height.toFloat() - ypad

        val x1 = xHalfPad
        val x2 = ww.toInt() - xHalfPad
        val y1 = yHalfPad
        val y2 = hh.toInt() - yHalfPad

        val xLongProp = gap.toFloat() / max.toFloat()
        val xLongRadius = (xLongProp * ww) / 2.0F

        val yLongRectCenter = (y1 + y2) / 2
        val longRectProp = 0.5F
        val yLongRadius = longRectProp * height / 2.0F
        val y1l = (yLongRectCenter - yLongRadius).toInt()
        val y2l = (yLongRectCenter + yLongRadius).toInt()

        longRect = Rect(x1,y2l,x2,y1l)

        val shortRectRadius = ((range.toFloat() / (max - min).toFloat()) / 2.0F) * ww
        val xCenterShort = (x2 + x1) / 2
        val x1s = (xCenterShort - shortRectRadius).toInt()
        val x2s = (xCenterShort + shortRectRadius).toInt()

        smallRect = Rect(x1s, y2, x2s, y1)

        xMid = xCenterShort
        yMid = height/2 + yHalfPad

        val x1l = xMid - xLongRadius.toInt()
        val x2l = xMid + xLongRadius.toInt()

        longRect = Rect(x1l,y2l,x2l,y1l)
    }

    @SuppressLint("DrawAllocation")
    override fun onLayout(changed: Boolean, lf: Int, tp: Int, rt: Int, bt: Int) {
        if ( changed ) {
            updateRectangles()
        }
    }

        override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.apply {
            canvas.drawRect(longRect, paint)
            canvas.drawRect(smallRect, paintShort)
            canvas.drawText(range.toString(), xMid.toFloat(), yMid.toFloat(), paintText)
            canvas.drawText(gap.toString()  , xpad.toFloat()+paintText.measureText(gap.toString()), yMid.toFloat(), paintText)

        }
    }
}