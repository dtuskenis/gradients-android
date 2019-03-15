package com.dtuskenis.gradients

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.support.annotation.DrawableRes
import android.support.annotation.StyleableRes
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.dtuskenis.gradients.extensions.*
import com.dtuskenis.gradients.utils.AttributesResolver
import kotlin.math.*

class HSVColorPickerView(context: Context, attributeSet: AttributeSet?): View(context, attributeSet) {

    private val layoutRect = RectF()
    private val hueSaturationCircleRect = RectF()
    private val inputValueBarRect = RectF()

    private var hueSaturationCircleClipPath = Path()

    private val hueSaturationMarker: Drawable
    private val inputLevelMarker: Drawable
    private val inputLevelBar: Drawable

    private val internalSpacing = context.resources.getDimension(R.dimen.hsv_color_picker_default_internal_spacing)

    private val hueSaturationMarkerLocation = PointF()
    private var inputValueMarkerPosition = 0.0f

    private var touchMode: TouchMode? = null

    private var currentHSV = HSV.from(Color())

    var onColorChanged: ((Color) -> Unit)? = null

    var selectedColor: Color
        get() = currentHSV.toColor()
        set(newColor) {
            currentHSV = HSV.from(newColor)

            updateMarkerLocations()

            invalidate()

            notifyColorChanged()
        }

