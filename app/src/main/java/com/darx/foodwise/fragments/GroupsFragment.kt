package com.darx.foodwise.fragments


import android.content.Intent
import androidx.fragment.app.Fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.darx.foodwise.*
import com.darx.foodwise.adapters.GroupAdapter
import com.darx.foodwise.database.GroupModel
import com.darx.foodwise.database.GroupViewModel
import com.darx.foodwise.services.ApiService
import com.darx.foodwise.services.ConnectivityInterceptorImpl
import com.darx.foodwise.services.NetworkDataSourceImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.Serializable


class GroupsFragment(val groupViewModel: GroupViewModel) : Fragment() {

    private var networkDataSource: NetworkDataSourceImpl? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_groups, container, false)

        // all Groups
        val allGroupAdapter = GroupAdapter(emptyList(), groupViewModel, this, object : GroupAdapter.Callback {
            override fun onItemClicked(item: GroupModel) {
                val intent = Intent(activity, GroupActivity::class.java)
                intent.putExtra("GROUP", item as Serializable)
                startActivity(intent)
            }
        },0, 40)
        val allGroupsRecycler = view.findViewById<RecyclerView>(R.id.groups_rv)
        allGroupsRecycler.adapter = allGroupAdapter

        val apiService = ApiService(ConnectivityInterceptorImpl(this.context!!))
        networkDataSource = NetworkDataSourceImpl(apiService, this.context!!)

        networkDataSource?.groups?.observe(this, Observer {
            allGroupAdapter.addItems(it)
        })
        networkDataSource?.groupSearch?.observe(this, Observer {
            allGroupAdapter.addItems(it)
        })

        GlobalScope.launch(Dispatchers.Main) {
            networkDataSource?.fetchGroups()
        }

        return view
    }

    fun searchGroups(query: String) {
        if (query.isEmpty()) {
            GlobalScope.launch(Dispatchers.Main) {
                networkDataSource?.fetchGroups()
            }
        } else {
            GlobalScope.launch(Dispatchers.Main) {
                networkDataSource?.fetchGroupSearch(query)
            }
        }
    }
}