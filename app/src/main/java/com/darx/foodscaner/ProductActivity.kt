package com.darx.foodscaner

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.darx.foodscaner.adapters.IngredientAdapter
import com.darx.foodscaner.data.Ingredient
import com.darx.foodscaner.services.LocalDatabase
import kotlinx.android.synthetic.main.activity_product.*
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import com.darx.foodscaner.data.response.Product


class ProductActivity : AppCompatActivity() {

//    private val database = LocalDatabase(this)
    private lateinit var product: Product

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product)

        product = intent.extras.get("PRODUCT") as Product
        productName.text = product.name


        val items = listOf(
            Ingredient("Сахар очень вк"),
            Ingredient("Соль"),
            Ingredient("Яблокоааа"),
            Ingredient("Груша"),
            Ingredient("Яб"),
            Ingredient("Грушалвалфыва ваыв"),
            Ingredient("Ябвафы"),
            Ingredient("Ябвыфаваыав"),
            Ingredient("Ябав")
        )

        ingredientRecycler.adapter = IngredientAdapter(items, object : IngredientAdapter.Callback {
            override fun onItemClicked(item: Ingredient) {
                val intent = Intent(this@ProductActivity, IngredientActivity::class.java)
                startActivity(intent)
            }
        })
    }

//    private fun addToFavorite(view: View) {
//        val productDao = database.productDao()
//        productDao.upsert(product)
//    }
}
