package com.dtuskenis.gradients

import android.content.Context
import android.graphics.*
import android.support.annotation.ColorInt
import android.util.AttributeSet
import android.view.View
import java.lang.RuntimeException

class LinearGradientView: View {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    private val colorComponents = mutableListOf<ColorComponent>()

    private val gradientPaint = Paint()

    fun setColorComponents(newComponents: List<ColorComponent>) {
        colorComponents.clear()
        colorComponents.addAll(newComponents)

        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val colors = colorComponents.map { it.colorValue }
        val positions = colorComponents.map { it.relativePosition }

        gradientPaint.shader =
            LinearGradient(0.0f,
                           0.0f,
                           width.toFloat(),
                           0.0f,
                           colors.toIntArray(),
                           positions.toFloatArray(),
                           Shader.TileMode.MIRROR)

        canvas.drawRect(0.0f,
                        0.0f,
                        width.toFloat(),
                        height.toFloat(),
                        gradientPaint)
    }

    data class ColorComponent(@ColorInt val colorValue: Int,
                              val relativePosition: Float) {
        init {
            if (relativePosition !in 0.0f..1.0f) {
                throw RuntimeException("relativePosition should be in [0..1]")
            }
        }
    }
}