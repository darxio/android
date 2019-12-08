package com.darx.foodwise.fragments


import androidx.fragment.app.Fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup


import android.content.Intent
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.LinearLayout
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.darx.foodwise.*
import com.darx.foodwise.adapters.GroupAdapter
import com.darx.foodwise.database.GroupModel
import com.darx.foodwise.database.GroupViewModel
import kotlinx.android.synthetic.main.activity_favorites.*
import kotlinx.android.synthetic.main.activity_groups.*
import kotlinx.android.synthetic.main.fragment_groups.*
import kotlinx.android.synthetic.main.fragment_groups.view.*
import java.io.Serializable


class MyGroupsFragment(val groupViewModel: GroupViewModel) : Fragment() {

    private var myGroupAdapter: GroupAdapter? = null
    private var queryString: String = ""


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
        }, 0, 11)
        val myGroupsRecycler = view.findViewById<RecyclerView>(R.id.groups_rv)
        myGroupsRecycler.adapter = myGroupAdapter

        groupViewModel.getAll_().observe(this, object : Observer<List<GroupModel>> {
            override fun onChanged(l: List<GroupModel>?) {
                if (l != null && l.isNotEmpty()) {
                    groups_fragments_frame.visibility = GONE
                } else {
                    showEmptyFragment()
                    groups_fragments_frame.visibility = VISIBLE
                }
                myGroupAdapter?.addItems(l ?: return)
            }
        })

        return view
    }

    fun searchMyGroups(query: String) {
        queryString = query
        val data = matchMyGroups(query)
        if (data.isEmpty()) {
            showEmptyFragment()
            groups_fragments_frame.visibility = VISIBLE
        } else {
            groups_fragments_frame.visibility = GONE
        }
        myGroupAdapter?.addItems(data)
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

    private fun showEmptyFragment() {
        var emptyFragment: EmptyFragment? = null
        if (queryString.isEmpty()) {
            emptyFragment = EmptyFragment(
                R.drawable.empty_groups,
                getString(R.string.empty_groups_message),
                getString(R.string.empty_groups_button),
                LinearLayout.VERTICAL,
                View.OnClickListener {
                    (activity as UserGroupsActivity).changeTab(0)
                }
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