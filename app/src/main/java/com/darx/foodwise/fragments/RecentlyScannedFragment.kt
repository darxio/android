package com.darx.foodwise.fragments


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.darx.foodwise.ProductActivity
import com.darx.foodwise.adapters.ProductsAdapter
import com.darx.foodwise.services.ApiService
import com.darx.foodwise.services.ConnectivityInterceptorImpl
import com.darx.foodwise.services.NetworkDataSourceImpl
import kotlinx.android.synthetic.main.fragment_recently_scanned.*
import kotlinx.android.synthetic.main.fragment_recently_scanned.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.Serializable
import android.view.*
import com.arlib.floatingsearchview.FloatingSearchView
import android.app.Activity
import android.view.inputmethod.InputMethodManager
import android.speech.RecognizerIntent
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.widget.NestedScrollView
import com.darx.foodwise.MainActivity
import com.darx.foodwise.R
import com.darx.foodwise.database.*
import java.lang.Exception
import java.util.*




class RecentlyScannedFragment : Fragment() {

    private val REQUEST_CODE_SPEECH_INPUT = 100

    private var productViewModel: ProductViewModel? = null
    private var networkDataSource: NetworkDataSourceImpl? = null
    private var scannedProductsAdapter: ProductsAdapter? = null
    private var searchedProductsAdapter: ProductsAdapter? = null

    private var groupsDB: List<GroupModel> = listOf()
    private var ingredientsDB: List<IngredientModel> = listOf()

    private var scanedProducts: List<ProductModel> = listOf()
    private var staredProducts: List<ProductModel> = listOf()
    private var products: List<ProductModel> = listOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(com.darx.foodwise.R.layout.fragment_recently_scanned, container, false)

        val apiService = ApiService(ConnectivityInterceptorImpl(this.context!!))
        networkDataSource = NetworkDataSourceImpl(apiService, context!!)

        productViewModel = ViewModelProviders.of(this).get(ProductViewModel::class.java)
        val ingredientViewModel = ViewModelProviders.of(this).get(IngredientViewModel::class.java)
        val groupViewModel = ViewModelProviders.of(this).get(GroupViewModel::class.java)

        // observers
        groupViewModel.getAll_().observe(this, Observer {
            groupsDB = it
            filter()
            filterScanned()
            searchedProductsAdapter?.addItems(products)
            scannedProductsAdapter?.addItems(scanedProducts)
        })
        ingredientViewModel.getAll_().observe(this, Observer<List<IngredientModel>> {
            ingredientsDB = it
            filter()
            filterScanned()
            searchedProductsAdapter?.addItems(products)
            scannedProductsAdapter?.addItems(scanedProducts)
        })

        // scaned products
        scannedProductsAdapter = ProductsAdapter(emptyList(), productViewModel!!, this.context!!, true, object : ProductsAdapter.Callback {
            override fun onItemClicked(item: ProductModel) {
                val intent = Intent(this@RecentlyScannedFragment.activity, ProductActivity::class.java)
                intent.putExtra("PRODUCT", item as Serializable)
                startActivity(intent)
            }
        })
        view.history_rv.adapter = scannedProductsAdapter

        productViewModel?.getScanned_()?.observe(this@RecentlyScannedFragment,
            Observer<List<ProductModel>> { l ->
                scanedProducts = l
                filterScanned()
                if (l?.size == 0 && searchedProductsAdapter?.itemCount == 0) {
                    showEmptyFragment(view)
                } else {
                    view.rs_fragments_frame.visibility = View.GONE
                }
                scannedProductsAdapter?.addItems(matchMyProducts(rs_search_view?.query.toString()))
            })

        productViewModel?.getFavourites_()?.observe(this@RecentlyScannedFragment,
            Observer<List<ProductModel>> { l ->
                staredProducts = l
                filter()
                if (view.rs_search_view.query.isNotEmpty()) {
                    searchedProductsAdapter?.addItems(products)
                }
            })


        // search products
        searchedProductsAdapter = ProductsAdapter(emptyList(), productViewModel!!, this.context!!, false, object : ProductsAdapter.Callback {
            override fun onItemClicked(item: ProductModel) {
                val intent = Intent(this@RecentlyScannedFragment.activity, ProductActivity::class.java)
                intent.putExtra("PRODUCT", item as Serializable)
                startActivity(intent)
            }
        })
        view.all_products_rv.adapter = searchedProductsAdapter

