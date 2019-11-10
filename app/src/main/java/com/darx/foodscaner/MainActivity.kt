package com.darx.foodscaner

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.darx.foodscaner.adapters.PageAdapter
import com.darx.foodscaner.adapters.WizardAdapter
import com.darx.foodscaner.fragments.*
import kotlinx.android.synthetic.main.activity_main.*
import android.widget.Toast
import android.R
import android.view.MenuItem
import androidx.lifecycle.ViewModelProviders
import com.darx.foodscaner.database.ProfileModel
import com.darx.foodscaner.database.ProfileViewModel
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

        val profileViewModel = ViewModelProviders.of(this).get(ProfileViewModel::class.java)
        val isProfile = profileViewModel.getCount_()

        bottomNavigationView.setOnNavigationItemSelectedListener(object :
            BottomNavigationView.OnNavigationItemSelectedListener {
            override fun onNavigationItemSelected(item: MenuItem): Boolean {
                when (item.getItemId()) {
                    com.darx.foodscaner.R.id.action_profile ->
                        viewPager.currentItem = pagerAdapter.getItemNum("Profile")
                    com.darx.foodscaner.R.id.action_camera ->
                        viewPager.currentItem = pagerAdapter.getItemNum("Camera")
                    com.darx.foodscaner.R.id.action_recently_scanned ->
                        viewPager.currentItem = pagerAdapter.getItemNum("RecentlyScanned")
                }
                return true
            }
        })

        viewPager.adapter = pagerAdapter
        viewPager.currentItem = pagerAdapter.getItemNum("Camera")

        if (isProfile == 0) {
            val intent = Intent(this@MainActivity, WelcomeWizardActivity::class.java)
            startActivity(intent)
            profileViewModel.add_(ProfileModel())
        }
    }

}
