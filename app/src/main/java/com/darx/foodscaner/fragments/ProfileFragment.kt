package com.darx.foodscaner.fragments


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.view.*
import android.R.attr.colorPrimary
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat.setAlpha
import com.darx.foodscaner.*
import com.darx.foodscaner.data.request.RegistrationInfo
import com.darx.foodscaner.services.ApiService
import com.skydoves.balloon.BalloonAnimation
import com.skydoves.balloon.createBalloon
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


/**
 * A simple [Fragment] subclass.
 */
class ProfileFragment : Fragment() {

    private val apiService = ApiService(/*ConnectivityInterceptorImpl(this.baseContext)*/)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        // User API
        view.registrationButton.setOnClickListener {
            registration(it)
        }

        view.loginButton.setOnClickListener {
            login(it)
        }

        view.logoutButton.setOnClickListener {
            logout(it)
        }

        // New activityes
        view.usersGroups.setOnClickListener {
            val intent = Intent(this@ProfileFragment.activity, GroupsActivity::class.java)
            startActivity(intent)
        }

        view.usersProducts.setOnClickListener {
            val intent = Intent(this@ProfileFragment.activity, IngredientsActivity::class.java)
            startActivity(intent)
        }

        view.usersFavorites.setOnClickListener {
            val intent = Intent(this@ProfileFragment.activity, FavoritesActivity::class.java)
            startActivity(intent)
        }

//        view.loginButton.setOnClickListener({
//            val balloon = createBalloon(it.context) {
//                setArrowSize(10)
//                setWidthRatio(1.0f)
//                setHeight(65)
//                setArrowPosition(0.7f)
//                setCornerRadius(4f)
//                setAlpha(0.9f)
//                setText("You can access your profile from on now.")
//                setTextColorResource(R.color.colorAccent)
//                setIconDrawable(ContextCompat.getDrawable(it.context, R.drawable.camera))
//                setBackgroundColorResource(R.color.colorPrimary)
//                setOnBalloonClickListener{Toast.makeText(it.context, "clicked", Toast.LENGTH_SHORT).show()}
//                setBalloonAnimation(BalloonAnimation.FADE)
//                lifecycleOwner?.let { it1 -> setLifecycleOwner(it1) }
//            }
//            balloon.show(it)
//        })

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    private fun registration(view:View) {
//        val networkDataSource = NetworkDataSourceImpl(apiService)
//        networkDataSource.downloadedData.observe(this, Observer {
//            username.text = it.toString()
//        })

        GlobalScope.launch(Dispatchers.Main) {
            val registrationInfo = RegistrationInfo(loginInput.text.toString(), passwordInput.text.toString())
//            networkDataSource.fetchRegistration(registrationInfo)

            val response = apiService.registration(registrationInfo).await()
            username.text = registrationInfo.username
        }
    }

    private fun login(view:View) {
        GlobalScope.launch(Dispatchers.Main) {
            val loginInfo = RegistrationInfo(loginInput.text.toString(), passwordInput.text.toString())
            val response = apiService.login(loginInfo).await()
            username.text = loginInfo.username
        }
    }

    private fun logout(view:View) {
        GlobalScope.launch(Dispatchers.Main) {
            val response = apiService.logout().await()
            username.text = "Guest"
        }
    }

}
