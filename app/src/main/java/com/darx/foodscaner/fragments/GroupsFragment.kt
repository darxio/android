package com.darx.foodscaner.fragments


import android.content.Intent
import androidx.fragment.app.Fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.darx.foodscaner.*
import com.darx.foodscaner.adapters.GroupAdapter
import com.darx.foodscaner.database.GroupModel
import com.darx.foodscaner.database.GroupViewModel
import com.darx.foodscaner.database.IngredientViewModel
import com.darx.foodscaner.services.ApiService
import com.darx.foodscaner.services.ConnectivityInterceptorImpl
import com.darx.foodscaner.services.NetworkDataSourceImpl
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
        val allGroupAdapter = GroupAdapter(emptyList(), object : GroupAdapter.Callback {
            override fun onItemClicked(item: GroupModel) {
                val intent = Intent(activity, GroupActivity::class.java)
                intent.putExtra("GROUP", item as Serializable)
                startActivity(intent)
            }
        }, 2000)
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
}