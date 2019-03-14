package com.dtuskenis.gradients.extensions

import android.graphics.RectF
import android.view.View

internal fun View.setContentDimensionsTo(rect: RectF) {
    rect.set(paddingStart.toFloat(),
             paddingTop.toFloat(),
             width.toFloat() - paddingEnd,
             height.toFloat() - paddingBottom)
}