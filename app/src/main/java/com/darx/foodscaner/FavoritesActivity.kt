package com.darx.foodscaner

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.darx.foodscaner.adapters.ProductsAdapter
import com.darx.foodscaner.database.ProductModel
import com.darx.foodscaner.database.ProductViewModel
import kotlinx.android.synthetic.main.activity_favorites.*

//import com.darx.foodscaner.data.response.Product

class FavoritesActivity : AppCompatActivity() {

    private var productViewModel: ProductViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorites)

//        this.productViewModel = ViewModelProviders.of(this).get(ProductViewModel::class.java)
//
//        val data = productViewModel?.getFavourites_()
//
//        val productAdapter = ProductsAdapter(data,  productViewModel!!, this@FavoritesActivity!!, object : ProductsAdapter.Callback {
//            override fun onItemClicked(item: ProductModel) {
//                val intent = Intent(this@FavoritesActivity, ProductActivity::class.java)
//                startActivity(intent)
//            }
//        })
//        favoritesProductRecycler.adapter = productAdapter
//
//        val tmp = productViewModel
//        val tmpAdp = productAdapter
//
//        tmp!!.getAll_().observe(this@FavoritesActivity, object :
//            Observer<List<ProductModel>> {
//            override fun onChanged(l: List<ProductModel>?) {
//                tmpAdp!!.addItems(l ?: return)
//            }
//        })
    }
}
