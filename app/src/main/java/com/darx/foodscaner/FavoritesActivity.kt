package com.darx.foodscaner

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.graphics.drawable.toDrawable
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.darx.foodscaner.adapters.ProductsAdapter
import com.darx.foodscaner.database.ProductModel
import com.darx.foodscaner.database.ProductViewModel
import com.miguelcatalan.materialsearchview.MaterialSearchView
import kotlinx.android.synthetic.main.activity_favorites.*
import kotlinx.android.synthetic.main.activity_groups.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.Serializable
import android.R.attr.data
import android.app.Activity
import android.text.TextUtils
import android.speech.RecognizerIntent
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.view.MotionEvent.*
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.view.*
import android.view.inputmethod.InputMethodManager
import kotlinx.android.synthetic.main.fragment_add_product.view.*


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
                inputMethodManger.hideSoftInputFromWindow(favourites_rv.getWindowToken(), 0)
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

        // hide keyboard
        //        favourites_rv.setOnTouchListener { view: View, motionEvent: MotionEvent ->
        //            val inputMethodManger = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //            inputMethodManger.hideSoftInputFromWindow(favourites_rv.getWindowToken(), 0)
        //        }

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
