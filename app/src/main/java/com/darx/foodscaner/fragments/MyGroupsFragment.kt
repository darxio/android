package com.darx.foodscaner.fragments


import androidx.fragment.app.Fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup


import android.content.Intent
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.darx.foodscaner.*
import com.darx.foodscaner.adapters.GroupAdapter
import com.darx.foodscaner.database.GroupModel
import com.darx.foodscaner.database.GroupViewModel
import com.darx.foodscaner.services.NetworkDataSourceImpl
import com.google.android.material.textfield.TextInputLayout
import java.io.Serializable


class MyGroupsFragment(val groupViewModel: GroupViewModel) : Fragment() {

    private var myGroupAdapter: GroupAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_groups, container, false)

        // my Groups
        myGroupAdapter = GroupAdapter(emptyList(), object : GroupAdapter.Callback {
            override fun onItemClicked(item: GroupModel) {
                val intent = Intent(activity, GroupActivity::class.java)
                intent.putExtra("GROUP", item as Serializable)
                startActivity(intent)
            }
        })
        val myGroupsRecycler = view.findViewById<RecyclerView>(R.id.groups_rv)
        myGroupsRecycler.adapter = myGroupAdapter

        groupViewModel.getAll_().observe(this, object : Observer<List<GroupModel>> {
            override fun onChanged(l: List<GroupModel>?) {
                myGroupAdapter?.addItems(l ?: return)
            }
        })

        return view
    }
}