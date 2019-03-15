package com.dtuskenis.gradients.views

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.dtuskenis.gradients.core.Gradient
import com.dtuskenis.gradients.core.GradientComponents
import com.dtuskenis.gradients.drawing.GradientDrawers

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

        GradientDrawers.linear.drawOn(canvas, components)
    }
}