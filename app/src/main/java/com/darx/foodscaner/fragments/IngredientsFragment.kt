package com.darx.foodscaner.fragments


import android.content.Intent
import androidx.fragment.app.Fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.darx.foodscaner.*
import com.darx.foodscaner.adapters.IngredientAdapter
import com.darx.foodscaner.database.GroupViewModel
import com.darx.foodscaner.database.IngredientModel
import com.darx.foodscaner.database.IngredientViewModel
import com.darx.foodscaner.services.ApiService
import com.darx.foodscaner.services.ConnectivityInterceptorImpl
import com.darx.foodscaner.services.NetworkDataSourceImpl
import kotlinx.android.synthetic.main.fragment_ingredients.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.Serializable


class IngredientsFragment(val ingredientViewModel: IngredientViewModel, val groupViewModel: GroupViewModel) : Fragment() {

    private var networkDataSource: NetworkDataSourceImpl? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_ingredients, container, false)

        val apiService = ApiService(ConnectivityInterceptorImpl(this.context!!))
        networkDataSource = NetworkDataSourceImpl(apiService, this.context!!)

        // all Ingredients
        val allIngredientsAdapter = IngredientAdapter(emptyList(), this, ingredientViewModel, groupViewModel, object : IngredientAdapter.Callback {
            override fun onItemClicked(item: IngredientModel) {
                val intent = Intent(activity, IngredientActivity::class.java)
                intent.putExtra("INGREDIENT", item as Serializable)
                startActivity(intent)
            }
        })
        val ingredientRecycler = view.findViewById<RecyclerView>(R.id.ingredients_rv)
        ingredientRecycler.adapter = allIngredientsAdapter

        networkDataSource?.ingredients?.observe(this, Observer {
            allIngredientsAdapter.addItems(it)
        })
        networkDataSource?.ingredientSearch?.observe(this, Observer {
            allIngredientsAdapter.addItems(it)
        })

        GlobalScope.launch(Dispatchers.Main) {
            networkDataSource?.fetchIngredients(30, 0)
        }

        return view
    }
}