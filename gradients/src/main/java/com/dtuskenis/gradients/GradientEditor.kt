package com.dtuskenis.gradients

interface GradientEditor: Gradient {

    var delegate: Delegate?

    interface Delegate {

        fun addColor(onComplete: (Gradient.Color) -> Unit)

        fun editColor(color: Gradient.Color, onRemove: (() -> Unit)?)
    }
}

internal fun GradientEditor.addColorAt(relativePosition: Float) {
    delegate?.addColor { newColor ->
        mutateComponents { it.add(Gradient.Component(newColor, relativePosition)) }
    }
}

internal fun GradientEditor.editColorAt(index: Int) {
    components.toList().getOrNull(index)?.let {
        val removeComponent = {
            mutateComponents { it.removeAt(index) }
        }.takeIf { components.rest.isNotEmpty() }

        delegate?.editColor(it.color, removeComponent)
    }
}

private fun GradientEditor.mutateComponents(block: (MutableList<Gradient.Component>) -> Unit) {
    components.toMutableList().also(block).let { components = GradientComponents.normalizedFrom(it) }
}
