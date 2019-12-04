package com.darx.foodscaner

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.darx.foodscaner.adapters.IngredientAdapter
import com.darx.foodscaner.adapters.PageAdapter
import com.darx.foodscaner.database.GroupViewModel
import com.darx.foodscaner.database.IngredientModel
import com.darx.foodscaner.database.IngredientViewModel
import com.darx.foodscaner.fragments.IngredientsFragment
import com.darx.foodscaner.fragments.MyIngredientsFragment
import com.darx.foodscaner.services.ApiService
import com.darx.foodscaner.services.ConnectivityInterceptorImpl
import com.darx.foodscaner.services.NetworkDataSourceImpl
import com.miguelcatalan.materialsearchview.MaterialSearchView
import kotlinx.android.synthetic.main.activity_ingredients.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.Serializable
import com.google.android.material.tabs.TabLayout
import androidx.viewpager.widget.ViewPager
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.activity_groups.*


class UserIngredientsActivity : AppCompatActivity() {

    private val pagerAdapter: PageAdapter = PageAdapter(supportFragmentManager, lifecycle)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ingredients)

        setSupportActionBar(ingredients_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        val ingredientViewModel = ViewModelProviders.of(this).get(IngredientViewModel::class.java)
        val groupViewModel = ViewModelProviders.of(this).get(GroupViewModel::class.java)

        pagerAdapter.addFragment(IngredientsFragment(ingredientViewModel, groupViewModel), "Ingredients")
        pagerAdapter.addFragment(MyIngredientsFragment(ingredientViewModel, groupViewModel), "MyIngredients")

        ingredients_view_pager.adapter = pagerAdapter

        ingredients_tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                ingredients_view_pager.currentItem = tab.position
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        ingredients_view_pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                ingredients_tabs.getTabAt(position)?.select()
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)

        val item = menu!!.findItem(R.id.action_search)
        ingredients_search_view.setMenuItem(item)

        ingredients_search_view.setOnQueryTextListener(object : MaterialSearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                onQueryTextChange(query)
                val inputMethodManger = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManger.hideSoftInputFromWindow(ingredients_view_pager.windowToken, 0)
                return true
            }

            override fun onQueryTextChange(query: String): Boolean {
                when (val current = ingredients_view_pager.currentItem) {
                    0 -> (pagerAdapter.createFragment(current) as IngredientsFragment).searchIngredients(query)
                    1 -> (pagerAdapter.createFragment(current) as MyIngredientsFragment).searchMyIngredients(query)
                }
                return false
            }
        })

        ingredients_search_view.setOnSearchViewListener(object : MaterialSearchView.SearchViewListener {
            override fun onSearchViewShown() {
                ingredients_tabs.visibility = View.GONE
                val params = ingredient_frame_toolbar.layoutParams as AppBarLayout.LayoutParams
                params.scrollFlags = 0
                ingredients_view_pager.isUserInputEnabled = false;
            }

            override fun onSearchViewClosed() {
                ingredients_tabs.visibility = View.VISIBLE
                val params = ingredient_frame_toolbar.layoutParams as AppBarLayout.LayoutParams
                params.scrollFlags = AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL or AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS
                ingredients_view_pager.isUserInputEnabled = true;
            }
        })

        return true
    }
}
