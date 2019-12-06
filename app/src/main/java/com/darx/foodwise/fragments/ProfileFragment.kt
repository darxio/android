package com.darx.foodwise.fragments


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.fragment_profile.view.*
import com.darx.foodwise.*
import com.darx.foodwise.services.ApiService
import com.darx.foodwise.services.ConnectivityInterceptorImpl
import com.darx.foodwise.services.NetworkDataSourceImpl
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.darx.foodwise.adapters.*
import com.darx.foodwise.database.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.Serializable






class ProfileFragment : Fragment() {

    private var networkDataSource: NetworkDataSourceImpl? = null
    private var ingredientViewModel: IngredientViewModel? = null
    private var productViewModel: ProductViewModel? = null
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
        productViewModel = ViewModelProviders.of(this).get(ProductViewModel::class.java)

        // GROUPS
        val allGroupAdapter: GroupAdapter = GroupAdapter(listOf(), groupViewModel!!, this, object : GroupAdapter.Callback {
            override fun onItemClicked(item: GroupModel) {
                val intent = Intent(activity, GroupActivity::class.java)
                intent.putExtra("GROUP", item as Serializable)
                startActivity(intent)
            }
        }, 2450, 60)
        val allGroupsRecycler = view.findViewById<RecyclerView>(R.id.groups_multi_rv)
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
        val allIngredientsAdapter = ChipsAdapter(emptyList(), activity!!.baseContext, this, ingredientViewModel, groupViewModel, object : IngredientAdapter.Callback {
            override fun onItemClicked(item: IngredientModel) {
                val intent = Intent(activity, IngredientActivity::class.java)
                intent.putExtra("INGREDIENT", item as Serializable)
                startActivity(intent)
            }
        })
        val ingredientRecycler = view.findViewById<RecyclerView>(R.id.ingredients_multi_rv)
        val layoutManagerIngredients = GridLayoutManager(activity, 2, GridLayoutManager.HORIZONTAL, false)
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
                        val intent = Intent(this@ProfileFragment.activity, FavoritesActivity::class.java)
                        startActivity(intent)
                    }
//                    val emptyFragment = EmptyFragment(
//                        R.drawable.ic_star_black,
//                        "У Вас пока нет любимых продуктов!",
//                        "Добавить",
//                        LinearLayout.HORIZONTAL,
//                        View.OnClickListener {
//                            (activity as MainActivity?)?.chooseFragment(2)
//                        }
//                    )
//                    activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.favorites_recycler_frame, emptyFragment)?.commit()
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
            startActivity(intent)
        }
        
        return view
    }
}
