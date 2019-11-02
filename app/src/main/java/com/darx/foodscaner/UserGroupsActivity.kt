package com.darx.foodscaner

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.darx.foodscaner.adapters.GroupAdapter
import com.darx.foodscaner.adapters.PageAdapter
import com.darx.foodscaner.fragments.GroupsFragment
import com.darx.foodscaner.models.Group
import com.darx.foodscaner.services.ApiService
import com.darx.foodscaner.services.ConnectivityInterceptorImpl
import com.darx.foodscaner.services.NetworkDataSourceImpl
import com.google.android.material.textfield.TextInputLayout
import com.miguelcatalan.materialsearchview.MaterialSearchView
import kotlinx.android.synthetic.main.activity_groups.*
import kotlinx.android.synthetic.main.activity_ingredient.*
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class UserGroupsActivity : AppCompatActivity() {

    private var networkDataSource: NetworkDataSourceImpl? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_groups)

        setSupportActionBar(groupToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        val apiService = ApiService(ConnectivityInterceptorImpl(this))
        networkDataSource = NetworkDataSourceImpl(apiService)

        networkDataSource?.productSearch?.observe(this@UserGroupsActivity, Observer {
            myGroupsTitle.text = it[0].toString() ?: ""
        })

        // my Groups
        val myGroupAdapter = GroupAdapter(getUsersGroups(), object : GroupAdapter.Callback {
            override fun onItemClicked(item: Group) {
                val intent = Intent(this@UserGroupsActivity, GroupActivity::class.java)
                startActivity(intent)
            }
        })
        val myGroupsRecycler = this.findViewById<RecyclerView>(R.id.myGroupsRecycler)
        myGroupsRecycler.adapter = myGroupAdapter

        // all Groups
        val allGroupAdapter = GroupAdapter(getAllGroups(""), object : GroupAdapter.Callback {
            override fun onItemClicked(item: Group) {
                val intent = Intent(this@UserGroupsActivity, GroupActivity::class.java)
                startActivity(intent)
            }
        })
        val allGroupsRecycler = this.findViewById<RecyclerView>(R.id.allGroupsRecycler)
        allGroupsRecycler.adapter = allGroupAdapter
    }

    private fun groups(view: View) {
//        GlobalScope.launch(Dispatchers.Main) {
//            val response = apiService.groups().await()
////            username.text = response.toString()
//
//
//        }
    }



    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)

        val item = menu!!.findItem(R.id.action_search)
        groupsSearchView.setMenuItem(item)


        groupsSearchView.setOnQueryTextListener(object : MaterialSearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                //Do some magic
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if (newText.length >= 4) {
                    GlobalScope.launch(Dispatchers.Main) {
                        networkDataSource?.fetchProductSearch(newText)
                    }
                }
                return false
            }
        })

        groupsSearchView.setOnSearchViewListener(object : MaterialSearchView.SearchViewListener {
            override fun onSearchViewShown() {
                //Do some magic
            }

            override fun onSearchViewClosed() {
                //Do some magic
            }
        })


        return true
    }

    private fun getUsersGroups(): List<Group> {
        return listOf(
            Group(1, "Вегетарианец", "Вкусно"),
            Group(2, "Веган", "Очень вкусно"),
            Group(3, "Мясоед", "Вкусно"),
            Group(4, "Солнцеед", "Очень вкусно"),
            Group(5, "Углеводная диета", "Вкусно")
        )
    }

    private fun getAllGroups(name: String): List<Group> {
        return listOf(
            Group(1, "Вегетарианец", "Вкусно"),
            Group(2, "Веган", "Очень вкусно"),
            Group(3, "Мясоед", "Вкусно"),
            Group(4, "Солнцеед", "Очень вкусно"),
            Group(5, "Углеводная диета", "Вкусно"),
            Group(6, "Вегетарианец", "Вкусно"),
            Group(7, "Веган", "Очень вкусно"),
            Group(8, "Мясоед", "Вкусно"),
            Group(9, "Солнцеед", "Очень вкусно"),
            Group(10, "Углеводная диета", "Вкусно")
        )
    }
}
