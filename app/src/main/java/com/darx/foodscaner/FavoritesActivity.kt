package com.darx.foodscaner

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.darx.foodscaner.adapters.GroupAdapter
import com.darx.foodscaner.adapters.ProductAdapter
import com.darx.foodscaner.data.response.Group
import com.darx.foodscaner.data.response.Product
import com.darx.foodscaner.services.LocalDatabase
import kotlinx.android.synthetic.main.activity_favorites.*
import kotlinx.android.synthetic.main.activity_groups.*
import java.io.Serializable

class FavoritesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorites)

        val database = LocalDatabase(applicationContext)
        val productDao = database.productDao()
        val fovoriteProducts = listOf<Product>(
            Product(1, "Сникерс", 13443, "Вкусно"),
            Product(1, "Сникерс", 13443, "Вкусно"),
            Product(1, "Сникерс", 13443, "Вкусно"),
            Product(1, "Сникерс", 13443, "Вкусно"),
            Product(1, "Сникерс", 13443, "Вкусно"),
            Product(1, "Сникерс", 13443, "Вкусно"),
            Product(1, "Сникерс", 13443, "Вкусно"),
            Product(1, "Сникерс", 13443, "Вкусно")
        )

//                = productDao.getProducts()

        val productAdapter = ProductAdapter(fovoriteProducts as List<Product>, object : ProductAdapter.Callback {
            override fun onItemClicked(item: Product) {
                val intent = Intent(this@FavoritesActivity, ProductActivity::class.java)
                intent.putExtra("PRODUCT", item as Serializable)
                startActivity(intent)
            }
        })
        productRecycler.adapter = productAdapter
    }
}
