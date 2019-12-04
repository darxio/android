package com.darx.foodwise.fragments


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.darx.foodwise.ProductActivity
import com.darx.foodwise.adapters.ProductsAdapter
import com.darx.foodwise.database.ProductModel
import com.darx.foodwise.database.ProductViewModel
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
import android.widget.Toast
import androidx.core.widget.NestedScrollView
import com.darx.foodwise.R
import java.lang.Exception
import java.util.*




class RecentlyScannedFragment : Fragment() {

    private val REQUEST_CODE_SPEECH_INPUT = 100

    private var productViewModel: ProductViewModel? = null
    private var networkDataSource: NetworkDataSourceImpl? = null
    private var scannedProductsAdapter: ProductsAdapter? = null
    private var searchedProductsAdapter: ProductsAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(com.darx.foodwise.R.layout.fragment_recently_scanned, container, false)

        val apiService = ApiService(ConnectivityInterceptorImpl(this.context!!))
        networkDataSource = NetworkDataSourceImpl(apiService, context!!)

        productViewModel = ViewModelProviders.of(this).get(ProductViewModel::class.java)

        // scaned products
        scannedProductsAdapter = ProductsAdapter(emptyList(), productViewModel!!, this.context!!, this, true, object : ProductsAdapter.Callback {
            override fun onItemClicked(item: ProductModel) {
                val intent = Intent(this@RecentlyScannedFragment.activity, ProductActivity::class.java)
                intent.putExtra("PRODUCT", item as Serializable)
                startActivity(intent)
            }
        })
        view.history_rv.adapter = scannedProductsAdapter

        productViewModel?.getScanned_()?.observe(this@RecentlyScannedFragment, object : Observer<List<ProductModel>> {
            override fun onChanged(l: List<ProductModel>?) {
                scannedProductsAdapter?.addItems(matchMyProducts(rs_search_view?.query.toString()))
            }
        })


        // search products
        searchedProductsAdapter = ProductsAdapter(emptyList(), productViewModel!!, this.context!!, this, false, object : ProductsAdapter.Callback {
            override fun onItemClicked(item: ProductModel) {
                val intent = Intent(this@RecentlyScannedFragment.activity, ProductActivity::class.java)
                intent.putExtra("PRODUCT", item as Serializable)
                startActivity(intent)
            }
        })
        view.all_products_rv.adapter = searchedProductsAdapter

        networkDataSource?.productSearch?.observe(this@RecentlyScannedFragment, Observer {
            if (view.rs_search_view.query != "") searchedProductsAdapter?.addItems(it)
        })

        // searching
        view.rs_search_view.setOnQueryChangeListener(FloatingSearchView.OnQueryChangeListener { oldQuery, newQuery ->
            if (newQuery.isEmpty()) {
                searchedProductsAdapter?.addItems(listOf())
            } else {
                GlobalScope.launch(Dispatchers.Main) {
                    networkDataSource?.fetchProductSearch(newQuery, 15, 0)
                }
            }
            scannedProductsAdapter?.addItems(matchMyProducts(newQuery))
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

    fun matchMyProducts(typed: String): List<ProductModel> {
        val matched: MutableList<ProductModel> = mutableListOf()

        val data = productViewModel?.getScanned_()!! //?.observe(this@UserIngredientsActivity, object : Observer<List<IngredientModel>>
        for (product in data.value!!) {
            if (product.name.contains(typed, ignoreCase=true)) {
                matched.add(product)
            }
        }
        return matched
    }
}
