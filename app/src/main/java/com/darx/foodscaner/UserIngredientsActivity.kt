package com.darx.foodscaner

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.darx.foodscaner.adapters.IngredientAdapter
import com.darx.foodscaner.adapters.PageAdapter
import com.darx.foodscaner.fragments.IngredientsFragment
import com.darx.foodscaner.models.Ingredient
import com.darx.foodscaner.services.ApiService
import com.darx.foodscaner.services.ConnectivityInterceptorImpl
import com.darx.foodscaner.services.NetworkDataSourceImpl
import com.miguelcatalan.materialsearchview.MaterialSearchView
import kotlinx.android.synthetic.main.activity_groups.*
import kotlinx.android.synthetic.main.activity_ingredients.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class UserIngredientsActivity : AppCompatActivity() {

    private var networkDataSource: NetworkDataSourceImpl? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ingredients)

        setSupportActionBar(ingredientsToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        val apiService = ApiService(ConnectivityInterceptorImpl(this))
        networkDataSource = NetworkDataSourceImpl(apiService)

        networkDataSource?.productSearch?.observe(this@UserIngredientsActivity, Observer {
            myIngredientsTitle.text = it[0].toString() ?: ""
        })

        // my Ingredients
        val myIngredientsAdapter = IngredientAdapter(getUsersIngredients(), object : IngredientAdapter.Callback {
            override fun onItemClicked(item: Ingredient) {
                val intent = Intent(this@UserIngredientsActivity, IngredientActivity::class.java)
                startActivity(intent)
            }
        })
        val myIngredientsRecycler = this.findViewById<RecyclerView>(R.id.myIngredientsRecycler)
        myIngredientsRecycler.adapter = myIngredientsAdapter

        // all Ingredients
        val allIngredientsAdapter = IngredientAdapter(getAllIngredients(""), object : IngredientAdapter.Callback {
            override fun onItemClicked(item: Ingredient) {
                val intent = Intent(this@UserIngredientsActivity, IngredientActivity::class.java)
                startActivity(intent)
            }
        })
        val allIngredientsRecycler = this.findViewById<RecyclerView>(R.id.allIngredientsRecycler)
        allIngredientsRecycler.adapter = allIngredientsAdapter
    }



    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)

        val item = menu!!.findItem(R.id.action_search)
        ingredientsSearchView.setMenuItem(item)


        ingredientsSearchView.setOnQueryTextListener(object : MaterialSearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                //Do some magic
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if (newText.length >= 4) {
                    GlobalScope.launch(Dispatchers.Main) {
                        networkDataSource?.fetchProductSearch(newText)
                    }
                }
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


    private fun getUsersIngredients(): List<Ingredient> {
        return listOf(
            Ingredient("Сахар"),
            Ingredient("Соль"),
            Ingredient("Уксус"),
            Ingredient("Что-то")
        )
    }

    private fun getAllIngredients(name: String): List<Ingredient> {
        return listOf(
            Ingredient("Сахар"),
            Ingredient("Соль"),
            Ingredient("Уксус"),
            Ingredient("Что-то"),
            Ingredient("Сахар"),
            Ingredient("Соль"),
            Ingredient("Уксус"),
            Ingredient("Что-то"),
            Ingredient("Сахар"),
            Ingredient("Соль"),
            Ingredient("Уксус"),
            Ingredient("Что-то")
        )
    }
}
