package com.darx.foodscaner.fragments


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.fragment_profile.view.*
import com.darx.foodscaner.*
import com.darx.foodscaner.services.ApiService
import com.darx.foodscaner.services.ConnectivityInterceptorImpl
import com.darx.foodscaner.services.NetworkDataSourceImpl
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.darx.foodscaner.adapters.GroupAdapter
import com.darx.foodscaner.adapters.IngredientAdapter
import com.darx.foodscaner.database.GroupModel
import com.darx.foodscaner.database.GroupViewModel
import com.darx.foodscaner.database.IngredientModel
import com.darx.foodscaner.database.IngredientViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.Serializable


class ProfileFragment : Fragment() {

    private var networkDataSource: NetworkDataSourceImpl? = null
    private var ingredientViewModel: IngredientViewModel? = null
    private var groupViewModel: GroupViewModel? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        val apiService = ApiService(ConnectivityInterceptorImpl(this.context!!))
        networkDataSource = NetworkDataSourceImpl(apiService, context!!)

        ingredientViewModel = ViewModelProviders.of(this).get(IngredientViewModel::class.java)
        groupViewModel = ViewModelProviders.of(this).get(GroupViewModel::class.java)


        // GROUPS
        val allGroupAdapter: GroupAdapter = GroupAdapter(listOf(), object : GroupAdapter.Callback {
            override fun onItemClicked(item: GroupModel) {
                val intent = Intent(activity, GroupActivity::class.java)
                intent.putExtra("GROUP", item as Serializable)
                startActivity(intent)
            }
        })
        val allGroupsRecycler = view.findViewById<RecyclerView>(R.id.groupsMultiRecycler)
        val layoutManagerGroups = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        allGroupsRecycler.layoutManager = layoutManagerGroups
        allGroupsRecycler.adapter = allGroupAdapter

        networkDataSource?.groups?.observe(this, Observer {
            allGroupAdapter.addItems(it)
        })
        networkDataSource?.groupSearch?.observe(this, Observer {
            allGroupAdapter.addItems(it)
        })
        GlobalScope.launch(Dispatchers.Main) {
            networkDataSource?.fetchGroups()
        }

        // INGREDIENTS
        val allIngredientsAdapter = IngredientAdapter(emptyList(), this, ingredientViewModel, groupViewModel, object : IngredientAdapter.Callback {
            override fun onItemClicked(item: IngredientModel) {
                val intent = Intent(activity, IngredientActivity::class.java)
                intent.putExtra("INGREDIENT", item as Serializable)
                startActivity(intent)
            }
        })
        val ingredientRecycler = view.findViewById<RecyclerView>(R.id.ingredientMultiRecycler)
        val layoutManagerIngredients = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        ingredientRecycler.layoutManager = layoutManagerIngredients
        ingredientRecycler.adapter = allIngredientsAdapter

        networkDataSource?.ingredients?.observe(this, Observer {
            allIngredientsAdapter.addItems(it)
        })
        networkDataSource?.ingredientSearch?.observe(this, Observer {
            allIngredientsAdapter.addItems(it)
        })
        GlobalScope.launch(Dispatchers.Main) {
            networkDataSource?.fetchIngredients(30, 0)
        }


        // New activities
        view.moreGroups.setOnClickListener {
            val intent = Intent(this@ProfileFragment.activity, UserGroupsActivity::class.java)
            startActivity(intent)
        }
        view.moreIngredients.setOnClickListener {
            val intent = Intent(this@ProfileFragment.activity, UserIngredientsActivity::class.java)
            startActivity(intent)
        }
        view.usersFavorites.setOnClickListener {
            val intent = Intent(this@ProfileFragment.activity, FavoritesActivity::class.java)
            startActivity(intent)
        }
        
        return view
    }
}
