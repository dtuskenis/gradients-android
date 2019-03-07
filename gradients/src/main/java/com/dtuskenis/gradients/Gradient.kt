package com.dtuskenis.gradients

import android.support.annotation.ColorRes
import java.lang.RuntimeException

interface Gradient {

    var components: GradientComponents

    data class Color(@ColorRes var rawValue: Int)

    data class Component(val color: Color,
                         val relativePosition: Float) {
        init {
            if (relativePosition !in 0.0f..1.0f) {
                throw RuntimeException("relativePosition should be in [0..1]")
            }
        }
    }
}