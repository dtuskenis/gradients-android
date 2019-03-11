package com.dtuskenis.gradients.sample

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.dtuskenis.gradients.Gradient
import com.dtuskenis.gradients.GradientComponents
import android.support.v7.app.AlertDialog
import com.dtuskenis.gradients.GradientEditor
import kotlinx.android.synthetic.main.activity_main.*

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
            override fun addColor(onComplete: (Color) -> Unit) {
                colorsPool.random()
                    .let(onComplete)
            }

            override fun editColor(color: Color, onRemove: (() -> Unit)?) {
                onRemove?.let { remove -> confirmRemove { remove() } }
            }
        }
    }

    private fun confirmRemove(onConfirmed: () -> Unit) {
        AlertDialog.Builder(this)
            .setTitle(R.string.are_you_sure)
            .setMessage(R.string.this_action_cannot_be_undone)
            .setCancelable(true)
            .setPositiveButton(R.string.yes) { _, _ -> onConfirmed() }
            .setNegativeButton(R.string.no) { dialog, _ -> dialog.cancel() }
            .show()
    }
}
