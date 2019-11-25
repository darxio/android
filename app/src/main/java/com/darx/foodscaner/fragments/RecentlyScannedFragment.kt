package com.darx.foodscaner.fragments


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.darx.foodscaner.ProductActivity
import com.darx.foodscaner.adapters.ProductsAdapter
import com.darx.foodscaner.database.ProductModel
import com.darx.foodscaner.database.ProductViewModel
import com.darx.foodscaner.services.ApiService
import com.darx.foodscaner.services.ConnectivityInterceptorImpl
import com.darx.foodscaner.services.NetworkDataSourceImpl
import kotlinx.android.synthetic.main.fragment_recently_scanned.*
import kotlinx.android.synthetic.main.fragment_recently_scanned.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.Serializable


class RecentlyScannedFragment : Fragment() {

    private var productViewModel: ProductViewModel? = null
    private var networkDataSource: NetworkDataSourceImpl? = null
//    private var productsAdapter: ProductsAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(com.darx.foodscaner.R.layout.fragment_recently_scanned, container, false)

        val apiService = ApiService(ConnectivityInterceptorImpl(this.context!!))
        networkDataSource = NetworkDataSourceImpl(apiService, context!!)

        productViewModel = ViewModelProviders.of(this).get(ProductViewModel::class.java)

        // scaned products
        val scannedProductsAdapter = ProductsAdapter(emptyList(), productViewModel!!, this.context!!, this, true, object : ProductsAdapter.Callback {
            override fun onItemClicked(item: ProductModel) {
                val intent = Intent(this@RecentlyScannedFragment.activity, ProductActivity::class.java)
                intent.putExtra("PRODUCT", item as Serializable)
                startActivity(intent)
            }
        })
        view.history_rv.adapter = scannedProductsAdapter

        productViewModel?.getScanned_()?.observe(this@RecentlyScannedFragment, object : Observer<List<ProductModel>> {
            override fun onChanged(l: List<ProductModel>?) {
                scannedProductsAdapter.addItems(l!!)
//                scannedProductsAdapter.addItems(matchMyProducts(productSearchView?.query.toString()))
            }
        })


        // search products
        val searchedProductsAdapter = ProductsAdapter(emptyList(), productViewModel!!, this.context!!, this, false, object : ProductsAdapter.Callback {
            override fun onItemClicked(item: ProductModel) {
                val intent = Intent(this@RecentlyScannedFragment.activity, ProductActivity::class.java)
                intent.putExtra("PRODUCT", item as Serializable)
                startActivity(intent)
            }
        })
        view.all_products_rv.adapter = searchedProductsAdapter

        networkDataSource?.productSearch?.observe(this@RecentlyScannedFragment, Observer {
            searchedProductsAdapter.addItems(it)
        })

        // searching
//        view.productSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
//            override fun onQueryTextSubmit(query: String): Boolean {
//                return false
//            }
//
//            override fun onQueryTextChange(newText: String): Boolean {
//                // take data for all ingredients
//                if (newText.length >= 3) {
//                    GlobalScope.launch(Dispatchers.Main) {
//                        networkDataSource?.fetchProductSearch(newText)
//                    }
//                } else {
//                    searchedProductsAdapter.addItems(listOf())
//                }
//
//                // take data for my ingredients
//                scannedProductsAdapter.addItems(matchMyProducts(newText))
//
//                return false
//            }
//        })

        return view
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
