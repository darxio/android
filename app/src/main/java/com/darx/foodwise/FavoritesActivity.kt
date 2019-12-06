package com.darx.foodwise

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.darx.foodwise.adapters.ProductsAdapter
import com.darx.foodwise.database.ProductModel
import com.darx.foodwise.database.ProductViewModel
import com.miguelcatalan.materialsearchview.MaterialSearchView
import kotlinx.android.synthetic.main.activity_favorites.*
import java.io.Serializable
import android.app.Activity
import android.view.*
import android.view.View.VISIBLE
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.widget.NestedScrollView
import com.darx.foodwise.fragments.EmptyFragment


class FavoritesActivity : AppCompatActivity() {

    private var productViewModel: ProductViewModel? = null
    private var productsAdapter: ProductsAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorites)

        setSupportActionBar(favourites_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        supportActionBar?.setDisplayShowHomeEnabled(true);

        this.productViewModel = ViewModelProviders.of(this).get(ProductViewModel::class.java)

        productsAdapter = ProductsAdapter(emptyList(),  productViewModel!!, this@FavoritesActivity, this, false, object : ProductsAdapter.Callback {
            override fun onItemClicked(item: ProductModel) {
                val intent = Intent(this@FavoritesActivity, ProductActivity::class.java)
                intent.putExtra("PRODUCT", item as Serializable)
                startActivity(intent)
            }
        })

        favourites_rv.adapter = productsAdapter

        productViewModel!!.getFavourites_().observe(this@FavoritesActivity, object : Observer<List<ProductModel>> {
            override fun onChanged(l: List<ProductModel>?) {
                if (l?.size == 0) {
                    favorites_fragments_frame.visibility = VISIBLE
                    val emptyFragment = EmptyFragment(
                        R.drawable.empty_favourites,
                        "У Вас пока нет любимых продуктов!",
                        "Добавить",
                        LinearLayout.VERTICAL,
                        View.OnClickListener {
                            Toast.makeText(this@FavoritesActivity, "yy", Toast.LENGTH_LONG)
                        }
                    )
                    supportFragmentManager.beginTransaction().replace(R.id.favorites_fragments_frame, emptyFragment).commit()
                }
                productsAdapter!!.addItems(l ?: return)
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
                productsAdapter?.addItems(matchFavouritesGroups(query))
                val inputMethodManger = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManger.hideSoftInputFromWindow(favourites_rv.windowToken, 0)
                return true
            }
            override fun onQueryTextChange(newText: String): Boolean {
                productsAdapter?.addItems(matchFavouritesGroups(newText))
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
}
