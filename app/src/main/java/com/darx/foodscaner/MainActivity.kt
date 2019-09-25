package com.darx.foodscaner

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.darx.foodscaner.components.PageAdapter
import com.darx.foodscaner.fragments.*
import kotlinx.android.synthetic.main.activity_main.*

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
}
