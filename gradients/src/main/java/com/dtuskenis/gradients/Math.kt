package com.dtuskenis.gradients

internal fun scaleFloat(value: Float,
                        srcRange: ClosedRange<Float>,
                        dstRange: ClosedRange<Float>): Float {
    if (value !in srcRange) {
        throw RuntimeException("$value !in $srcRange")
    }

    val srcMin = srcRange.start
    val srcMax = srcRange.endInclusive

    val localSrcMax = srcMax - srcMin
    val localSrcValue = value - srcMin

    val dstMin = dstRange.start
    val dstMax = dstRange.endInclusive

    val localDstMax = dstMax - dstMin

    return (localSrcValue / localSrcMax) * localDstMax + dstMin
}