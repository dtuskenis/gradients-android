package com.dtuskenis.gradients

import android.graphics.Color

interface GradientEditor: Gradient {

    var delegate: Delegate?

    interface Delegate {

        fun onAddColor(add: (Color) -> Unit)

        fun onEditColor(color: Color, change: (Color) -> Unit, remove: (() -> Unit)?)
    }
}

internal fun GradientEditor.addColorAt(relativePosition: Float) {
    delegate?.onAddColor { newColor ->
        mutateComponents { it.add(Gradient.Component(newColor, relativePosition)) }
    }
}

internal fun GradientEditor.editColorOf(component: Gradient.Component) {
    val changeComponentColor = { newColor: Color ->
        mutateComponents {
            val index = it.indexOf(component)
            val oldComponent = it.removeAt(index)
            it.add(index, oldComponent.copy(color = newColor))
        }
    }
    val removeComponent = {
        mutateComponents { it.remove(component) }
    }.takeIf { components.rest.isNotEmpty() }

    delegate?.onEditColor(component.color, changeComponentColor, removeComponent)
}

private fun GradientEditor.mutateComponents(block: (MutableList<Gradient.Component>) -> Unit) {
    components.toMutableList().also(block).let { components = GradientComponents.normalizedFrom(it) }
}
