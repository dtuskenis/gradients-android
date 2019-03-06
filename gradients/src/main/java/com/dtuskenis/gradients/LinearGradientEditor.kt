package com.dtuskenis.gradients

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.max
import kotlin.math.min

class LinearGradientEditor: View, Gradient {
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
    private var touchingAddButton = false
    private var touchingEditButtonIndex: Int? = null

    var delegate: Delegate? = null
    var target: Gradient? = null

    init {
        addButtonBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.ic_add_128)
        editButtonBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.ic_edit_128)
    }

    override var components = GradientComponents()
        set(value) {
            setComponents2(value) { field = value }
        }

    private fun setComponents2(newComponents: GradientComponents, upd: (GradientComponents) -> Unit) {
        val min = newComponents.minBy { it.relativePosition }!!.relativePosition
        val max = newComponents.maxBy { it.relativePosition }!!.relativePosition
        val components = newComponents
            .take(MAX_COMPONENTS)
            .sortedBy { it.relativePosition }
            .map { Gradient.Component(it.color,
                                      scaleFloat(it.relativePosition,
                                                 min..max,
                                                 0f..1f)) }

        upd(components.wrap())

        val addButtonWasDisabled = !isAddButtonEnabled
        isAddButtonEnabled = components.size < MAX_COMPONENTS
        if (addButtonWasDisabled && isAddButtonEnabled) {
            addButtonOffset = width.toFloat() / 2 - addButtonBitmap.width.toFloat() / 2
        }

        target?.components = components.wrap()

        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)

        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        val width = when (widthMode) {
            MeasureSpec.EXACTLY,
            MeasureSpec.AT_MOST -> widthSize
            else -> 0
        }

        val desiredHeight = addButtonBitmap.height

        val height = when(heightMode) {
            MeasureSpec.EXACTLY -> heightSize
            MeasureSpec.AT_MOST -> min(desiredHeight, heightSize)
            else -> desiredHeight
        }

        addButtonOffset = width.toFloat() / 2 - addButtonBitmap.width.toFloat() / 2

        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val zeroX = 0f + addButtonBitmap.width.toFloat() / 2
        val maxxX = canvas.width - addButtonBitmap.width.toFloat() / 2

        with(canvas) {
            LinearGradientDrawer.drawOn(this, components, zeroX)

            val width = width.toFloat()
            val height = height.toFloat()

            drawRect(RectF(zeroX + BORDER_THICKNESS / 2,
                           0f + BORDER_THICKNESS / 2,
                           maxxX - BORDER_THICKNESS / 2,
                           height - BORDER_THICKNESS / 2),
                     borderPaint)

            val centerX = width / 2
            val centerY = height / 2

            components.forEach {
                val posRelative = it.relativePosition
                val posAbsolute = (maxxX - zeroX) * posRelative + zeroX

                editButtonBackgroundPaint.color = it.color.rawValue
                drawCircle(posAbsolute, centerY, height / 2, editButtonBackgroundPaint)

                drawCircle(posAbsolute, centerY, height / 2 - BORDER_THICKNESS / 2, borderPaint)

                drawBitmap(editButtonBitmap, posAbsolute - editButtonBitmap.width.toFloat() / 2, 0f, null)
            }

            if (isAddButtonEnabled) {
                drawBitmap(addButtonBitmap, addButtonOffset, 0f, null)
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {

        val addButtonWidth = addButtonBitmap.width.toFloat()

        val width = width.toFloat()

        val updateOffset = {
            val offset = event.x - addButtonWidth / 2
            addButtonOffset = max(min(offset, width - addButtonWidth), 0f)
        }
        val update = {
            updateOffset()
            invalidate()
        }

        if (event.action == MotionEvent.ACTION_DOWN) {
            if (isAddButtonEnabled && event.x in addButtonOffset..(addButtonOffset + addButtonWidth) /* TODO: or edit buttons */) {
                touchingAddButton = true

                update()
            } else {
                val offset = event.x

                touchingEditButtonIndex =
                components
                    .find {
                        val absPos = it.relativePosition * (width - addButtonWidth) + addButtonWidth / 2
                        offset in (absPos - addButtonWidth / 2)..(absPos + addButtonWidth / 2)
                    }
                    ?.let { components.indexOf(it) }

                if (touchingEditButtonIndex == null) {
                    touchingAddButton = true

                    update()
                }
            }
        }

        if (event.action == MotionEvent.ACTION_MOVE) {
            if (touchingAddButton) {
                update()
            } else {
                // recalculate? edit button index
            }
        }

        if (event.action == MotionEvent.ACTION_UP) {
            if (touchingAddButton) {
                touchingAddButton = false

                updateOffset()

                val colorPos = (addButtonOffset) / (width - addButtonWidth)

                addColorAt(colorPos)
            } else {
                touchingEditButtonIndex?.let { editColorAt(it) }
                touchingEditButtonIndex = null
            }
        }

        return true
    }

    private fun addColorAt(position: Float) {
        delegate?.addColor { newColor ->
            components.toMutableList()
                .also { it.add(Gradient.Component(newColor, position)) }
                .let { components = it.wrap() }
        }
    }

    private fun editColorAt(index: Int) {
        components.toList().getOrNull(index)?.let {
            val remove = {
                components.toMutableList()
                    .also { it.removeAt(index) }
                    .let { components = it.wrap() }
            }.takeIf { components.rest.isNotEmpty() }

            delegate?.editColor(it.color, remove)
        }
    }

    interface Delegate {

        fun addColor(onComplete: (Gradient.Color) -> Unit)

        fun editColor(color: Gradient.Color, onRemove: (() -> Unit)?)
    }

    companion object {
        private const val BORDER_THICKNESS = 4f
        private const val MAX_COMPONENTS = 5
    }
}