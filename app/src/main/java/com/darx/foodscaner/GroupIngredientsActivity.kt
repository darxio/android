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
import kotlinx.android.synthetic.main.activity_group_ingredients.*
import kotlinx.android.synthetic.main.activity_ingredients.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.Serializable

class GroupIngredientsActivity : AppCompatActivity() {

    private var networkDataSource: NetworkDataSourceImpl? = null
    private var groupIngredientsAdapter: IngredientAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_ingredients)

        setSupportActionBar(groupIngredientsToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        // group Ingredients
        groupIngredientsAdapter = IngredientAdapter(emptyList(), object : IngredientAdapter.Callback {
            override fun onItemClicked(item: IngredientModel) {
                val intent = Intent(this@GroupIngredientsActivity, IngredientActivity::class.java)
                intent.putExtra("INGREDIENT", item as Serializable)
                startActivity(intent)
            }
        })
        val groupIngredientsRecycler = this.findViewById<RecyclerView>(R.id.groupIngredientsRecycler)
        groupIngredientsRecycler.adapter = groupIngredientsAdapter

        val apiService = ApiService(ConnectivityInterceptorImpl(this))
        networkDataSource = NetworkDataSourceImpl(apiService)

        networkDataSource?.groupIngredients?.observe(this@GroupIngredientsActivity, Observer {
            groupIngredientsAdapter?.addItems(it)
        })

        val groupId: Int = intent.getIntExtra("GROUP_ID", 0)
        GlobalScope.launch(Dispatchers.Main) {
            networkDataSource?.fetchGroupIngredients(groupId, 30, 1)
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
        groupIngredientsSearchView.setMenuItem(item)


        groupIngredientsSearchView.setOnQueryTextListener(object : MaterialSearchView.OnQueryTextListener {
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
                return false
            }
        })

        groupIngredientsSearchView.setOnSearchViewListener(object : MaterialSearchView.SearchViewListener {
            override fun onSearchViewShown() {
                //Do some magic
            }

            override fun onSearchViewClosed() {
                //Do some magic
            }
        })


        return true
    }
}