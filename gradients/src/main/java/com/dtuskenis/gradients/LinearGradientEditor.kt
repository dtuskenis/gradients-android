package com.dtuskenis.gradients

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.max
import kotlin.math.min

class LinearGradientEditor: View, GradientEditor {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    private val editButtonBackgroundPaint = Paint()

    private val borderPaint = Paint().apply {
        strokeWidth = BORDER_THICKNESS
        color = Color.WHITE
        style = Paint.Style.STROKE
    }

    private val addButtonBitmap: Bitmap
    private val editButtonBitmap: Bitmap

    private var addButtonOffset: Float = 0.0f

    private var isAddButtonEnabled = true

    private var draggingAddButton = false
    private var touchedComponent: Gradient.Component? = null

    var onComponentsChanged: ((GradientComponents) -> Unit)? = null

    override var delegate: GradientEditor.Delegate? = null

    init {
        addButtonBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.ic_add_128)
        editButtonBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.ic_edit_128)
    }

    override var components = GradientComponents()
        set(value) {
            field = value

            updateAddButtonState()

            onComponentsChanged?.invoke(components)

            invalidate()
        }

    private fun updateAddButtonState() {
        val addButtonWasDisabled = !isAddButtonEnabled
        isAddButtonEnabled = components.toList().size < MAX_COMPONENTS
        if (addButtonWasDisabled && isAddButtonEnabled) {
            setAddButtonToCenter()
        }
    }

    private fun setAddButtonToCenter() {
        addButtonOffset = (measuredWidth - addButtonBitmap.width).toFloat() / 2
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        LayoutMeasurer.measure(widthMeasureSpec,
                               heightMeasureSpec,
                               desiredHeight = addButtonBitmap.height) { width, height ->
            setAddButtonToCenter()
            setMeasuredDimension(width, height)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val wholeRect = RectF(0.0f, 0.0f, width.toFloat(), height.toFloat())
        val gradientRect = RectF(wholeRect).apply { inset(addButtonBitmap.width.toFloat() / 2, 0.0f) }
        val borderRect = RectF(gradientRect).apply { inset(BORDER_THICKNESS / 2, BORDER_THICKNESS / 2) }

        with(canvas) {
            LinearGradientDrawer.drawOn(this, components, gradientRect)

            drawRect(borderRect, borderPaint)

            components.forEach {
                val offsetX = gradientRect.width() * it.relativePosition + gradientRect.left
                val centerY = wholeRect.centerY()
                val radius = gradientRect.height() / 2

                editButtonBackgroundPaint.color = it.color.rawValue

                drawCircle(offsetX, centerY, radius, editButtonBackgroundPaint)
                drawCircle(offsetX, centerY, radius - BORDER_THICKNESS / 2, borderPaint)
                drawBitmap(editButtonBitmap, offsetX - editButtonBitmap.width.toFloat() / 2, 0f, null)
            }

            if (isAddButtonEnabled) {
                drawBitmap(addButtonBitmap, addButtonOffset, 0f, null)
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {

        val touchOffsetX = event.x
        val addButtonWidth = addButtonBitmap.width.toFloat()

        fun updateAddButtonOffset() {
            val unconfinedOffset = touchOffsetX - addButtonWidth / 2
            addButtonOffset = max(min(unconfinedOffset, width.toFloat() - addButtonWidth), 0f)
            invalidate()
        }

        when(event.action) {
            MotionEvent.ACTION_DOWN -> {
                val foundTouchedComponent = findTouchedComponent(touchOffsetX)
                val isAddButtonTouched = touchOffsetX in addButtonOffset..(addButtonOffset + addButtonWidth)

                if (isAddButtonEnabled && (isAddButtonTouched || foundTouchedComponent == null)) {
                    draggingAddButton = true
                    updateAddButtonOffset()
                } else {
                    touchedComponent = foundTouchedComponent
                }
            }
            MotionEvent.ACTION_MOVE -> {
                if (draggingAddButton) {
                    updateAddButtonOffset()
                }
            }
            MotionEvent.ACTION_UP -> {
                if (draggingAddButton) {
                    draggingAddButton = false

                    addColorAt(relativePosition = addButtonOffset / (width - addButtonWidth))
                } else {
                    touchedComponent?.let { editColorOf(it) }
                    touchedComponent = null
                }
            }
        }

        return true
    }

    private fun findTouchedComponent(touchOffsetX: Float): Gradient.Component? {
        val editButtonWidth = editButtonBitmap.width.toFloat()

        return components.find {
            val centerOffsetX =
                it.relativePosition * (width.toFloat() - editButtonWidth) + editButtonWidth / 2

            touchOffsetX in (centerOffsetX - editButtonWidth / 2)..(centerOffsetX + editButtonWidth / 2)
        }
    }

    companion object {
        private const val BORDER_THICKNESS = 4f
        private const val MAX_COMPONENTS = 5
    }
}