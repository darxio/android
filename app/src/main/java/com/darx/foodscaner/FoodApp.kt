package com.darx.foodscaner

import android.app.Application
import android.hardware.Camera
import com.darx.foodscaner.camerafragment.camera.CameraSource


class FoodApp : Application() {

    var cameraSource: CameraSource? = null
    var camera: Camera? = null

    override fun onCreate() {
        super.onCreate()
        camera = cameraSource?.createCamera()
    }
}
