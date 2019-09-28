package com.darx.foodscaner

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.darx.foodscaner.components.PageAdapter
import com.darx.foodscaner.fragments.*
//import com.darx.foodscaner.services.ApiService
import com.darx.foodscaner.services.responce.ApiService
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_camera.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.Dispatcher
import okhttp3.Request

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val pagerAdapter = PageAdapter(supportFragmentManager, lifecycle)
        pagerAdapter.addFragment(ProfileFragment(), "Profile")
        pagerAdapter.addFragment(CameraFragment(), "Camera")
        pagerAdapter.addFragment(InfoFragment(), "Info")
        viewPager.adapter = pagerAdapter
    }

    fun req(view:View) {
        val apiService = ApiService.create()
        GlobalScope.launch(Dispatchers.Main) {
            val currentWether = apiService.search("82e4f09b1d58065e0a32fb06341135c2", "London").await()
            camera.text = currentWether.toString()
        }
    }

}