        networkDataSource?.productSearch?.observe(this@RecentlyScannedFragment, Observer {
            products = it
            filter()
            if (it.isEmpty() && scannedProductsAdapter?.itemCount == 0) {
                showEmptyFragment(view)
            } else {
                view.rs_fragments_frame.visibility = View.GONE
            }

            if (view.rs_search_view.query.isNotEmpty()) {
                searchedProductsAdapter?.addItems(products)
            }
        })

        // searching
        view.rs_search_view.setOnQueryChangeListener(FloatingSearchView.OnQueryChangeListener { oldQuery, newQuery ->
            scannedProductsAdapter?.addItems(matchMyProducts(newQuery))
            if (newQuery.isEmpty()) {
                searchedProductsAdapter?.addItems(listOf())
                if (scannedProductsAdapter?.itemCount == 0) {
                    showEmptyFragment(view)
                }
            } else {
                GlobalScope.launch(Dispatchers.Main) {
                    networkDataSource?.fetchProductSearch(newQuery, 15, 0)
                }
            }
        })

        view.rs_search_view.setOnMenuItemClickListener { item ->
            when (item?.itemId) {
                R.id.voice_search -> voiceGoogle()
            }
        }

        view.rs_need_scroll.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, _, _, _, _ ->
            val inputMethodManger = activity?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManger.hideSoftInputFromWindow(v.windowToken, 0)
        })

        return view
    }

    private fun showEmptyFragment(view: View) {
        var emptyFragment: EmptyFragment? = null
        if (view.rs_search_view.query.isEmpty()) {
            emptyFragment = EmptyFragment(
                R.drawable.empty_history,
                getString(R.string.empty_rs_scanned_message),
                getString(R.string.empty_rs_scanned_button),
                LinearLayout.VERTICAL,
                View.OnClickListener {
                    (activity as MainActivity).chooseFragment(1)
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
        view.rs_fragments_frame.visibility = View.VISIBLE
        activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.rs_fragments_frame, emptyFragment)?.commit()
    }

    private fun voiceGoogle() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.speach_prompt))

        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT)
        } catch (e: Exception) {
            Toast.makeText(this.context, e.message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode) {
            REQUEST_CODE_SPEECH_INPUT -> {
                if (resultCode == Activity.RESULT_OK && null != data) {
                    val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)[0].toString()
                    rs_search_view.setSearchText(result)
                    GlobalScope.launch(Dispatchers.Main) {
                        networkDataSource?.fetchProductSearch(result, 15, 0)
                    }
                }
            }
        }
    }

    private fun matchMyProducts(typed: String): List<ProductModel> {
        val matched: MutableList<ProductModel> = mutableListOf()

        val data = productViewModel?.getScanned_()!! //?.observe(this@UserIngredientsActivity, object : Observer<List<IngredientModel>>
        for (product in data.value!!) {
            if (product.name.contains(typed, ignoreCase=true)) {
                matched.add(product)
            }
        }
        return matched
    }

    private fun filter() {
        for (product in products) {
            product.ok = 0
        }

        for (product in products) {
            if (product.ingredients != null) {
                for (ingredient in product.ingredients!!) {
                    product.ok += preorder(ingredient)
                }
            }
            for (pr in staredProducts) {
                if (product.barcode == pr.barcode) {
                    product.starred = pr.starred
                }
            }
        }
    }

    private fun filterScanned() {
        for (product in scanedProducts) {
            product.ok = 0
        }

        for (product in scanedProducts) {
            if (product.ingredients != null) {
                for (ingredient in product.ingredients!!) {
                    product.ok += preorder(ingredient)
                }
            }
        }
    }

    private fun preorder(ingredient: IngredientExtended): Int {
        var count: Int = 0
        if (ingredient.ingredients != null) {
            for (ingr in ingredient.ingredients!!) {
                count += preorder(ingr)
            }
        }

        var isOk: Boolean = true
        if (ingredient.groups != null) {
            for (g in ingredient.groups) {
                for (group in groupsDB) {
                    if (group.id == g) {
                        isOk = false
                    }
                }
            }
        }
        for (ingredientDB in ingredientsDB) {
            if (ingredientDB.id == ingredient.id) {
                isOk = ingredientDB.allowed ?: isOk
            }
        }
        return count + if (!isOk) 1 else 0
    }
}
