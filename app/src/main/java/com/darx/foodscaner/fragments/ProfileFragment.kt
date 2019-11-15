package com.darx.foodscaner.fragments


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_profile.view.*
import com.darx.foodscaner.*
import com.darx.foodscaner.services.ApiService
import com.darx.foodscaner.services.ConnectivityInterceptorImpl
import com.darx.foodscaner.services.NetworkDataSourceImpl

class ProfileFragment : Fragment() {

    private var networkDataSource: NetworkDataSourceImpl? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        val apiService = ApiService(ConnectivityInterceptorImpl(this.context!!))
        networkDataSource = NetworkDataSourceImpl(apiService, context!!)

        // New activities
        view.usersGroups.setOnClickListener {
            val intent = Intent(this@ProfileFragment.activity, UserGroupsActivity::class.java)
            startActivity(intent)
        }

        view.usersProducts.setOnClickListener {
            val intent = Intent(this@ProfileFragment.activity, UserIngredientsActivity::class.java)
            startActivity(intent)
        }

        view.usersFavorites.setOnClickListener {
            val intent = Intent(this@ProfileFragment.activity, FavoritesActivity::class.java)
            startActivity(intent)
        }
        
        return view
    }
}
