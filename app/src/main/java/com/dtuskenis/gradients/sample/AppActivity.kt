package com.dtuskenis.gradients.sample

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.dtuskenis.gradients.LinearGradientView
import kotlinx.android.synthetic.main.activity_main.*

class AppActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val gradientColorComponents =
            listOf(Color.RED to 0.0f,
                   Color.GREEN to 0.5f,
                   Color.BLUE to 1.0f)
                .map { LinearGradientView.ColorComponent(it.first, it.second) }

        gradientView.setColorComponents(gradientColorComponents)
    }
}
