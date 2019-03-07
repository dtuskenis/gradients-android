package com.dtuskenis.gradients

import android.graphics.Color.BLACK
import android.graphics.Color.WHITE
import com.dtuskenis.gradients.Gradient.Color
import com.dtuskenis.gradients.Gradient.Component
import java.lang.RuntimeException

class GradientComponents(val first: Component = Component(Color(WHITE), 0.0f),
                         val last: Component = Component(Color(BLACK), 1.0f),
                         val rest: List<Component> = emptyList()): Iterable<Component> {

    private val list = listOf(first) + rest + last

    override fun iterator(): Iterator<Component> = list.iterator()

    companion object {

        fun from(list: List<Component>): GradientComponents {
            if (list.size < 2) {
                throw RuntimeException("gradient should have at least 2 colors")
            }

            return GradientComponents(first = list.first(),
                                      last = list.last(),
                                      rest = list.drop(1).dropLast(1))
        }
    }
}