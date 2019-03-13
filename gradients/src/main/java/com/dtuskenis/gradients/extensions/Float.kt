package com.dtuskenis.gradients.extensions

import kotlin.math.PI

internal fun Float.toDegrees(): Float = this * 180f / PI.toFloat()

internal fun Float.toRadians(): Float = this / 180f * PI.toFloat()

internal fun Float.scale(srcRange: ClosedRange<Float>, dstRange: ClosedRange<Float>): Float {
    if (this !in srcRange) {
        throw RuntimeException("$this !in $srcRange")
    }

    val srcMin = srcRange.start
    val srcMax = srcRange.endInclusive

    val dstMin = dstRange.start
    val dstMax = dstRange.endInclusive

    val localSrcMax = srcMax - srcMin
    val localSrcValue = this - srcMin

    val localDstMax = dstMax - dstMin

    return (localSrcValue / localSrcMax) * localDstMax + dstMin
}