package com.dtuskenis.gradients.extensions

import android.graphics.PointF
import kotlin.math.hypot

internal fun PointF.distanceTo(other: PointF): Float = hypot(other.y - y, other.x - x)