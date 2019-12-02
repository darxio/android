package com.darx.foodscaner

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.darx.foodscaner.adapters.PageAdapter
import com.darx.foodscaner.fragments.*
import kotlinx.android.synthetic.main.activity_main.*
import android.view.MenuItem
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {

    private val profileFragment = ProfileFragment()
    private val cameraFragment = CameraFragment()
    private val recentlyScannedFragment = RecentlyScannedFragment()

    private var bottomNavigationView: BottomNavigationView? = null

    override fun onBackPressed() {
        val count = supportFragmentManager.backStackEntryCount

        if (count == 0) {
            super.onBackPressed()
        } else {
            supportFragmentManager.popBackStack()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView?.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.action_profile ->
                    supportFragmentManager.beginTransaction().replace(R.id.fragments_frame, profileFragment).commit()//pagerAdapter.getItemNum("Profile")
                R.id.action_camera ->
                    supportFragmentManager.beginTransaction().replace(R.id.fragments_frame, cameraFragment).commit()
                R.id.action_recently_scanned ->
                    supportFragmentManager.beginTransaction().replace(R.id.fragments_frame, recentlyScannedFragment).commit()
            }
            true
        }

        supportFragmentManager.beginTransaction().replace(R.id.fragments_frame, cameraFragment).commit()
        bottomNavigationView?.selectedItemId = R.id.action_camera

        val prefs = getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val firstLaunch = prefs.getBoolean("firstLaunch", true)

        if (firstLaunch) {
            val intent = Intent(this@MainActivity, WelcomeWizardActivity::class.java)
            startActivity(intent)
        }
    }

    fun chooseFragment(id: Int) {
        if (id < 0 || id > 2) {
            return
        }
        when (id) {
            0 -> {
                supportFragmentManager.beginTransaction().replace(R.id.fragments_frame, profileFragment).commit()
                bottomNavigationView?.selectedItemId = R.id.action_profile
            }
            1 -> {
                supportFragmentManager.beginTransaction().replace(R.id.fragments_frame, cameraFragment).commit()
                bottomNavigationView?.selectedItemId = R.id.action_camera
            }
            2 -> {
                supportFragmentManager.beginTransaction().replace(R.id.fragments_frame, recentlyScannedFragment).commit()
                bottomNavigationView?.selectedItemId = R.id.action_recently_scanned
            }
        }
    }

}
