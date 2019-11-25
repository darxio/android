package com.darx.foodscaner

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.darx.foodscaner.adapters.ProductsAdapter
import com.darx.foodscaner.database.ProductModel
import com.darx.foodscaner.database.ProductViewModel
import kotlinx.android.synthetic.main.activity_favorites.*
import java.io.Serializable

//import com.darx.foodscaner.data.response.Product

class FavoritesActivity : AppCompatActivity() {

    private var productViewModel: ProductViewModel? = null
    private var productsAdapter: ProductsAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorites)

//        setSupportActionBar(favoritesToolbar)
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
}
