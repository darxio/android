package com.darx.foodscaner

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.darx.foodscaner.adapters.GroupAdapter
import com.darx.foodscaner.adapters.PageAdapter
import com.darx.foodscaner.fragments.GroupsFragment
import com.darx.foodscaner.models.Group
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.activity_groups.*
import kotlinx.android.synthetic.main.activity_ingredient.*
import kotlinx.android.synthetic.main.toolbar.*

class UserGroupsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_groups)

        setSupportActionBar(groupToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

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
