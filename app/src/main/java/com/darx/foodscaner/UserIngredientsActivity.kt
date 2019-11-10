package com.darx.foodscaner

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.darx.foodscaner.adapters.IngredientAdapter
import com.darx.foodscaner.database.IngredientModel
import com.darx.foodscaner.database.IngredientViewModel
import com.darx.foodscaner.services.ApiService
import com.darx.foodscaner.services.ConnectivityInterceptorImpl
import com.darx.foodscaner.services.NetworkDataSourceImpl
import com.miguelcatalan.materialsearchview.MaterialSearchView
import kotlinx.android.synthetic.main.activity_ingredients.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.Serializable

class UserIngredientsActivity : AppCompatActivity() {

    private var ingredientViewModel: IngredientViewModel? = null
    private var networkDataSource: NetworkDataSourceImpl? = null
    private var myIngredientsAdapter: IngredientAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ingredients)

        setSupportActionBar(ingredientsToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        // my Ingredients
        myIngredientsAdapter = IngredientAdapter(emptyList(), object : IngredientAdapter.Callback {
            override fun onItemClicked(item: IngredientModel) {
                val intent = Intent(this@UserIngredientsActivity, IngredientActivity::class.java)
                intent.putExtra("INGREDIENT", item as Serializable)
                startActivity(intent)
            }
        })
        val myIngredientsRecycler = this.findViewById<RecyclerView>(R.id.myIngredientsRecycler)
        myIngredientsRecycler.adapter = myIngredientsAdapter

        ingredientViewModel = ViewModelProviders.of(this).get(IngredientViewModel::class.java)
        ingredientViewModel?.getAll_()?.observe(this@UserIngredientsActivity, object : Observer<List<IngredientModel>> {
            override fun onChanged(l: List<IngredientModel>?) {
                myIngredientsAdapter?.addItems(l ?: return)
            }
        })

        // all Ingredients
        val allIngredientsAdapter: IngredientAdapter = IngredientAdapter(emptyList(), object : IngredientAdapter.Callback {
            override fun onItemClicked(item: IngredientModel) {
                val intent = Intent(this@UserIngredientsActivity, IngredientActivity::class.java)
                intent.putExtra("INGREDIENT", item as Serializable)
                startActivity(intent)
            }
        })
        val allIngredientsRecycler = this.findViewById<RecyclerView>(R.id.allIngredientsRecycler)
        allIngredientsRecycler.adapter = allIngredientsAdapter

        val apiService = ApiService(ConnectivityInterceptorImpl(this))
        networkDataSource = NetworkDataSourceImpl(apiService)

        networkDataSource?.ingredients?.observe(this@UserIngredientsActivity, Observer {
            allIngredientsAdapter.addItems(it)
        })
        networkDataSource?.ingredientSearch?.observe(this@UserIngredientsActivity, Observer {
            allIngredientsAdapter.addItems(it)
        })

        GlobalScope.launch(Dispatchers.Main) {
            networkDataSource?.fetchIngredients(30, 1)
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
        ingredientsSearchView.setMenuItem(item)


        ingredientsSearchView.setOnQueryTextListener(object : MaterialSearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                // take data for all ingredients
                if (newText.length >= 3) {
                    GlobalScope.launch(Dispatchers.Main) {
                        networkDataSource?.fetchIngredientSearch(newText)
                    }
                }

                // take data for my ingredients
                myIngredientsAdapter?.addItems(matchMyIngredients(newText))

                return false
            }
        })

        ingredientsSearchView.setOnSearchViewListener(object : MaterialSearchView.SearchViewListener {
            override fun onSearchViewShown() {
                //Do some magic
            }

            override fun onSearchViewClosed() {
                //Do some magic
            }
        })


        return true
    }

    fun matchMyIngredients(typed: String): List<IngredientModel> {
        val matched: MutableList<IngredientModel> = mutableListOf()

        val data = ingredientViewModel?.getAll_()!! //?.observe(this@UserIngredientsActivity, object : Observer<List<IngredientModel>>
        for (ingredient in data.value!!) {
            if (ingredient.name.contains(typed, ignoreCase=true)) {
                matched.add(ingredient)
            }
        }
        return matched
    }
}
