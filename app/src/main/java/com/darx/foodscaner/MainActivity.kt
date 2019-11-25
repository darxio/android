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
    private val pagerAdapter = PageAdapter(supportFragmentManager, lifecycle)

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

//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        pagerAdapter.addFragment(ProfileFragment(), "Profile")
        pagerAdapter.addFragment(CameraFragment(), "Camera")
        pagerAdapter.addFragment(RecentlyScannedFragment(), "RecentlyScanned")

        setContentView(com.darx.foodscaner.R.layout.activity_main)

        val bottomNavigationView =
            findViewById<BottomNavigationView>(com.darx.foodscaner.R.id.bottom_navigation)

        bottomNavigationView.setOnNavigationItemSelectedListener(object :
            BottomNavigationView.OnNavigationItemSelectedListener {
            override fun onNavigationItemSelected(item: MenuItem): Boolean {
                when (item.getItemId()) {
                    com.darx.foodscaner.R.id.action_profile ->
                        fragments_view_pager.currentItem = pagerAdapter.getItemNum("Profile")
                    com.darx.foodscaner.R.id.action_camera ->
                        fragments_view_pager.currentItem = pagerAdapter.getItemNum("Camera")
                    com.darx.foodscaner.R.id.action_recently_scanned ->
                        fragments_view_pager.currentItem = pagerAdapter.getItemNum("RecentlyScanned")
                }
                return true
            }
        })

        fragments_view_pager.setUserInputEnabled(false);
        fragments_view_pager.adapter = pagerAdapter
        fragments_view_pager.currentItem = pagerAdapter.getItemNum("Camera")

        val prefs = getSharedPreferences("prefs", Context.MODE_PRIVATE)
        var firstLaunch = prefs.getBoolean("firstLaunch", true)

        if (firstLaunch) {
            val intent = Intent(this@MainActivity, WelcomeWizardActivity::class.java)
            startActivity(intent)
        }
    }

}
