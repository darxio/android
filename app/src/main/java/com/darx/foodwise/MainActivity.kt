package com.darx.foodwise

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.darx.foodwise.fragments.*
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.facebook.drawee.backends.pipeline.Fresco
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {

    private var CAMERA_REQUEST = 1;

    private val profileFragment = ProfileFragment()
    var cameraFragment: CameraFragment? = null
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_REQUEST) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                cameraFragment = CameraFragment()
                supportFragmentManager.beginTransaction().replace(R.id.fragments_frame, cameraFragment!!).commit()
            } else {
                supportFragmentManager.beginTransaction().replace(R.id.fragments_frame, NoPermissonFragment()).commit()
                Toast.makeText(this, R.string.ask_fpermission_on_deny, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val loader = Intent(this@MainActivity, LoaderActivity::class.java)
        startActivity(loader)
        Fresco.initialize(this)

        setContentView(R.layout.activity_main)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_REQUEST
            )
        } else {
            cameraFragment = CameraFragment()
        }

        bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
//        bottomNavigationView?.visibility= View.GONE
        bottomNavigationView?.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.action_profile -> {
                    supportFragmentManager.beginTransaction().replace(R.id.fragments_frame, profileFragment).commit()
                }
                R.id.action_camera -> {
                    if (cameraFragment != null) {
                        supportFragmentManager.beginTransaction().replace(R.id.fragments_frame, cameraFragment!!).commit()
                    } else {
                        supportFragmentManager.beginTransaction().replace(R.id.fragments_frame, NoPermissonFragment()).commit()
                    }
                }
                R.id.action_recently_scanned -> {
                    supportFragmentManager.beginTransaction().replace(R.id.fragments_frame, recentlyScannedFragment).commit()

                }
            }
            true
        }

        if (cameraFragment != null) {
            supportFragmentManager.beginTransaction().replace(R.id.fragments_frame, cameraFragment!!).commit()
        } else {
            supportFragmentManager.beginTransaction().replace(R.id.fragments_frame, NoPermissonFragment()).commit()
        }
        bottomNavigationView?.selectedItemId = R.id.action_camera

        val prefs = getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val firstLaunch = prefs.getBoolean("firstLaunch", true)

        if (firstLaunch) {
            val intent = Intent(this@MainActivity, WelcomeWizardActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            startActivity(intent)
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
                supportFragmentManager.beginTransaction().replace(R.id.fragments_frame, cameraFragment!!).commit()
                bottomNavigationView?.selectedItemId = R.id.action_camera
            }
            2 -> {
                supportFragmentManager.beginTransaction().replace(R.id.fragments_frame, recentlyScannedFragment).commit()
                bottomNavigationView?.selectedItemId = R.id.action_recently_scanned
            }
        }
    }
}
