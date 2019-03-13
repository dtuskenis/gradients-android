package com.dtuskenis.gradients

import android.graphics.Color

interface GradientEditor: Gradient {

    var delegate: Delegate?

    interface Delegate {

        fun addColor(onComplete: (Color) -> Unit)

        fun editColor(color: Color, onChange: (Color) -> Unit, onRemove: (() -> Unit)?)
    }
}

internal fun GradientEditor.addColorAt(relativePosition: Float) {
    delegate?.addColor { newColor ->
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

    delegate?.editColor(component.color, changeComponentColor, removeComponent)
}

private fun GradientEditor.mutateComponents(block: (MutableList<Gradient.Component>) -> Unit) {
    components.toMutableList().also(block).let { components = GradientComponents.normalizedFrom(it) }
}