    init {
        val attributesResolver = AttributesResolver(context, attributeSet, R.styleable.HSVColorPickerView)

        fun getDrawable(@StyleableRes index: Int, @DrawableRes fallbackId: Int) =
                attributesResolver.getDrawable(index, fallbackId).apply { setBoundsCenteredToIntrinsic() }

        hueSaturationMarker = getDrawable(index = R.styleable.HSVColorPickerView_hueSaturationMarker,
                                          fallbackId = R.drawable.hsv_color_picker_default_hue_saturation_marker)

        inputLevelMarker = getDrawable(index = R.styleable.HSVColorPickerView_inputLevelMarker,
                                       fallbackId = R.drawable.hsv_color_picker_default_input_level_marker)

        inputLevelBar = getDrawable(index = R.styleable.HSVColorPickerView_inputLevelBar,
                                    fallbackId = R.drawable.hsv_color_picker_default_input_level_bar)

        attributesResolver.cleanUp()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.withinSaveRestore {
            clipPath(hueSaturationCircleClipPath)

            GradientDrawers.sweep.drawOn(this,
                                         HUE_GRADIENT_COMPONENTS,
                                         hueSaturationCircleRect)
        }

        GradientDrawers.radial.drawOn(canvas,
                                      SATURATION_GRADIENT_COMPONENTS,
                                      hueSaturationCircleRect,
                                      porterDuffMode = PorterDuff.Mode.SRC_OVER)

        inputLevelBar.draw(canvas)

        hueSaturationMarker.draw(canvas, center = hueSaturationMarkerLocation)

        inputLevelMarker.draw(canvas, center = PointF(inputValueBarRect.left + inputValueBarRect.width() * inputValueMarkerPosition,
                                                      inputValueBarRect.centerY()))
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        getMeasuredDimensions(widthMeasureSpec,
                              heightMeasureSpec,
                              desiredHeight = { it.width + internalSpacing.roundToInt() + inputLevelBar.intrinsicHeight },
                              onComplete = { setMeasuredDimension(it.width, it.height) })
    }

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)

        setContentDimensionsTo(layoutRect)

        hueSaturationCircleRect.set(layoutRect.left,
                                    layoutRect.top,
                                    layoutRect.right,
                                    layoutRect.bottom - internalSpacing - inputLevelBar.bounds.height())

        hueSaturationCircleClipPath.reset()
        hueSaturationCircleClipPath.addCircle(hueSaturationCircleRect.centerX(),
                                              hueSaturationCircleRect.centerY(),
                                              hueSaturationCircleRect.innerCircleRadius(),
                                              Path.Direction.CW)

        inputValueBarRect.set(hueSaturationCircleRect.left,
                              hueSaturationCircleRect.bottom + internalSpacing,
                              hueSaturationCircleRect.right,
                              layoutRect.bottom)

        inputLevelBar.bounds = inputValueBarRect.round()

        updateMarkerLocations()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {

        val touchPoint = PointF(event.x, event.y)

        fun updateAccordingToTouchMode() = touchMode?.let { when (it) {
            TouchMode.HUE_SATURATION -> updateHueAndSaturation(touchPoint)
            TouchMode.INPUT_LEVEL ->  updateInputValue(touchPoint)
        } }

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                touchMode = when {
                    hueSaturationCircleRect.contains(touchPoint) -> TouchMode.HUE_SATURATION
                    inputValueBarRect.contains(touchPoint) -> TouchMode.INPUT_LEVEL
                    else -> null
                }
                updateAccordingToTouchMode()
            }
            MotionEvent.ACTION_MOVE -> {
                updateAccordingToTouchMode()
            }
            MotionEvent.ACTION_UP -> {
                touchMode = null
            }
        }

        return true
    }

    private fun updateHueAndSaturation(newLocation: PointF) {
        val canvasRadius = hueSaturationCircleRect.innerCircleRadius()

        val distance = newLocation.distanceTo(hueSaturationCircleRect.center())

        val tx = newLocation.x - hueSaturationCircleRect.centerX()
        val ty = newLocation.y - hueSaturationCircleRect.centerY()

        var angle = atan2(ty / distance, tx / distance)
        if (angle < 0) {
            angle += (2 * PI).toFloat()
        }

        val x = cos(angle) * min(distance, canvasRadius) + hueSaturationCircleRect.centerX()
        val y = sin(angle) * min(distance, canvasRadius) + hueSaturationCircleRect.centerY()

        hueSaturationMarkerLocation.set(x, y)

        currentHSV = currentHSV.copy(hue = angle.toDegrees(),
                                     saturation = distance / canvasRadius)

        invalidate()

        notifyColorChanged()
    }

    private fun updateInputValue(newLocation: PointF) {
        val confinedTouchX = max(min(newLocation.x, inputValueBarRect.right), inputValueBarRect.left)

        inputValueMarkerPosition = (confinedTouchX - inputValueBarRect.left) / inputValueBarRect.width()

        currentHSV = currentHSV.copy(value = inputValueMarkerPosition)

        invalidate()

        notifyColorChanged()
    }

    private fun updateMarkerLocations() {
        val angle = currentHSV.hue.toRadians()
        val distance = currentHSV.saturation * hueSaturationCircleRect.innerCircleRadius()

        val x = cos(angle) * distance + hueSaturationCircleRect.centerX()
        val y = sin(angle) * distance + hueSaturationCircleRect.centerY()

        hueSaturationMarkerLocation.set(x, y)

        inputValueMarkerPosition = currentHSV.value
    }

    private fun notifyColorChanged() {
        onColorChanged?.invoke(selectedColor)
    }

    companion object {

        private fun colorsOf(vararg rawColors: Int) = rawColors.toList().map { Color.valueOf(it) }

        private val HUE_GRADIENT_COMPONENTS = GradientComponents.evenlyDistributed(colorsOf(Color.RED,
                                                                                            Color.YELLOW,
                                                                                            Color.GREEN,
                                                                                            Color.CYAN,
                                                                                            Color.BLUE,
                                                                                            Color.MAGENTA,
                                                                                            Color.RED))

        private val SATURATION_GRADIENT_COMPONENTS = GradientComponents.evenlyDistributed(colorsOf(Color.WHITE,
                                                                                                   Color.TRANSPARENT))
    }

    private enum class TouchMode {
        HUE_SATURATION,
        INPUT_LEVEL,
    }

    private data class HSV(val hue: Float,
                           val saturation: Float,
                           val value: Float) {

        fun toColor(): Color = Color.valueOf(Color.HSVToColor(floatArrayOf(hue, saturation, value)))

        companion object {
            fun from(color: Color): HSV = FloatArray(3)
                                            .apply { Color.colorToHSV(color.toArgb(), this) }
                                            .let { HSV(it[0], it[1], it[2]) }
        }
    }
}