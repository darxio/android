package com.darx.foodwise

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.darx.foodwise.adapters.ProductsAdapter
import com.miguelcatalan.materialsearchview.MaterialSearchView
import kotlinx.android.synthetic.main.activity_favorites.*
import java.io.Serializable
import android.app.Activity
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.widget.NestedScrollView
import com.darx.foodwise.fragments.EmptyFragment
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.view.View.*
import com.darx.foodwise.fragments.RecentlyScannedFragment
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import com.darx.foodwise.database.*


class FavoritesActivity : AppCompatActivity() {

    private var productViewModel: ProductViewModel? = null
    private var productsAdapter: ProductsAdapter? = null

    private var groupsDB: List<GroupModel> = listOf()
    private var ingredientsDB: List<IngredientModel> = listOf()

    private var products: List<ProductModel> = listOf()

    private var queryString: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorites)

        setSupportActionBar(favourites_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        this.productViewModel = ViewModelProviders.of(this).get(ProductViewModel::class.java)
        val ingredientViewModel = ViewModelProviders.of(this).get(IngredientViewModel::class.java)
        val groupViewModel = ViewModelProviders.of(this).get(GroupViewModel::class.java)

        productsAdapter = ProductsAdapter(emptyList(),  productViewModel!!, this@FavoritesActivity, false, object : ProductsAdapter.Callback {
            override fun onItemClicked(item: ProductModel) {
                val intent = Intent(this@FavoritesActivity, ProductActivity::class.java)
                intent.putExtra("PRODUCT", item as Serializable)
                startActivity(intent)
            }
        })

        favourites_rv.adapter = productsAdapter

        groupViewModel.getAll_().observe(this, Observer {
            groupsDB = it
            filter()
            productsAdapter?.addItems(products)
        })
        ingredientViewModel.getAll_().observe(this, Observer<List<IngredientModel>> {
            ingredientsDB = it
            filter()
            productsAdapter?.addItems(products)
        })

        productViewModel!!.getFavourites_().observe(this@FavoritesActivity, object : Observer<List<ProductModel>> {
            override fun onChanged(l: List<ProductModel>?) {
                products = l?: listOf()
                filter()
                if (products.isEmpty()) {
                    showEmptyFragment()
                    favorites_fragments_frame.visibility = VISIBLE
                } else {
                    favorites_fragments_frame.visibility = GONE
                }
                productsAdapter?.addItems(products)
            }
        })
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
        favourites_search_view.setMenuItem(item)

        favourites_search_view.setOnQueryTextListener(object : MaterialSearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                queryString = query
                val data = matchFavouritesGroups(query)
                productsAdapter?.addItems(data)
                if (data.isEmpty()) {
                    showEmptyFragment()
                    favorites_fragments_frame.visibility = VISIBLE
                } else {
                    favorites_fragments_frame.visibility = GONE
                }
                val inputMethodManger = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManger.hideSoftInputFromWindow(favourites_rv.windowToken, 0)
                return true
            }
            override fun onQueryTextChange(query: String): Boolean {
                queryString = query
                val data = matchFavouritesGroups(query)
                productsAdapter?.addItems(data)
                if (data.isEmpty()) {
                    showEmptyFragment()
                    favorites_fragments_frame.visibility = VISIBLE
                } else {
                    favorites_fragments_frame.visibility = GONE
                }
                return false
            }
        })

        favourites_search_view.setOnSearchViewListener(object : MaterialSearchView.SearchViewListener {
            override fun onSearchViewShown() {}
            override fun onSearchViewClosed() {}
        })

        favourites_nead_scroll.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, _, _, _, _ ->
            val inputMethodManger = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManger.hideSoftInputFromWindow(v.windowToken, 0)
        })

        return true
    }

    fun matchFavouritesGroups(typed: String): List<ProductModel> {
        val matched: MutableList<ProductModel> = mutableListOf()

        val data = productViewModel?.getFavourites_()!!
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
                R.drawable.empty_favourites,
                getString(R.string.empty_favourites_message),
                getString(R.string.empty_favourites_button),
                LinearLayout.VERTICAL,
                View.OnClickListener {
                    val resultInt = Intent()
                    resultInt.putExtra("Result", "Done")
                    setResult(Activity.RESULT_OK, resultInt)
                    super.onBackPressed()
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
        supportFragmentManager.beginTransaction().replace(R.id.favorites_fragments_frame, emptyFragment).commit()
    }

    private fun filter() {
        for (product in products) {
            product.ok = 0
        }

        for (product in products) {
            if (product.ingredients != null) {
                for (ingredient in product.ingredients!!) {
                    var isOk: Boolean = true
                    if (ingredient.groups != null) {
                        for (g in ingredient.groups) {
                            for (group in groupsDB) {
                                if (group.id == g) {
                                    isOk = false
                                }
                            }
                        }
                    }
                    for (ingredientDB in ingredientsDB) {
                        if (ingredientDB.id == ingredient.id) {
                            isOk = ingredientDB.allowed ?: isOk
                        }
                    }
                    product.ok += if (!isOk) 1 else 0
                }
            }
        }
    }
}
