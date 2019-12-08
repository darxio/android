package com.darx.foodwise.fragments


import android.content.Intent
import androidx.fragment.app.Fragment


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.darx.foodwise.*
import com.darx.foodwise.adapters.GroupAdapter
import com.darx.foodwise.camerafragment.camera.WorkflowModel
import com.darx.foodwise.database.GroupModel
import com.darx.foodwise.database.GroupViewModel
import com.darx.foodwise.database.ProductModel
import com.darx.foodwise.services.ApiService
import com.darx.foodwise.services.ConnectivityInterceptorImpl
import com.darx.foodwise.services.NetworkDataSource
import com.darx.foodwise.services.NetworkDataSourceImpl
import kotlinx.android.synthetic.main.activity_groups.*
import kotlinx.android.synthetic.main.fragment_groups.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.Serializable


class GroupsFragment(val groupViewModel: GroupViewModel) : Fragment() {

    private var networkDataSource: NetworkDataSourceImpl? = null
    private var groups: List<GroupModel> = listOf()
    private var groupsDB: List<GroupModel> = listOf()

    private var queryString: String = ""

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
        },0, 15)
        val allGroupsRecycler = view.findViewById<RecyclerView>(R.id.groups_rv)
        allGroupsRecycler.adapter = allGroupAdapter

        val apiService = ApiService(ConnectivityInterceptorImpl(this.context!!))
        networkDataSource = NetworkDataSourceImpl(apiService, this.context!!)

        networkDataSource?.groups?.observe(this, Observer {
            groups = it
            filter()
            if (groups.isEmpty()){
                showEmptyFragment()
                groups_fragments_frame.visibility = VISIBLE
            } else {
                groups_fragments_frame.visibility = GONE
            }
            allGroupAdapter.addItems(groups)
        })
        networkDataSource?.groupSearch?.observe(this, Observer {
            groups = it
            filter()
            if (groups.isEmpty()){
                showEmptyFragment()
                groups_fragments_frame.visibility = VISIBLE
            } else {
                groups_fragments_frame.visibility = GONE
            }
            allGroupAdapter.addItems(groups)
        })
        groupViewModel.getAll_().observe(this,
            Observer<List<GroupModel>> {
                groupsDB = it
                filter()
                allGroupAdapter.addItems(groups)
            })


        GlobalScope.launch(Dispatchers.Main) {
            networkDataSource?.fetchGroups(object : NetworkDataSource.Callback {
                override fun onNoConnectivityException() {
                    Log.e("HTTP", "Wrong answer.")
                    groups_fragments_frame.visibility = VISIBLE
                    showEmptyFragment()
                }
                override fun onHttpException() {}
                override fun onTimeoutException() {}
                override fun onException() {}
            })
        }

        return view
    }

    fun searchGroups(query: String) {
        queryString = query
        if (query.isEmpty()) {
            GlobalScope.launch(Dispatchers.Main) {
                networkDataSource?.fetchGroups(object : NetworkDataSource.Callback {
                    override fun onNoConnectivityException() {
                        Log.e("HTTP", "Wrong answer.")
                        groups_fragments_frame.visibility = VISIBLE
                        showEmptyFragment()
                    }
                    override fun onHttpException() {}
                    override fun onTimeoutException() {}
                    override fun onException() {}

                })
            }
        } else {
            GlobalScope.launch(Dispatchers.Main) {
                networkDataSource?.fetchGroupSearch(query, object : NetworkDataSource.Callback {
                    override fun onNoConnectivityException() {
                        Log.e("HTTP", "Wrong answer.")
                        groups_fragments_frame.visibility = VISIBLE
                        showEmptyFragment()
                    }
                    override fun onHttpException() {}
                    override fun onTimeoutException() {}
                    override fun onException() {}
                })
            }
        }
    }

    private fun filter() {
        for (element in groupsDB) {
            for (group in groups) {
                if (element.id == group.id) {
                    group.isInBase = true
                }
            }
        }
    }

    private fun showEmptyFragment() {
        var emptyFragment: EmptyFragment? = null
        if (queryString.isEmpty()) {
            emptyFragment = EmptyFragment(
                R.drawable.empty_no_internet,
                getString(R.string.empty_internet_message),
                getString(R.string.empty_internet_button),
                LinearLayout.VERTICAL,
                View.OnClickListener {}
            )
        } else {
            emptyFragment = EmptyFragment(
                R.drawable.empty_search,
                getString(R.string.empty_search_message),
                getString(R.string.empty_search_button),
                LinearLayout.VERTICAL,
                View.OnClickListener {}
            )
        }
        activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.groups_fragments_frame, emptyFragment)?.commit()
    }
}