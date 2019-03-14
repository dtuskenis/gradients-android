package com.dtuskenis.gradients.sample

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.dtuskenis.gradients.Gradient
import com.dtuskenis.gradients.GradientComponents
import android.support.v7.app.AlertDialog
import com.dtuskenis.gradients.GradientEditor
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.view_color_picker.view.*

class AppActivity : AppCompatActivity() {

    private val colorsPool = listOf(
        Color.CYAN,
        Color.YELLOW,
        Color.MAGENTA,
        Color.BLUE,
        Color.RED,
        Color.GREEN,
        Color.GRAY
    ).map { Color.valueOf(it) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val gradientComponents =
            listOf(Color.RED to 0.0f,
                   Color.BLUE to 1.0f)
                .map { Gradient.Component(Color.valueOf(it.first), it.second) }
                .let { GradientComponents.normalizedFrom(it) }

        gradientEditorView.onComponentsChanged = { gradientView.components = it }
        gradientEditorView.components = gradientComponents
        gradientEditorView.delegate = object : GradientEditor.Delegate {
            override fun onAddColor(add: (Color) -> Unit) {
                showEditingDialog(colorsPool.random(), add)
            }

            override fun onEditColor(color: Color, change: (Color) -> Unit, remove: (() -> Unit)?) {
                showEditingDialog(color, change, remove)
            }
        }
    }

    private fun showEditingDialog(color: Color,
                                  changeColor: (Color) -> Unit,
                                  removeColor: (() -> Unit)? = null) {
        layoutInflater.inflate(R.layout.view_color_picker, null).apply {
            sampleView.setBackgroundColor(color.toArgb())
            hsvPickerView.selectedColor = color
            hsvPickerView.onColorChanged = { sampleView.setBackgroundColor(it.toArgb()) }

            AlertDialog.Builder(context)
                    .setTitle(R.string.editing_dialog_title)
                    .setMessage(R.string.editing_dialog_message)
                    .setView(this)
                    .setCancelable(true)
                    .setPositiveButton(R.string.editing_dialog_action_save) { _, _ ->  changeColor(hsvPickerView.selectedColor) }
                    .apply { removeColor?.let { setNegativeButton(R.string.editing_dialog_action_remove) { _, _ -> it() } } }
                    .show()
        }
    }
}
