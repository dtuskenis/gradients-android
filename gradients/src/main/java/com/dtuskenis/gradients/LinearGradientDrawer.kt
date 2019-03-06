package com.dtuskenis.gradients

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Shader

internal class LinearGradientDrawer {

    private val gradientPaint = Paint()

    var components: List<LinearGradient.Component> = emptyList()
        get() = field.toList()
        set(value) { field = value.toList() }

    fun drawOn(canvas: Canvas,
               horizontalMargin: Float = 0.0f) {

        val width = canvas.width.toFloat()
        val height = canvas.height.toFloat()

        val colors = components.map { it.colorValue }
        val positions = components.map { it.relativePosition }

        gradientPaint.shader =
            android.graphics.LinearGradient(0.0f,
                                            0.0f,
                                            width,
                                            0.0f,
                                            colors.toIntArray(),
                                            positions.toFloatArray(),
                                            Shader.TileMode.MIRROR)

        canvas.drawRect(horizontalMargin,
                        0.0f,
                        width - horizontalMargin,
                        height,
                        gradientPaint)
    }
}