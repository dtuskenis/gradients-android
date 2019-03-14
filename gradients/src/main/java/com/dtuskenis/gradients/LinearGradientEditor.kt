package com.dtuskenis.gradients

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.dtuskenis.gradients.extensions.getMeasuredDimensions
import com.dtuskenis.gradients.extensions.setContentDimensionsTo
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

    private val gradientRect = RectF()
    private val borderRect = RectF()

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
        addButtonOffset = gradientRect.width() / 2 + gradientRect.left
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        getMeasuredDimensions(widthMeasureSpec,
                              heightMeasureSpec,
                              desiredHeight = { addButtonBitmap.height },
                              onComplete = { setMeasuredDimension(it.width, it.height) })
    }

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)

        setContentDimensionsTo(gradientRect)

        gradientRect.inset(editButtonBitmap.width.toFloat() / 2, 0.0f)

        borderRect.set(gradientRect)
        borderRect.inset(BORDER_THICKNESS / 2, BORDER_THICKNESS / 2)

        setAddButtonToCenter()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        with(canvas) {
            GradientDrawers.linear.drawOn(this, components, gradientRect)

            drawRect(borderRect, borderPaint)

            val centerY = gradientRect.centerY()

            components.forEach {
                val offsetX = gradientRect.width() * it.relativePosition + gradientRect.left
                val radius = gradientRect.height() / 2

                editButtonBackgroundPaint.color = it.color.toArgb()

                drawCircle(offsetX, centerY, radius, editButtonBackgroundPaint)
                drawCircle(offsetX, centerY, radius - BORDER_THICKNESS / 2, borderPaint)
                drawBitmap(editButtonBitmap, offsetX - editButtonBitmap.width / 2, centerY - editButtonBitmap.height / 2, null)
            }

            if (isAddButtonEnabled) {
                drawBitmap(addButtonBitmap, addButtonOffset - addButtonBitmap.width / 2, centerY - addButtonBitmap.height / 2, null)
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {

        val touchOffsetX = event.x
        val addButtonWidth = addButtonBitmap.width.toFloat()

        fun updateAddButtonOffset() {
            addButtonOffset = max(min(touchOffsetX, gradientRect.right), gradientRect.left)
            invalidate()
        }

        when(event.action) {
            MotionEvent.ACTION_DOWN -> {
                val foundTouchedComponent = findTouchedComponent(touchOffsetX)
                val isAddButtonTouched = touchOffsetX in (addButtonOffset - addButtonWidth / 2)..(addButtonOffset + addButtonWidth / 2)

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

                    addColorAt(relativePosition = (addButtonOffset - gradientRect.left) / gradientRect.width())
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
            val centerOffsetX = it.relativePosition * gradientRect.width() + gradientRect.left

            touchOffsetX in (centerOffsetX - editButtonWidth / 2)..(centerOffsetX + editButtonWidth / 2)
        }
    }

    companion object {
        private const val BORDER_THICKNESS = 4f
        private const val MAX_COMPONENTS = 5
    }
}