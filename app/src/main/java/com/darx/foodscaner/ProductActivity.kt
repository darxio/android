package com.darx.foodscaner

import android.content.Intent
import android.content.res.ColorStateList
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.darx.foodscaner.data.Ingredient
import kotlinx.android.synthetic.main.activity_product.*
import androidx.core.graphics.drawable.toDrawable
import com.darx.foodscaner.data.response.Product
import com.google.android.material.chip.Chip
import kotlinx.android.synthetic.main.product_item.*


class ProductActivity : AppCompatActivity() {

//    private val database = LocalDatabase(applicationContext)
//    var database: LocalDatabase = databaseBuilder(baseContext, LocalDatabase::class.java!!, "database").build()
    private lateinit var product: Product

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product)

        product = intent.extras.get("PRODUCT") as Product
        productName.text = product.name

        val ingredients = listOf(
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


        for (ingredient: Ingredient in ingredients) {
            val chip: Chip = Chip(this)
            chip.text = ingredient.name

            val states = arrayOf(
                intArrayOf(android.R.attr.state_enabled), // enabled
                intArrayOf(-android.R.attr.state_enabled), // disabled
                intArrayOf(-android.R.attr.state_checked), // unchecked
                intArrayOf(android.R.attr.state_pressed)  // pressed
            )
            val colors = intArrayOf(R.color.positiveColor, R.color.positiveColor, R.color.positiveColor, R.color.positiveColor)

            chip.chipBackgroundColor = ColorStateList(states, colors)
            chip.chipStrokeColor = ColorStateList(states, colors)
            chip.chipStrokeWidth = 1F
            chip.chipIcon = R.drawable.ingredient.toDrawable()

            chip.setOnClickListener {
                val intent = Intent(this, IngredientActivity::class.java)
//                intent.putExtra("PRODUCT", item as Serializable)
                startActivity(intent)
            }
            ingredientChipsGroup.addView(chip)
        }

//        productStar.setOnClickListener {
//            addToFavorite(it)
//        }
    }

    private fun addToFavorite(view: View) {
        val database = LocalDatabase(applicationContext)
        val productDao = database.productDao()
        productDao.upsert(product)
    }
}
