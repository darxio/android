package com.darx.foodwise.fragments


import androidx.fragment.app.Fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup


import android.content.Intent
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
import java.io.Serializable


class MyGroupsFragment(val groupViewModel: GroupViewModel) : Fragment() {

    private var myGroupAdapter: GroupAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_groups, container, false)

        // my Groups
        myGroupAdapter = GroupAdapter(emptyList(), groupViewModel, this, object : GroupAdapter.Callback {
            override fun onItemClicked(item: GroupModel) {
                val intent = Intent(activity, GroupActivity::class.java)
                intent.putExtra("GROUP", item as Serializable)
                startActivity(intent)
            }
        }, 0, 90)
        val myGroupsRecycler = view.findViewById<RecyclerView>(R.id.groups_rv)
        myGroupsRecycler.adapter = myGroupAdapter

        groupViewModel.getAll_().observe(this, object : Observer<List<GroupModel>> {
            override fun onChanged(l: List<GroupModel>?) {
                if (l?.size == 0) {
                    groups_fragments_frame.visibility = View.VISIBLE
                    val emptyFragment = EmptyFragment(
                        R.drawable.empty_favourites,
                        "У Вас пока нет любимых продуктов!",
                        "Добавить",
                        LinearLayout.VERTICAL,
                        View.OnClickListener {}
                    )
                    activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.groups_fragments_frame, emptyFragment)?.commit()
                }
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