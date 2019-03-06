package com.dtuskenis.gradients

import android.support.annotation.ColorInt
import java.lang.RuntimeException

interface LinearGradient {

    fun setComponents(newComponents: List<Component>)

    data class Component(@ColorInt val colorValue: Int,
                         val relativePosition: Float) {
        init {
            if (relativePosition !in 0.0f..1.0f) {
                throw RuntimeException("relativePosition should be in [0..1]")
            }
        }
    }
}