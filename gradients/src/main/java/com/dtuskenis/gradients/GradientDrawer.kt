package com.dtuskenis.gradients

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader

internal class GradientDrawer(private val createShader: (drawingRegion: RectF,
                                                         colors: IntArray,
                                                         positions: FloatArray) -> Shader) {

    private val gradientPaint = Paint()

    fun drawOn(canvas: Canvas,
               components: GradientComponents,
               drawingRegion: RectF = RectF(0.0f,
                                            0.0f,
                                            canvas.width.toFloat(),
                                            canvas.height.toFloat())) {

        val colors = components.map { it.color.toArgb() }
        val positions = components.map { it.relativePosition }

        gradientPaint.shader = createShader(drawingRegion,
                                            colors.toIntArray(),
                                            positions.toFloatArray())

        canvas.drawRect(drawingRegion, gradientPaint)
    }
}