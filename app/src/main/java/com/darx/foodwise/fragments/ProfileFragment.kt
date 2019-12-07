package com.darx.foodwise.fragments


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.fragment_profile.view.*
import com.darx.foodwise.*
import com.darx.foodwise.services.ApiService
import com.darx.foodwise.services.ConnectivityInterceptorImpl
import com.darx.foodwise.services.NetworkDataSourceImpl
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.darx.foodwise.adapters.*
import com.darx.foodwise.database.*
import com.google.android.material.chip.Chip
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.group_item.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.Serializable
import java.security.acl.Group
import java.util.ArrayList


class ProfileFragment : Fragment() {

    private var networkDataSource: NetworkDataSourceImpl? = null
    private var ingredientViewModel: IngredientViewModel? = null
    private var productViewModel: ProductViewModel? = null
    private var groupViewModel: GroupViewModel? = null

    private var groups: List<GroupModel> = listOf()
    private var groupsDB: List<GroupModel> = listOf()

    private var TO_HISTORY: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        val apiService = ApiService(ConnectivityInterceptorImpl(this.context!!))
        networkDataSource = NetworkDataSourceImpl(apiService, context!!)

        ingredientViewModel = ViewModelProviders.of(this).get(IngredientViewModel::class.java)
        groupViewModel = ViewModelProviders.of(this).get(GroupViewModel::class.java)
        productViewModel = ViewModelProviders.of(this).get(ProductViewModel::class.java)

        // GROUPS
        val allGroupAdapter: GroupAdapter = GroupAdapter(listOf(), object : GroupAdapter.Callback {
            override fun onItemClicked(item: GroupModel) {
                val intent = Intent(activity, GroupActivity::class.java)
                intent.putExtra("GROUP", item as Serializable)
                startActivity(intent)
            }
        }, 2450, 65)
        val allGroupsRecycler = view.findViewById<RecyclerView>(R.id.groups_multi_rv)
        val layoutManagerGroups = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        allGroupsRecycler.layoutManager = layoutManagerGroups
        allGroupsRecycler.adapter = allGroupAdapter

        groupViewModel?.getAll_()?.observe(this, Observer {
            groupsDB = it
            filter()
            allGroupAdapter.addItems(groups)
        })
        networkDataSource?.groups?.observe(this, Observer {
            groups = it
            filter()
            allGroupAdapter.addItems(groups)
        })
        GlobalScope.launch(Dispatchers.Main) {
            networkDataSource?.fetchGroups()
        }

        // INGREDIENTS
        networkDataSource?.ingredients?.observe(this, Observer {
            var j: Int = 0
            for (i in it) {
                val chip = Chip(context!!)
                chip.text = i.name
                if (j < 15) {
                    ingredients_chip_group_1.addView(chip)
                } else {
                    ingredients_chip_group_2.addView(chip)
                }
                j += 1
            }
        })
        GlobalScope.launch(Dispatchers.Main) {
            networkDataSource?.fetchIngredients(30, 0)
        }


        // FAVOURITES
        val productsAdapter = PreviewProductsAdapter(emptyList(), object : PreviewProductsAdapter.Callback {
            override fun onItemClicked(item: ProductModel) {
                val intent = Intent(activity, ProductActivity::class.java)
                intent.putExtra("PRODUCT", item as Serializable)
                startActivity(intent)
            }
        })

        val favouritesRecycler = view.findViewById<RecyclerView>(R.id.favourites_multi_recycler)
        favouritesRecycler.adapter = productsAdapter
        productViewModel!!.getFavourites_().observe(this, object : Observer<List<ProductModel>> {
            override fun onChanged(l: List<ProductModel>?) {
                if (l?.size == 0) {
                    more_favourites.visibility = View.GONE
                    favorites_recycler_frame.visibility = View.VISIBLE
                    add_favs.setOnClickListener {
                        (activity as MainActivity).chooseFragment(2)
                    }
                } else {
                    more_favourites.visibility = View.VISIBLE
                    favorites_recycler_frame.visibility = View.GONE
                }
                productsAdapter.addItems(l ?: return)
            }
        })

        // New activities
        view.more_groups.setOnClickListener {
            val intent = Intent(this@ProfileFragment.activity, UserGroupsActivity::class.java)
            startActivity(intent)
        }
        view.more_ingredients.setOnClickListener {
            val intent = Intent(this@ProfileFragment.activity, UserIngredientsActivity::class.java)
            startActivity(intent)
        }
        view.more_favourites.setOnClickListener {
            val intent = Intent(this@ProfileFragment.activity, FavoritesActivity::class.java)
            startActivityForResult(intent, TO_HISTORY)
        }
        
        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode) {
            TO_HISTORY -> {
                if (resultCode == Activity.RESULT_OK && null != data) {
                    (activity as MainActivity).chooseFragment(2)
                }
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
}
