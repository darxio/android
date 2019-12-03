package com.darx.foodscaner.fragments


import android.app.Activity
import androidx.fragment.app.Fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup


import android.content.Intent
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.darx.foodscaner.*
import com.darx.foodscaner.adapters.GroupAdapter
import com.darx.foodscaner.database.GroupModel
import com.darx.foodscaner.database.GroupViewModel
import com.darx.foodscaner.services.NetworkDataSourceImpl
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.activity_favorites.*
import kotlinx.android.synthetic.main.fragment_groups.*
import kotlinx.android.synthetic.main.fragment_groups.view.*
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

    fun searchMyGroups(query: String) {
        myGroupAdapter?.addItems(matchMyGroups(query))
    }

    private fun matchMyGroups(typed: String): List<GroupModel> {
        val matched: MutableList<GroupModel> = mutableListOf()

        val data = groupViewModel.getAll_()
        for (group in data.value!!) {
            if (group.name.contains(typed, ignoreCase=true)) {
                matched.add(group)
            }
        }
        return matched
    }
}