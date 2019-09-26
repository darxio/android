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
            Ingredient("Сахар"),
            Ingredient("Соль"),
            Ingredient("Яблоко"),
            Ingredient("Груша"),
            Ingredient("Яблоко"),
            Ingredient("Груша")
        )

        ingredientRecycler.adapter = IngredientAdapter(items, object : IngredientAdapter.Callback {
            override fun onItemClicked(item: Ingredient) {}
        })
    }
}
