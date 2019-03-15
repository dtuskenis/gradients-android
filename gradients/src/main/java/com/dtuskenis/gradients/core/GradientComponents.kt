package com.dtuskenis.gradients.core

import android.graphics.Color
import android.graphics.Color.BLACK
import android.graphics.Color.WHITE
import com.dtuskenis.gradients.core.Gradient.Component
import com.dtuskenis.gradients.extensions.scale
import java.lang.RuntimeException

class GradientComponents(val first: Color = Color.valueOf(WHITE),
                         val last: Color = Color.valueOf(BLACK),
                         val rest: List<Component> = emptyList()): Iterable<Component> {

    private val list = listOf(Component(first, 0.0f)) + rest + Component(last, 1.0f)

    override fun iterator(): Iterator<Component> = list.iterator()

    companion object {

        fun evenlyDistributed(colors: List<Color>): GradientComponents {
            if (colors.size < 2) {
                throw RuntimeException("gradient should have at least 2 colors")
            }

            val rest = colors.withIndex().dropFirstAndLast().map { Component(it.value, it.index.toFloat() / (colors.size - 1)) }

            return GradientComponents(first = colors.first(),
                                      last = colors.last(),
                                      rest = rest)
        }

        fun normalizedFrom(list: List<Component>): GradientComponents {
            if (list.size < 2) {
                throw RuntimeException("gradient should have at least 2 colors")
            }

            val normalizedList = list.normalize()

            return GradientComponents(first = normalizedList.first().color,
                                      last = normalizedList.last().color,
                                      rest = normalizedList.dropFirstAndLast())
        }

        private fun Iterable<Component>.normalize(): Iterable<Component> {
            val minPosition = minBy { it.relativePosition }!!.relativePosition
            val maxPosition = maxBy { it.relativePosition }!!.relativePosition

            return sortedBy { it.relativePosition }
                    .map { it.copy(relativePosition = it.relativePosition.scale(minPosition..maxPosition,
                                                                                0.0f..1.0f)) }
        }

        private fun <T> Iterable<T>.dropFirstAndLast() = drop(1).dropLast(1)
    }
}