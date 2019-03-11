package com.dtuskenis.gradients

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader

internal object LinearGradientDrawer {

    private val gradientPaint = Paint()

    fun drawOn(canvas: Canvas,
               components: GradientComponents,
               drawingRegion: RectF = RectF(0.0f,
                                            0.0f,
                                            canvas.width.toFloat(),
                                            canvas.height.toFloat())) {

        val colors = components.map { it.color.toArgb() }
        val positions = components.map { it.relativePosition }

        gradientPaint.shader =
            android.graphics.LinearGradient(0.0f,
                                            0.0f,
                                            drawingRegion.width(),
                                            0.0f,
                                            colors.toIntArray(),
                                            positions.toFloatArray(),
                                            Shader.TileMode.MIRROR)

        canvas.drawRect(drawingRegion, gradientPaint)
    }
}