package com.darx.foodscaner

import android.app.Application
import android.hardware.Camera
import com.darx.foodscaner.camerafragment.camera.CameraSource
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T




class FoodApp: Application() {

    var camera: Camera? = null
    var cameraSource: CameraSource? = null

    private object HOLDER {
        val INSTANCE = FoodApp()
    }

    companion object {
        val instance: FoodApp by lazy { HOLDER.INSTANCE }
    }

}

