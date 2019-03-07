package com.dtuskenis.gradients

import android.view.View
import kotlin.math.min

internal object LayoutMeasurer {

    fun measure(widthMeasureSpec: Int,
                heightMeasureSpec: Int,
                desiredWidth: Int? = null,
                desiredHeight: Int? = null,
                measuredDimenstionConsumer: (Int, Int) -> Unit) {
        val widthMode = View.MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = View.MeasureSpec.getSize(widthMeasureSpec)

        val heightMode = View.MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = View.MeasureSpec.getSize(heightMeasureSpec)

        val width = determineDimension(widthMode,
                                       widthSize,
                                       desiredWidth)

        val height = determineDimension(heightMode,
                                        heightSize,
                                        desiredHeight)

        measuredDimenstionConsumer(width, height)
    }

    private fun determineDimension(specMode: Int,
                                   specSize: Int,
                                   desiredSize: Int?): Int = when (specMode) {
        View.MeasureSpec.EXACTLY -> specSize
        View.MeasureSpec.AT_MOST -> desiredSize?.let { min(it, specSize) } ?: specSize
        else -> desiredSize ?: 0
    }
}