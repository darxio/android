package com.darx.foodscaner.fragments


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
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
import android.view.*
import kotlinx.android.synthetic.main.fragment_add_product.view.*
import kotlinx.android.synthetic.main.activity_favorites.view.*
import androidx.appcompat.app.AppCompatActivity
import android.widget.LinearLayout
import com.arlib.floatingsearchview.FloatingSearchView
import com.google.android.material.snackbar.Snackbar


class RecentlyScannedFragment : Fragment() {

    private var productViewModel: ProductViewModel? = null
    private var networkDataSource: NetworkDataSourceImpl? = null
    private var scannedProductsAdapter: ProductsAdapter? = null
    private var searchedProductsAdapter: ProductsAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(com.darx.foodscaner.R.layout.fragment_recently_scanned, container, false)
//
//        setHasOptionsMenu(true);
//        (activity as AppCompatActivity).setSupportActionBar(rs_toolbar)
//        (activity as AppCompatActivity).supportActionBar!!.setTitle("Contacts")


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
//        view.all_products_rv.adapter = searchedProductsAdapter

        networkDataSource?.productSearch?.observe(this@RecentlyScannedFragment, Observer {
            searchedProductsAdapter?.addItems(it)
        })

        // searching
        view.rs_search_view.setOnQueryChangeListener(FloatingSearchView.OnQueryChangeListener { oldQuery, newQuery ->
            if (newQuery.length == 0) {
                searchedProductsAdapter?.addItems(listOf())
            } else {
                GlobalScope.launch(Dispatchers.Main) {
                    networkDataSource?.fetchProductSearch(newQuery, 15, 0)
                }
            }
            scannedProductsAdapter?.addItems(matchMyProducts(newQuery))
        })

//        rs_search_view.setOnMenuItemClickListener(object : FloatingSearchView.OnMenuItemClickListener {
//            override fun onActionMenuItemSelected(item: MenuItem?) {
//                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//            }
//
//            fun onMenuItemSelected(item: MenuItem) {
//
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
