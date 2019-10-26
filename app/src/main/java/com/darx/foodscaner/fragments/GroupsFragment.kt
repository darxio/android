package com.darx.foodscaner.fragments


import androidx.fragment.app.Fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup


import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import com.darx.foodscaner.*
import com.darx.foodscaner.adapters.GroupAdapter
import com.darx.foodscaner.models.Group
import com.google.android.material.textfield.TextInputLayout


class GroupsFragment(private val isSearch: Boolean) : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_groups, container, false)

        var groups: List<Group>? = null
        if (isSearch) {
            groups = getAllGroups("")
        } else {
            val groupSearch = view.findViewById<TextInputLayout>(R.id.groupSearch)
            groupSearch?.visibility = View.GONE
            groups = getUsersGroups()
        }

        val groupAdapter = GroupAdapter(groups, object : GroupAdapter.Callback {
            override fun onItemClicked(item: Group) {
                val intent = Intent(this@GroupsFragment.activity, GroupActivity::class.java)
                startActivity(intent)
            }
        })

        val groupsRecycler = view.findViewById<RecyclerView>(R.id.groupsRecycler)
        groupsRecycler.adapter = groupAdapter

        return view
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
