package com.dtuskenis.gradients

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Shader

internal object LinearGradientDrawer {

    private val gradientPaint = Paint()

    fun drawOn(canvas: Canvas,
               components: GradientComponents) {

        val width = canvas.width.toFloat()
        val height = canvas.height.toFloat()

        val colors = components.map { it.color.rawValue }
        val positions = components.map { it.relativePosition }

        gradientPaint.shader =
            android.graphics.LinearGradient(0.0f,
                                            0.0f,
                                            width,
                                            0.0f,
                                            colors.toIntArray(),
                                            positions.toFloatArray(),
                                            Shader.TileMode.MIRROR)

        canvas.drawRect(0.0f,
                        0.0f,
                        width ,
                        height,
                        gradientPaint)
    }
}