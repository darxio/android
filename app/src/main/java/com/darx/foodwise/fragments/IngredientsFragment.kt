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
import com.darx.foodwise.database.GroupViewModel
import com.darx.foodwise.database.IngredientModel
import com.darx.foodwise.database.IngredientViewModel
import com.darx.foodwise.services.ApiService
import com.darx.foodwise.services.ConnectivityInterceptorImpl
import com.darx.foodwise.services.NetworkDataSource
import com.darx.foodwise.services.NetworkDataSourceImpl
import kotlinx.android.synthetic.main.fragment_groups.*
import kotlinx.android.synthetic.main.fragment_ingredients.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.Serializable


class IngredientsFragment(val ingredientViewModel: IngredientViewModel, val groupViewModel: GroupViewModel) : Fragment() {

    private var networkDataSource: NetworkDataSourceImpl? = null

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
                override fun onItemClicked(item: IngredientModel) {}
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
            allIngredientsAdapter.addItems(it)
        })
        networkDataSource?.ingredientSearch?.observe(this, Observer {
            allIngredientsAdapter.addItems(it)
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
}