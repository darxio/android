package com.darx.foodscaner.fragments


import androidx.fragment.app.Fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup


import android.content.Intent
import androidx.core.graphics.drawable.toDrawable
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.darx.foodscaner.*
import com.darx.foodscaner.adapters.IngredientAdapter
import com.darx.foodscaner.database.GroupModel
import com.darx.foodscaner.database.GroupViewModel
import com.darx.foodscaner.database.IngredientModel
import com.darx.foodscaner.database.IngredientViewModel
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.fragment_ingredients.*
import java.io.Serializable


class MyIngredientsFragment(val ingredientViewModel: IngredientViewModel, val groupViewModel: GroupViewModel) : Fragment() {

    private var myIngredientsAdapter: IngredientAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_ingredients, container, false)

        // my Ingredients
        myIngredientsAdapter = IngredientAdapter(emptyList(), this, ingredientViewModel, groupViewModel, object : IngredientAdapter.Callback {
            override fun onItemClicked(item: IngredientModel) {
                val intent = Intent(activity, IngredientActivity::class.java)
                intent.putExtra("INGREDIENT", item as Serializable)
                startActivity(intent)
            }
        })
        val ingredientRecycler = view.findViewById<RecyclerView>(R.id.ingredients_rv)
        ingredientRecycler.adapter = myIngredientsAdapter

        ingredientViewModel.getNotAllowed_().observe(this, object : Observer<List<IngredientModel>> {
            override fun onChanged(l: List<IngredientModel>?) {
                myIngredientsAdapter?.addItems(l ?: return)

//                if (myIngredientsAdapter?.items.isNullOrEmpty()) {
//                    fragmentManager!!.beginTransaction()
//                        .replace(R.id.fragment_empty_container, EmptyFragment("Текст", "Добавить"))
//                        .addToBackStack(null)
//                        .commit()
//                }
            }
        })

        return view
    }

    fun searchMyIngredients(query: String) {
        myIngredientsAdapter?.addItems(matchMyIngredients(query))
    }

    private fun matchMyIngredients(typed: String): List<IngredientModel> {
        val matched: MutableList<IngredientModel> = mutableListOf()

        val data = ingredientViewModel.getNotAllowed_()
        for (ingredient in data.value!!) {
            if (ingredient.name.contains(typed, ignoreCase=true)) {
                matched.add(ingredient)
            }
        }
        return matched
    }
}