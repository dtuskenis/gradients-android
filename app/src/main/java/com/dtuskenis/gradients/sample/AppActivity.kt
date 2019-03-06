package com.dtuskenis.gradients.sample

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import com.dtuskenis.gradients.LinearGradientEditor
import com.dtuskenis.gradients.LinearGradient
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
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val gradientColorComponents =
            listOf(Color.RED to 0.0f,
                   Color.BLUE to 1.0f)
                .map { LinearGradient.Component(it.first, it.second) }

        gradientEditorView.target = gradientView
        gradientEditorView.setComponents(gradientColorComponents)
        gradientEditorView.delegate = object : LinearGradientEditor.Delegate {
            override fun addColor(onComplete: (Int) -> Unit) {
                colorsPool.random().let(onComplete)
            }

            override fun editColor(color: Int, onRemove: (() -> Unit)?) {
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
