package com.dtuskenis.gradients

import android.graphics.LinearGradient
import android.graphics.Shader

internal object GradientDrawers {

    val linear = GradientDrawer { drawingRegion, colors, positions -> LinearGradient(0.0f,
                                                                                     0.0f,
                                                                                     drawingRegion.width(),
                                                                                     0.0f,
                                                                                     colors,
                                                                                     positions,
                                                                                     Shader.TileMode.MIRROR) }
}