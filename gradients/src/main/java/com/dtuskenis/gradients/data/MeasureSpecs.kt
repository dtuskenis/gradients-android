package com.dtuskenis.gradients.data

import android.util.Size
import android.view.View
import android.view.View.MeasureSpec

internal data class MeasureSpecs(private val widthSpec: Int,
                                 private val heightSpec: Int) {

    data class Spec(private val rawSpec: Int) {

        enum class Mode {
            EXACTLY,
            AT_MOST,
            ;
        }

        val mode: Mode? = when (View.MeasureSpec.getMode(rawSpec)) {
            MeasureSpec.EXACTLY -> Mode.EXACTLY
            MeasureSpec.AT_MOST -> Mode.AT_MOST
            else -> null
        }

        val size: Int = MeasureSpec.getSize(rawSpec)
    }

    val width: Spec = Spec(widthSpec)

    val height: Spec = Spec(heightSpec)

    val size = Size(width.size, height.size)
}