package com.darx.foodscaner

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.darx.foodscaner.adapters.PageAdapter
import com.darx.foodscaner.fragments.*
import kotlinx.android.synthetic.main.activity_main.*
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_live_barcode_kotlin.*


class MainActivity : AppCompatActivity() {

    private var CAMERA_REQUEST = 1;

    private val profileFragment = ProfileFragment()
    private var cameraFragment: CameraFragment? = null
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
                Toast.makeText(this, "We really need this permission", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED) {
//            Toast.makeText(
//                this, "Дайте доступ к камере!",
//                Toast.LENGTH_SHORT
//            ).show()
            requestPermissions(
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_REQUEST
            )
//            val n = no_permission_stub
//            n.visibility = View.VISIBLE
        } else {
            cameraFragment = CameraFragment()
        }

        bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
//        bottomNavigationView?.visibility= View.GONE
        bottomNavigationView?.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.action_profile ->
                    supportFragmentManager.beginTransaction().replace(R.id.fragments_frame, profileFragment).commit()//pagerAdapter.getItemNum("Profile")
                R.id.action_camera -> {
                    if (cameraFragment != null) {
                        supportFragmentManager.beginTransaction().replace(R.id.fragments_frame, cameraFragment!!).commit()
                    } else {
                        no_permission_stub.visibility = View.VISIBLE
                    }
                }
                R.id.action_recently_scanned ->
                    supportFragmentManager.beginTransaction().replace(R.id.fragments_frame, recentlyScannedFragment).commit()
            }
            true
        }

        if (cameraFragment != null) {
            supportFragmentManager.beginTransaction().replace(R.id.fragments_frame, cameraFragment!!).commit()
        } else {
            val emptyFragment = EmptyFragment(
                R.drawable.ic_star_black,
                "У Вас пока нет любимых продуктов!",
                "Добавить",
                View.OnClickListener {
                    
                }
            )
        }
        bottomNavigationView?.selectedItemId = R.id.action_camera

        val prefs = getSharedPreferences("prefs", Context.MODE_PRIVATE)
        val firstLaunch = prefs.getBoolean("firstLaunch", true)

        if (firstLaunch) {
            val intent = Intent(this@MainActivity, WelcomeWizardActivity::class.java)
            startActivity(intent)
        }
    }
}
