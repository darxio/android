package com.darx.foodscaner.fragments


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.view.*
import androidx.lifecycle.Observer
import com.darx.foodscaner.*
import com.darx.foodscaner.data.request.LoginRqst
import com.darx.foodscaner.data.request.RegistrationRqst
import com.darx.foodscaner.services.ApiService
import com.darx.foodscaner.services.ConnectivityInterceptorImpl
import com.darx.foodscaner.services.NetworkDataSourceImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private var networkDataSource: NetworkDataSourceImpl? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        val apiService = ApiService(ConnectivityInterceptorImpl(this.context!!))
        networkDataSource = NetworkDataSourceImpl(apiService)


        networkDataSource?.registration?.observe(this, Observer {
            username.text = it.message
        })

        networkDataSource?.login?.observe(this, Observer {
            username.text = it.message
        })

        networkDataSource?.logout?.observe(this, Observer {
            username.text = "Guest"
        })

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

        view.cameraIcon.setOnClickListener {
//            viewPager.setCurrentItem(1)
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

    private fun registration(view:View) {
        GlobalScope.launch(Dispatchers.Main) {
            val registrationInfo = RegistrationRqst(loginInput.text.toString(), passwordInput.text.toString())
            networkDataSource?.fetchRegistration(registrationInfo)
        }
    }

    private fun login(view:View) {
        GlobalScope.launch(Dispatchers.Main) {
            val loginInfo = LoginRqst(loginInput.text.toString(), passwordInput.text.toString())
            networkDataSource?.fetchLogin(loginInfo)
        }
    }

    private fun logout(view:View) {
        GlobalScope.launch(Dispatchers.Main) {
            networkDataSource?.fetchLogout()
        }
    }

}
