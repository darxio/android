package com.darx.foodwise

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.darx.foodwise.adapters.IngredientAdapter
import com.darx.foodwise.database.GroupViewModel
import com.darx.foodwise.database.IngredientModel
import com.darx.foodwise.database.IngredientViewModel
import com.darx.foodwise.services.ApiService
import com.darx.foodwise.services.ConnectivityInterceptorImpl
import com.darx.foodwise.services.NetworkDataSourceImpl
import com.miguelcatalan.materialsearchview.MaterialSearchView
import kotlinx.android.synthetic.main.activity_group_ingredients.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.Serializable

class GroupIngredientsActivity : AppCompatActivity() {

    private var networkDataSource: NetworkDataSourceImpl? = null
    private var groupIngredientsAdapter: IngredientAdapter? = null
    private var groupId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_ingredients)

        setSupportActionBar(group_ingredients_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        val ingredientViewModel = ViewModelProviders.of(this).get(IngredientViewModel::class.java)
        val groupViewModel = ViewModelProviders.of(this).get(GroupViewModel::class.java)

        // group Ingredients
        groupIngredientsAdapter = IngredientAdapter(emptyList(), baseContext,this, ingredientViewModel, groupViewModel, object : IngredientAdapter.Callback {
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

        networkDataSource?.groupIngredients?.observe(this@GroupIngredientsActivity, Observer {
            groupIngredientsAdapter?.addItems(it)
        })

        networkDataSource?.groupIngredientsSearch?.observe(this@GroupIngredientsActivity, Observer {
            groupIngredientsAdapter?.addItems(it)
        })

        groupId = intent.getIntExtra("GROUP_ID", 0)
        GlobalScope.launch(Dispatchers.Main) {
            networkDataSource?.fetchGroupIngredients(groupId?:0, 20, 0)
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
                onQueryTextChange(query)
                val inputMethodManger = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManger.hideSoftInputFromWindow(group_ingredients_rv.windowToken, 0)
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if (newText.isEmpty()) {
                    GlobalScope.launch(Dispatchers.Main) {
                        networkDataSource?.fetchGroupIngredients(groupId ?: 0, 20, 0)
                    }
                } else {
                    GlobalScope.launch(Dispatchers.Main) {
                        networkDataSource?.fetchGroupIngredientsSearch(groupId?:0, newText, 20, 0)
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
}
