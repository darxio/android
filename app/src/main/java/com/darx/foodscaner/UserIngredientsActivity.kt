package com.darx.foodscaner

import android.content.Intent
import android.content.res.ColorStateList
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.graphics.drawable.toDrawable
import com.darx.foodscaner.adapters.PageAdapter
import com.darx.foodscaner.data.Ingredient
import com.darx.foodscaner.fragments.GroupsFragment
import com.darx.foodscaner.fragments.IngredientsFragment
import com.google.android.material.chip.Chip
import kotlinx.android.synthetic.main.activity_groups.*
import kotlinx.android.synthetic.main.activity_product.*

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
