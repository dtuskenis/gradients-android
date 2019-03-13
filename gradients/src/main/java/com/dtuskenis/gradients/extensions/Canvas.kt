package com.dtuskenis.gradients.extensions

import android.graphics.Canvas

internal fun Canvas.withinSaveRestore(block: Canvas.() -> Unit) {
    save()

    block(this)

    restore()
}