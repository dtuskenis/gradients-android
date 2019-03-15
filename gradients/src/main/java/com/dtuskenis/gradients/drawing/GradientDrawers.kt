package com.dtuskenis.gradients.drawing

import android.graphics.*
import com.dtuskenis.gradients.extensions.innerCircleRadius

internal object GradientDrawers {

    val linear = GradientDrawer { drawingRegion, colors, positions -> LinearGradient(0.0f,
                                                                                     0.0f,
                                                                                     drawingRegion.width(),
                                                                                     0.0f,
                                                                                     colors,
                                                                                     positions,
                                                                                     Shader.TileMode.CLAMP) }

    val sweep = GradientDrawer { drawingRegion, colors, positions -> SweepGradient(drawingRegion.centerX(),
                                                                                   drawingRegion.centerY(),
                                                                                   colors,
                                                                                   positions) }

    val radial = GradientDrawer { drawingRegion, colors, positions -> RadialGradient(drawingRegion.centerX(),
                                                                                     drawingRegion.centerY(),
                                                                                     drawingRegion.innerCircleRadius(),
                                                                                     colors,
                                                                                     positions,
                                                                                     Shader.TileMode.CLAMP) }
}