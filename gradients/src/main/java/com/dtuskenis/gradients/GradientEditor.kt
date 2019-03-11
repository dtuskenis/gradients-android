package com.dtuskenis.gradients

import android.graphics.Color

interface GradientEditor: Gradient {

    var delegate: Delegate?

    interface Delegate {

        fun addColor(onComplete: (Color) -> Unit)

        fun editColor(color: Color, onRemove: (() -> Unit)?)
    }
}

internal fun GradientEditor.addColorAt(relativePosition: Float) {
    delegate?.addColor { newColor ->
        mutateComponents { it.add(Gradient.Component(newColor, relativePosition)) }
    }
}

internal fun GradientEditor.editColorOf(component: Gradient.Component) {
    val removeComponent = {
        mutateComponents { it.remove(component) }
    }.takeIf { components.rest.isNotEmpty() }

    delegate?.editColor(component.color, removeComponent)
}

private fun GradientEditor.mutateComponents(block: (MutableList<Gradient.Component>) -> Unit) {
    components.toMutableList().also(block).let { components = GradientComponents.normalizedFrom(it) }
}
