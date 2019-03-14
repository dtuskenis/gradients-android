package com.dtuskenis.gradients.extensions

import android.graphics.RectF
import android.util.Size
import android.view.View
import com.dtuskenis.gradients.data.MeasureSpecs
import kotlin.math.min

internal fun View.setContentDimensionsTo(rect: RectF) {
    rect.set(paddingStart.toFloat(),
             paddingTop.toFloat(),
             width.toFloat() - paddingEnd,
             height.toFloat() - paddingBottom)
}

internal fun View.getMeasuredDimensions(widthMeasureSpec: Int,
                                        heightMeasureSpec: Int,
                                        desiredWidth: ((Size) -> Int)? = null,
                                        desiredHeight: ((Size) -> Int)? = null,
                                        onComplete: (Size) -> Unit) {
    val specs = MeasureSpecs(widthMeasureSpec, heightMeasureSpec)

    fun determineDimension(spec: MeasureSpecs.Spec,
                           desiredSize: Int?): Int = when (spec.mode) {
        MeasureSpecs.Spec.Mode.EXACTLY -> spec.size
        MeasureSpecs.Spec.Mode.AT_MOST -> desiredSize?.let { min(it, spec.size) } ?: spec.size
        else -> desiredSize ?: 0
    }

    val width = determineDimension(specs.width, desiredWidth?.invoke(specs.size))
    val height = determineDimension(specs.height, desiredHeight?.invoke(specs.size))

    onComplete(Size(width, height))
}