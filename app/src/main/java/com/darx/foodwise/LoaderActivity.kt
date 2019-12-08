package com.darx.foodwise

import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_loader.*
import android.os.CountDownTimer
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.transition.Explode
import android.view.Window


class LoaderActivity : AppCompatActivity() {
//    private var loader: AnimatedVectorDrawable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        setContentView(R.layout.activity_loader)

        object : CountDownTimer(1500, 1000) {

            override fun onTick(millisUntilFinished: Long) {

            }


            override fun onFinish() {
                this@LoaderActivity.finish()
            }

        }.start()
    }
}