package com.dtuskenis.gradients.extensions

import android.graphics.PointF
import android.graphics.Rect
import android.graphics.RectF
import kotlin.math.min
import kotlin.math.roundToInt

internal fun RectF.center(): PointF = PointF(centerX(), centerY())

internal fun RectF.innerCircleRadius(): Float = min(width(), height()) / 2

internal fun RectF.contains(point: PointF): Boolean = contains(point.x, point.y)

internal fun RectF.round(): Rect = Rect(left.roundToInt(),
                                        top.roundToInt(),
                                        right.roundToInt(),
                                        bottom.roundToInt())