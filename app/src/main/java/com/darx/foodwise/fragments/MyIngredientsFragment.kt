package com.darx.foodwise.fragments


import androidx.fragment.app.Fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup


import android.content.Intent
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.LinearLayout
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.darx.foodwise.*
import com.darx.foodwise.adapters.IngredientAdapter
import com.darx.foodwise.database.GroupModel
import com.darx.foodwise.database.GroupViewModel
import com.darx.foodwise.database.IngredientModel
import com.darx.foodwise.database.IngredientViewModel
import kotlinx.android.synthetic.main.fragment_groups.*
import kotlinx.android.synthetic.main.fragment_ingredients.*
import java.io.Serializable


class MyIngredientsFragment(val ingredientViewModel: IngredientViewModel, val groupViewModel: GroupViewModel) : Fragment() {

    private var myIngredientsAdapter: IngredientAdapter? = null
    private var queryString: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_ingredients, container, false)

        // my Ingredients
        myIngredientsAdapter = IngredientAdapter(emptyList(), activity!!.baseContext,
            object : IngredientAdapter.Callback {
                override fun onItemClicked(item: IngredientModel) {
                    ingredientViewModel.deleteOne_(item)
                }
            },
            object : IngredientAdapter.CallbackInfo {
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
                if (l != null) {
                    for (ingredient in l) {
                        ingredient.ok = false
                    }
                    if (l.isEmpty()) {
                        showEmptyFragment()
                        ingredients_fragments_frame.visibility = VISIBLE
                    } else {
                        ingredients_fragments_frame.visibility = GONE
                    }
                }
                myIngredientsAdapter?.addItems(l ?: return)
            }
        })

        return view
    }

    fun searchMyIngredients(query: String) {
        val data = matchMyIngredients(query)
        myIngredientsAdapter?.addItems(data)
        if (data.isEmpty()) {
            showEmptyFragment()
            ingredients_fragments_frame.visibility = VISIBLE
        } else {
            ingredients_fragments_frame.visibility = GONE
        }
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

    private fun showEmptyFragment() {
        var emptyFragment: EmptyFragment? = null
        if (queryString.isEmpty()) {
            emptyFragment = EmptyFragment(
                R.drawable.empty_ingredients,
                getString(R.string.empty_ingredients_message),
                getString(R.string.empty_ingredients_button),
                LinearLayout.VERTICAL,
                View.OnClickListener {
                    (activity as UserIngredientsActivity).changeTab(0)
                }
            )
        } else {
            emptyFragment = EmptyFragment(
                R.drawable.empty_search,
                getString(R.string.empty_search_message),
                getString(R.string.empty_search_button),
                LinearLayout.VERTICAL,
                View.OnClickListener {}
            )
        }
        activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.ingredients_fragments_frame, emptyFragment)?.commit()
    }
}