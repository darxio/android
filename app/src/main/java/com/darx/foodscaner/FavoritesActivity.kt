package com.darx.foodscaner

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
//import com.darx.foodscaner.data.response.Product

class FavoritesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorites)

//        val database = AppDatabase(applicationContext)
//        val productDao = database.productDAO()
//        val fovoriteProducts = productDao.getProducts()

//        val productAdapter = ProductAdapter(fovoriteProducts as List<Product>, object : ProductAdapter.Callback {
//            override fun onItemClicked(item: Product) {
//                val intent = Intent(this@FavoritesActivity, ProductActivity::class.java)
//                intent.putExtra("PRODUCT", item as Serializable)
//                startActivity(intent)
//            }
//        })
//        groupRecycler.adapter = productAdapter
    }
}
