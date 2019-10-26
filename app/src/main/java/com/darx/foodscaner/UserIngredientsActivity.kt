package com.darx.foodscaner

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.darx.foodscaner.adapters.PageAdapter
import com.darx.foodscaner.fragments.IngredientsFragment
import kotlinx.android.synthetic.main.activity_groups.*
<<<<<<< HEAD
import kotlinx.android.synthetic.main.activity_ingredients.*
import kotlinx.android.synthetic.main.activity_product.*
=======
>>>>>>> af5cc3a2d9755dffaf9c303cce43138bd4361d2a

class UserIngredientsActivity : AppCompatActivity() {

    private val pagerAdapter = PageAdapter(supportFragmentManager, lifecycle)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        pagerAdapter.addFragment(IngredientsFragment(false), "MyIngredients")
        pagerAdapter.addFragment(IngredientsFragment(true), "ShearchIngredients")

        setContentView(R.layout.activity_ingredients)
        viewPager.adapter = pagerAdapter
    }
}
