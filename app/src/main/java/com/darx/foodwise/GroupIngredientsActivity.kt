package com.darx.foodwise

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.darx.foodwise.adapters.IngredientAdapter
import com.darx.foodwise.database.GroupModel
import com.darx.foodwise.database.GroupViewModel
import com.darx.foodwise.database.IngredientModel
import com.darx.foodwise.database.IngredientViewModel
import com.darx.foodwise.fragments.EmptyFragment
import com.darx.foodwise.services.ApiService
import com.darx.foodwise.services.ConnectivityInterceptorImpl
import com.darx.foodwise.services.NetworkDataSource
import com.darx.foodwise.services.NetworkDataSourceImpl
import com.miguelcatalan.materialsearchview.MaterialSearchView
import kotlinx.android.synthetic.main.activity_group_ingredients.*
import kotlinx.android.synthetic.main.fragment_ingredients.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.Serializable

class GroupIngredientsActivity : AppCompatActivity() {

    private var networkDataSource: NetworkDataSourceImpl? = null
    private var groupIngredientsAdapter: IngredientAdapter? = null
    private var groupId: Int? = null

    private var groupsDB: List<GroupModel> = listOf()

    private var ingredients: List<IngredientModel> = listOf()
    private var ingredientsDB: List<IngredientModel> = listOf()

    private var queryString: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_ingredients)

        setSupportActionBar(group_ingredients_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        val ingredientViewModel = ViewModelProviders.of(this).get(IngredientViewModel::class.java)
        val groupViewModel = ViewModelProviders.of(this).get(GroupViewModel::class.java)

        // group Ingredients
        groupIngredientsAdapter = IngredientAdapter(emptyList(), baseContext,
            object : IngredientAdapter.Callback {
                override fun onItemClicked(item: IngredientModel) {
                    if (!item.ok) {
                        if (item.groupMached) {
                            item.allowed = true
                            ingredientViewModel.add_(item)
                        } else {
                            ingredientViewModel.deleteOne_(item)
                        }
                    } else {
                        if (item.groupMached) {
                            ingredientViewModel.deleteOne_(item)
                        } else {
                            item.allowed = false
                            ingredientViewModel.add_(item)
                        }
                    }
                }
            },
            object : IngredientAdapter.CallbackInfo {
                override fun onItemClicked(item: IngredientModel) {
                    val intent = Intent(this@GroupIngredientsActivity, IngredientActivity::class.java)
                    intent.putExtra("INGREDIENT", item as Serializable)
                    startActivity(intent)
                }
            })
        val groupIngredientsRecycler = this.findViewById<RecyclerView>(R.id.group_ingredients_rv)
        groupIngredientsRecycler.adapter = groupIngredientsAdapter

        val apiService = ApiService(ConnectivityInterceptorImpl(this))
        networkDataSource = NetworkDataSourceImpl(apiService, this)

        networkDataSource?.groupIngredients?.observe(this, Observer {
            ingredients = it
            filter()
            if (ingredients.isEmpty()){
                showEmptyFragment()
                group_ingredients_fragments_frame.visibility = View.VISIBLE
            } else {
                group_ingredients_fragments_frame.visibility = View.GONE
            }
            groupIngredientsAdapter?.addItems(ingredients)
        })
        networkDataSource?.groupIngredientsSearch?.observe(this, Observer {
            ingredients = it
            filter()
            if (ingredients.isEmpty()){
                showEmptyFragment()
                group_ingredients_fragments_frame.visibility = View.VISIBLE
            } else {
                group_ingredients_fragments_frame.visibility = View.GONE
            }
            groupIngredientsAdapter?.addItems(ingredients)
        })

        groupViewModel.getAll_().observe(this, Observer {
            groupsDB = it
            filter()
            groupIngredientsAdapter?.addItems(ingredients)
        })
        ingredientViewModel.getAll_().observe(this, Observer<List<IngredientModel>> {
            ingredientsDB = it
            filter()
            groupIngredientsAdapter?.addItems(ingredients)
        })

        groupId = intent.getIntExtra("GROUP_ID", 0)
        GlobalScope.launch(Dispatchers.Main) {
            networkDataSource?.fetchGroupIngredients(groupId?:0, 20, 0, object : NetworkDataSource.Callback {
                override fun onNoConnectivityException() {
                    Log.e("HTTP", "Wrong answer.")
                    group_ingredients_fragments_frame.visibility = View.VISIBLE
                    showEmptyFragment()
                }
                override fun onHttpException() {}
                override fun onTimeoutException() {}
                override fun onException() {}
            })
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)

        val item = menu!!.findItem(R.id.action_search)
        group_ingredients_search_view.setMenuItem(item)


        group_ingredients_search_view.setOnQueryTextListener(object : MaterialSearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                queryString = query
                onQueryTextChange(query)
                val inputMethodManger = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManger.hideSoftInputFromWindow(group_ingredients_rv.windowToken, 0)
                return true
            }

            override fun onQueryTextChange(query: String): Boolean {
                queryString = query
                if (query.isEmpty()) {
                    GlobalScope.launch(Dispatchers.Main) {
                        networkDataSource?.fetchGroupIngredients(groupId ?: 0, 20, 0, object : NetworkDataSource.Callback {
                            override fun onNoConnectivityException() {
                                Log.e("HTTP", "Wrong answer.")
                                group_ingredients_fragments_frame.visibility = View.VISIBLE
                                showEmptyFragment()
                            }
                            override fun onHttpException() {}
                            override fun onTimeoutException() {}
                            override fun onException() {}
                        })
                    }
                } else {
                    GlobalScope.launch(Dispatchers.Main) {
                        networkDataSource?.fetchGroupIngredientsSearch(groupId?:0, query, 20, 0, object : NetworkDataSource.Callback {
                            override fun onNoConnectivityException() {
                                Log.e("HTTP", "Wrong answer.")
                                group_ingredients_fragments_frame.visibility = View.VISIBLE
                                showEmptyFragment()
                            }
                            override fun onHttpException() {}
                            override fun onTimeoutException() {}
                            override fun onException() {}
                        })
                    }
                }
                return false
            }
        })

        group_ingredients_nead_scroll.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, _, _, _, _ ->
            val inputMethodManger = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManger.hideSoftInputFromWindow(v.windowToken, 0)
        })


        return true
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
        supportFragmentManager.beginTransaction().replace(R.id.group_ingredients_fragments_frame, emptyFragment).commit()
    }

    private fun filter() {
        for (ingredient in ingredients) {
            ingredient.groupMached = false
            ingredient.ok = true
        }

        for (group in groupsDB) {
            group.isInBase = true
            for (ingredient in ingredients) {
                for (g in ingredient.groups) {
                    if (group.id == g) {
                        ingredient.groupMached = true
                        ingredient.ok = false
                    }
                }
            }
        }

        for (ingredient in ingredients) {
            for (i in ingredientsDB) {
                if (ingredient.id == i.id) {
                    ingredient.ok = i.allowed!!
                }
            }
        }
    }
}
