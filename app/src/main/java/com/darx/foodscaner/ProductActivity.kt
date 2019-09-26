package com.darx.foodscaner

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.darx.foodscaner.components.IngredientAdapter
import com.darx.foodscaner.components.ProductAdapter
import com.darx.foodscaner.models.Ingredient
import com.darx.foodscaner.models.Product
import kotlinx.android.synthetic.main.activity_product.*

class ProductActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product)

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
}
