package com.dtuskenis.gradients

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

class LinearGradientView: View, Gradient {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    override var components = GradientComponents()
        set(value) {
            field = value
            invalidate()
        }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        LinearGradientDrawer.drawOn(canvas, components)
    }
}