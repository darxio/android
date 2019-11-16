package com.darx.foodscaner

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.darx.foodscaner.adapters.GroupAdapter
import com.darx.foodscaner.database.GroupModel
import com.darx.foodscaner.database.GroupViewModel
import com.darx.foodscaner.services.ApiService
import com.darx.foodscaner.services.ConnectivityInterceptorImpl
import com.darx.foodscaner.services.NetworkDataSourceImpl
import com.miguelcatalan.materialsearchview.MaterialSearchView
import kotlinx.android.synthetic.main.activity_groups.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.Serializable

class UserGroupsActivity : AppCompatActivity() {

    private var groupViewModel: GroupViewModel? = null
    private var networkDataSource: NetworkDataSourceImpl? = null
    private var myGroupAdapter: GroupAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_groups)

        setSupportActionBar(groupToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        // my Groups
        myGroupAdapter = GroupAdapter(emptyList(), object : GroupAdapter.Callback {
            override fun onItemClicked(item: GroupModel) {
                val intent = Intent(this@UserGroupsActivity, GroupActivity::class.java)
                intent.putExtra("GROUP", item as Serializable)
                startActivity(intent)
            }
        })
        val myGroupsRecycler = this.findViewById<RecyclerView>(R.id.myGroupsRecycler)
        myGroupsRecycler.adapter = myGroupAdapter

        groupViewModel = ViewModelProviders.of(this).get(GroupViewModel::class.java)
        groupViewModel?.getAll_()?.observe(this@UserGroupsActivity, object : Observer<List<GroupModel>> {
            override fun onChanged(l: List<GroupModel>?) {
                myGroupAdapter?.addItems(l ?: return)
            }
        })

        // all Groups
        val allGroupAdapter: GroupAdapter = GroupAdapter(emptyList(), object : GroupAdapter.Callback {
            override fun onItemClicked(item: GroupModel) {
                val intent = Intent(this@UserGroupsActivity, GroupActivity::class.java)
                intent.putExtra("GROUP", item as Serializable)
                startActivity(intent)
            }
        })
        val allGroupsRecycler = this.findViewById<RecyclerView>(R.id.allGroupsRecycler)
        allGroupsRecycler.adapter = allGroupAdapter

        val apiService = ApiService(ConnectivityInterceptorImpl(this))
        networkDataSource = NetworkDataSourceImpl(apiService)

        networkDataSource?.groups?.observe(this@UserGroupsActivity, Observer {
            allGroupAdapter.addItems(it)
        })
        networkDataSource?.groupSearch?.observe(this@UserGroupsActivity, Observer {
            allGroupAdapter.addItems(it)
        })

        GlobalScope.launch(Dispatchers.Main) {
            networkDataSource?.fetchGroups()
        }
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
        groupsSearchView.setMenuItem(item)


        groupsSearchView.setOnQueryTextListener(object : MaterialSearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                //Do some magic
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                // take data for all groups
                if (newText.length >= 3) {
                    GlobalScope.launch(Dispatchers.Main) {
                        networkDataSource?.fetchGroupSearch(newText)
                    }
                }

                // take data for my groups
                myGroupAdapter?.addItems(matchMyGroups(newText))

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

    fun matchMyGroups(typed: String): List<GroupModel> {
        val matched: MutableList<GroupModel> = mutableListOf()

        val data = groupViewModel?.getAll_()!!
        for (group in data.value!!) {
            if (group.name.contains(typed, ignoreCase=true)) {
                matched.add(group)
            }
        }
        return matched
    }
}
