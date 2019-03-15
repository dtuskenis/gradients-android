package com.dtuskenis.gradients.utils

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.annotation.DrawableRes
import android.support.annotation.StyleableRes
import android.util.AttributeSet

internal class AttributesResolver(private val context: Context,
                                  attributeSet: AttributeSet?,
                                  @StyleableRes attributes: IntArray) {

    private val typedArray = context.obtainStyledAttributes(attributeSet, attributes)

    fun getDrawable(@StyleableRes index: Int, @DrawableRes fallbackId: Int): Drawable =
            typedArray.getDrawable(index) ?: context.getDrawable(fallbackId) !!

    fun cleanUp() {
        typedArray.recycle()
    }
}