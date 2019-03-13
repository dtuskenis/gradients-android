package com.dtuskenis.gradients

import android.view.View
import kotlin.math.min

internal object LayoutMeasurer {

    data class SpecSize(val width: Int,
                        val height: Int)

    fun measure(widthMeasureSpec: Int,
                heightMeasureSpec: Int,
                desiredWidth: ((SpecSize) -> Int)? = null,
                desiredHeight: ((SpecSize) -> Int)? = null,
                measuredDimensionConsumer: (Int, Int) -> Unit) {
        val specSize = SpecSize(width = View.MeasureSpec.getSize(widthMeasureSpec),
                                height = View.MeasureSpec.getSize(heightMeasureSpec))

        val widthMode = View.MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = View.MeasureSpec.getMode(heightMeasureSpec)

        val width = determineDimension(widthMode,
                                       specSize.width,
                                       desiredWidth?.invoke(specSize))

        val height = determineDimension(heightMode,
                                        specSize.height,
                                        desiredHeight?.invoke(specSize))

        measuredDimensionConsumer(width, height)
    }

    private fun determineDimension(specMode: Int,
                                   specSize: Int,
                                   desiredSize: Int?): Int = when (specMode) {
        View.MeasureSpec.EXACTLY -> specSize
        View.MeasureSpec.AT_MOST -> desiredSize?.let { min(it, specSize) } ?: specSize
        else -> desiredSize ?: 0
    }
}