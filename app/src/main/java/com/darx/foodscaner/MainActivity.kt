package com.darx.foodscaner

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.darx.foodscaner.adapters.PageAdapter
import com.darx.foodscaner.fragments.*
//import com.darx.foodscaner.services.ApiService
import com.darx.foodscaner.services.ApiService
import com.darx.foodscaner.data.request.RegistrationInfo
import com.darx.foodscaner.services.ConnectivityInterceptorImpl
import com.darx.foodscaner.services.NetworkDataSource
import com.darx.foodscaner.services.NetworkDataSourceImpl
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

//    val apiService = ApiService(/*ConnectivityInterceptorImpl(this.baseContext)*/)

    override fun onBackPressed() {

        val count = supportFragmentManager.backStackEntryCount

        if (count == 0) {
            super.onBackPressed()
            //additional code
        } else {
            supportFragmentManager.popBackStack()
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val pagerAdapter = PageAdapter(supportFragmentManager, lifecycle)
        pagerAdapter.addFragment(ProfileFragment(), "Profile")
        pagerAdapter.addFragment(CameraFragment(), "Camera")
        pagerAdapter.addFragment(InfoFragment(), "Info")

        viewPager.adapter = pagerAdapter
        viewPager.setCurrentItem(pagerAdapter.getItemNum("Camera"))
    }

}
