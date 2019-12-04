package com.darx.foodwise

import android.app.Application
import android.hardware.Camera
import com.darx.foodwise.camerafragment.camera.CameraSource


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

