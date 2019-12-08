package com.darx.foodwise.fragments


import android.content.Intent
import androidx.fragment.app.Fragment


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.darx.foodwise.*
import com.darx.foodwise.adapters.IngredientAdapter
import com.darx.foodwise.database.GroupModel
import com.darx.foodwise.database.GroupViewModel
import com.darx.foodwise.database.IngredientModel
import com.darx.foodwise.database.IngredientViewModel
import com.darx.foodwise.services.ApiService
import com.darx.foodwise.services.ConnectivityInterceptorImpl
import com.darx.foodwise.services.NetworkDataSource
import com.darx.foodwise.services.NetworkDataSourceImpl
import kotlinx.android.synthetic.main.fragment_groups.*
import kotlinx.android.synthetic.main.fragment_ingredients.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.Serializable


class IngredientsFragment(val ingredientViewModel: IngredientViewModel, val groupViewModel: GroupViewModel) : Fragment() {

    private var networkDataSource: NetworkDataSourceImpl? = null

    private var groupsDB: List<GroupModel> = listOf()

    private var ingredients: List<IngredientModel> = listOf()
    private var ingredientsDB: List<IngredientModel> = listOf()

    private var queryString: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_ingredients, container, false)

        val apiService = ApiService(ConnectivityInterceptorImpl(this.context!!))
        networkDataSource = NetworkDataSourceImpl(apiService, this.context!!)

        // all Ingredients
        val allIngredientsAdapter = IngredientAdapter(emptyList(), activity!!.baseContext,
            object : IngredientAdapter.Callback {
                override fun onItemClicked(item: IngredientModel) {
                    if (!item.ok) {
                        if (item.groupMached) {
                            item.allowed = true
                            ingredientViewModel.add_(item)
                        } else {
                            ingredientViewModel.deleteOne_(item)
                        }
                    } else {
                        if (item.groupMached) {
                            ingredientViewModel.deleteOne_(item)
                        } else {
                            item.allowed = false
                            ingredientViewModel.add_(item)
                        }
                    }
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
        ingredientRecycler.adapter = allIngredientsAdapter

        networkDataSource?.ingredients?.observe(this, Observer {
            ingredients = it
            filter()
            if (ingredients.isEmpty()){
                showEmptyFragment()
                ingredients_fragments_frame.visibility = View.VISIBLE
            } else {
                ingredients_fragments_frame.visibility = View.GONE
            }
            allIngredientsAdapter.addItems(ingredients)
        })
        networkDataSource?.ingredientSearch?.observe(this, Observer {
            ingredients = it
            filter()
            if (ingredients.isEmpty()){
                showEmptyFragment()
                ingredients_fragments_frame.visibility = View.VISIBLE
            } else {
                ingredients_fragments_frame.visibility = View.GONE
            }
            allIngredientsAdapter.addItems(ingredients)
        })

        groupViewModel.getAll_().observe(this, Observer {
            groupsDB = it
            filter()
            allIngredientsAdapter.addItems(ingredients)
        })
        ingredientViewModel.getAll_().observe(this, Observer<List<IngredientModel>> {
            ingredientsDB = it
            filter()
            allIngredientsAdapter.addItems(ingredients)
        })

        GlobalScope.launch(Dispatchers.Main) {
            networkDataSource?.fetchIngredients(20, 0)
        }
        return view
    }

    fun searchIngredients(query: String) {
        if (query.isEmpty()) {
            GlobalScope.launch(Dispatchers.Main) {
                networkDataSource?.fetchIngredients(20, 0, object : NetworkDataSource.Callback {
                    override fun onNoConnectivityException() {
                        Log.e("HTTP", "Wrong answer.")
                        ingredients_fragments_frame.visibility = View.VISIBLE
                        showEmptyFragment()
                    }
                    override fun onHttpException() {}
                    override fun onTimeoutException() {}
                    override fun onException() {}

                })
            }
        } else {
            GlobalScope.launch(Dispatchers.Main) {
                networkDataSource?.fetchIngredientSearch(query, 20, 0, object : NetworkDataSource.Callback {
                    override fun onNoConnectivityException() {
                        Log.e("HTTP", "Wrong answer.")
                        ingredients_fragments_frame.visibility = View.VISIBLE
                        showEmptyFragment()
                    }
                    override fun onHttpException() {}
                    override fun onTimeoutException() {}
                    override fun onException() {}

                })
            }
        }
    }

    private fun showEmptyFragment() {
        var emptyFragment: EmptyFragment? = null
        if (queryString.isEmpty()) {
            emptyFragment = EmptyFragment(
                R.drawable.empty_no_internet,
                getString(R.string.empty_internet_message),
                getString(R.string.empty_internet_button),
                LinearLayout.VERTICAL,
                View.OnClickListener {}
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

    private fun filter() {
        for (ingredient in ingredients) {
            ingredient.groupMached = false
            ingredient.ok = true
        }

        for (group in groupsDB) {
            group.isInBase = true
            for (ingredient in ingredients) {
                for (g in ingredient.groups) {
                    if (group.id == g) {
                        ingredient.groupMached = true
                        ingredient.ok = false
                    }
                }
            }
        }

        for (ingredient in ingredients) {
            for (i in ingredientsDB) {
                if (ingredient.id == i.id) {
                    ingredient.ok = i.allowed!!
                }
            }
        }
    }
}