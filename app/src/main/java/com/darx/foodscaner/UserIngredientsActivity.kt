package com.darx.foodscaner

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.darx.foodscaner.adapters.PageAdapter
import com.darx.foodscaner.fragments.IngredientsFragment
import kotlinx.android.synthetic.main.activity_groups.*

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
