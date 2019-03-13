package com.dtuskenis.gradients.extensions

import android.graphics.Canvas
import android.graphics.PointF
import android.graphics.Rect
import android.graphics.drawable.Drawable

internal fun Drawable.setBoundsCenteredToIntrinsic() {
    bounds = Rect(-intrinsicWidth / 2,
                  -intrinsicHeight / 2,
                  intrinsicWidth / 2,
                  intrinsicHeight / 2)
}

internal fun Drawable.draw(canvas: Canvas, center: PointF) {
    canvas.withinSaveRestore {
        canvas.translate(center.x, center.y)

        draw(canvas)
    }
}
