package com.darx.foodscaner.fragments


import androidx.fragment.app.Fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup


import android.content.Intent
import android.content.res.ColorStateList
import androidx.core.graphics.drawable.toDrawable
import androidx.recyclerview.widget.RecyclerView
import com.darx.foodscaner.*
import com.darx.foodscaner.adapters.GroupAdapter
import com.darx.foodscaner.data.Ingredient
import com.darx.foodscaner.data.response.Group
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.activity_product.*


class IngredientsFragment(private val isSearch: Boolean) : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_groups, container, false)

//        var ingredients: List<Ingredient>? = null
//        if (isSearch) {
//            ingredients = getAllIngredients("")
//        } else {
//            val groupSearch = view.findViewById<TextInputLayout>(R.id.groupSearch)
//            groupSearch?.visibility = View.GONE
//            ingredients = getUsersIngredients()
//        }
//
//        for (ingredient: Ingredient in ingredients) {
//            val chip: Chip = Chip(this@IngredientsFragment.activity)
//            chip.text = ingredient.name
//
//            chip.chipStrokeWidth = 1F
//            chip.chipIcon = R.drawable.ingredient.toDrawable()
//
//            chip.setOnClickListener {
//                val intent = Intent(this@IngredientsFragment.activity, IngredientActivity::class.java)
////                intent.putExtra("PRODUCT", item as Serializable)
//                startActivity(intent)
//            }
//
//            val ingredientsChipsGroup = view.findViewById<ChipGroup>(R.id.ingredientsChipsGroup)
//            ingredientsChipsGroup?.addView(chip)
//        }

        return view
    }

    private fun getUsersIngredients(): List<Ingredient> {
        return listOf(
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
    }

    private fun getAllIngredients(name: String): List<Ingredient> {
        return listOf(
            Ingredient("Сахар очень вк"),
            Ingredient("Соль"),
            Ingredient("Яблокоааа"),
            Ingredient("Груша"),
            Ingredient("Яб"),
            Ingredient("Грушалвалфыва ваыв"),
            Ingredient("Ябвафы"),
            Ingredient("Ябвыфаваыав"),
            Ingredient("Ябав"),
            Ingredient("Сахар очень вк"),
            Ingredient("Соль"),
            Ingredient("Яблокоааа"),
            Ingredient("Груша"),
            Ingredient("Яб"),
            Ingredient("Грушалвалфыва ваыв"),
            Ingredient("Ябвафы"),
            Ingredient("Ябвыфаваыав"),
            Ingredient("Ябав"),
            Ingredient("Сахар очень вк"),
            Ingredient("Соль"),
            Ingredient("Яблокоааа"),
            Ingredient("Груша"),
            Ingredient("Яб"),
            Ingredient("Грушалвалфыва ваыв"),
            Ingredient("Ябвафы"),
            Ingredient("Ябвыфаваыав"),
            Ingredient("Ябав"),
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
    }
}
