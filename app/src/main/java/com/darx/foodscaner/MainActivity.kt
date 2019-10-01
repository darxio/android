package com.darx.foodscaner

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.darx.foodscaner.adapters.PageAdapter
import com.darx.foodscaner.fragments.*
//import com.darx.foodscaner.services.ApiService
import com.darx.foodscaner.services.ApiService
import com.darx.foodscaner.data.request.RegistrationInfo
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    override fun onBackPressed() {

        val count = supportFragmentManager.backStackEntryCount

        if (count == 0) {
            super.onBackPressed()
            //additional code
        } else {
            supportFragmentManager.popBackStack()
        }

    }

    val apiService = ApiService.create()

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

    fun registration(view:View) {
        GlobalScope.launch(Dispatchers.Main) {
            val registrationInfo = RegistrationInfo(loginInput.text.toString(), passwordInput.text.toString())
            val response = apiService.registration(registrationInfo).await()
            username.text = response.toString()
        }
    }

    fun login(view:View) {
        GlobalScope.launch(Dispatchers.Main) {
            val loginInfo = RegistrationInfo(loginInput.text.toString(), passwordInput.text.toString())
            val response = apiService.login(loginInfo).await()
            username.text = response.toString()
        }
    }

    fun logout(view:View) {
        GlobalScope.launch(Dispatchers.Main) {
            val response = apiService.logout().await()
            username.text = "Guest"
        }
    }

//    fun groups(view:View) {
//        GlobalScope.launch(Dispatchers.Main) {
//            val response = apiService.groupsByName("vegan").await()
//            username.text = response.toString()
//        }
//    }

}
