package com.darx.foodwise

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager2.widget.ViewPager2
import com.darx.foodwise.adapters.PageAdapter
import com.darx.foodwise.database.GroupViewModel
import com.darx.foodwise.fragments.GroupsFragment
import com.darx.foodwise.fragments.MyGroupsFragment
import com.google.android.material.tabs.TabLayout
import com.miguelcatalan.materialsearchview.MaterialSearchView
import kotlinx.android.synthetic.main.activity_groups.*
//import kotlinx.android.synthetic.main.toolbar.*
import com.google.android.material.appbar.AppBarLayout
import android.app.Activity
import android.view.inputmethod.InputMethodManager


class UserGroupsActivity : AppCompatActivity() {

    private val pagerAdapter = PageAdapter(supportFragmentManager, lifecycle)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_groups)

        setSupportActionBar(groups_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        val groupViewModel = ViewModelProviders.of(this).get(GroupViewModel::class.java)

        pagerAdapter.addFragment(GroupsFragment(groupViewModel), "Groups")
        pagerAdapter.addFragment(MyGroupsFragment(groupViewModel), "MyGroups")

        groups_view_pager.adapter = pagerAdapter

        groups_tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                groups_view_pager.currentItem = tab.position
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        groups_view_pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                groups_tabs.getTabAt(position)?.select()
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
        groups_search_view.setMenuItem(item)

        groups_search_view.setOnQueryTextListener(object : MaterialSearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                onQueryTextChange(query)
                val inputMethodManger = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManger.hideSoftInputFromWindow(groups_view_pager.windowToken, 0)
                return true
            }

            override fun onQueryTextChange(query: String): Boolean {
                when (val current = groups_view_pager.currentItem) {
                    0 -> (pagerAdapter.createFragment(current) as GroupsFragment).searchGroups(query)
                    1 -> (pagerAdapter.createFragment(current) as MyGroupsFragment).searchMyGroups(query)
                }
                return false
            }
        })

        groups_search_view.setOnSearchViewListener(object : MaterialSearchView.SearchViewListener {
            override fun onSearchViewShown() {
                groups_tabs.visibility = View.GONE
                val params = frame_toolbar.layoutParams as AppBarLayout.LayoutParams
                params.scrollFlags = 0
                groups_view_pager.isUserInputEnabled = false;
            }
            override fun onSearchViewClosed() {
                groups_tabs.visibility = View.VISIBLE
                val params = frame_toolbar.layoutParams as AppBarLayout.LayoutParams
                params.scrollFlags = AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL or AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS
                groups_view_pager.isUserInputEnabled = true;
            }
        })

        return true
    }

}
