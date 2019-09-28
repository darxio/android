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
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat.setAlpha
import com.darx.foodscaner.*


/**
 * A simple [Fragment] subclass.
 */
class ProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(com.darx.foodscaner.R.layout.fragment_profile, container, false)

        view.usersGroups.setOnClickListener({
            val intent = Intent(this@ProfileFragment.activity, GroupsActivity::class.java)
            startActivity(intent)
        })

        view.usersProducts.setOnClickListener({
            val intent = Intent(this@ProfileFragment.activity, IngredientsActivity::class.java)
            startActivity(intent)
        })

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

}
