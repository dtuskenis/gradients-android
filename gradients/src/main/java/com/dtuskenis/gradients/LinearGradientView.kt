package com.dtuskenis.gradients

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

class LinearGradientView: View, LinearGradient {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    private val gradientDrawer = LinearGradientDrawer()

    override fun setComponents(newComponents: List<LinearGradient.Component>) {
        gradientDrawer.components = newComponents

        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        gradientDrawer.drawOn(canvas)
    }
}