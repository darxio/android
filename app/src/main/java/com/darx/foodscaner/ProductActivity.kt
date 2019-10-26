package com.darx.foodscaner

import android.content.Intent
import android.content.res.ColorStateList
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.darx.foodscaner.models.Ingredient
import kotlinx.android.synthetic.main.fragment_product_info.*
import androidx.core.graphics.drawable.toDrawable
import com.darx.foodscaner.database.*
import com.google.android.material.chip.Chip
import kotlinx.android.synthetic.main.product_item.*


class ProductActivity : AppCompatActivity() {
    private var db: AppDatabase? = null
    private var productsDAO: ProductsDAO? = null
    private lateinit var productToShow: ScannedProductModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_product_info)

        productToShow = intent.extras.get("PRODUCT") as ScannedProductModel
        product_name.text = productToShow.name
        product_manufacturer.text = productToShow.manufacturer
        product_description.text = productToShow.description

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
            ingredient_chips.addView(chip)
        }
    }
}
