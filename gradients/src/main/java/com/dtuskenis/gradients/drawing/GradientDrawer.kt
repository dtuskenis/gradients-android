package com.dtuskenis.gradients.drawing

import android.graphics.*
import com.dtuskenis.gradients.core.GradientComponents

internal class GradientDrawer(private val createShader: (drawingRegion: RectF,
                                                         colors: IntArray,
                                                         positions: FloatArray) -> Shader) {

    private val gradientPaint = Paint()

    fun drawOn(canvas: Canvas,
               components: GradientComponents,
               drawingRegion: RectF = RectF(0.0f,
                                            0.0f,
                                            canvas.width.toFloat(),
                                            canvas.height.toFloat()),
               porterDuffMode: PorterDuff.Mode? = null) {

        val colors = components.map { it.color.toArgb() }
        val positions = components.map { it.relativePosition }

        gradientPaint.xfermode = porterDuffMode?.let { PorterDuffXfermode(it) }
        gradientPaint.shader = createShader(drawingRegion,
                                            colors.toIntArray(),
                                            positions.toFloatArray())

        canvas.drawRect(drawingRegion, gradientPaint)
    }
}