package com.darx.foodscaner

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.darx.foodscaner.adapters.GroupAdapter
import com.darx.foodscaner.adapters.ProductAdapter
import com.darx.foodscaner.data.response.Group
import com.darx.foodscaner.data.response.Product
import com.darx.foodscaner.database.AppDatabase
import kotlinx.android.synthetic.main.activity_groups.*
import java.io.Serializable

class FavoritesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorites)

        val database = AppDatabase(applicationContext)
//        val productDao = database.productDAO()
//        val fovoriteProducts = productDao.getProducts()

//        val productAdapter = ProductAdapter(fovoriteProducts as List<Product>, object : ProductAdapter.Callback {
//            override fun onItemClicked(item: Product) {
//                val intent = Intent(this@FavoritesActivity, ProductActivity::class.java)
//                intent.putExtra("PRODUCT", item as Serializable)
//                startActivity(intent)
//            }
//        })
        groupRecycler.adapter = productAdapter
    }
}
