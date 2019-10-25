package com.darx.foodscaner.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.darx.foodscaner.ProductActivity

import com.darx.foodscaner.R
import com.darx.foodscaner.adapters.ProductAdapter
import com.darx.foodscaner.models.Product
import com.darx.foodscaner.services.ApiService
import com.darx.foodscaner.services.ConnectivityInterceptorImpl
import com.darx.foodscaner.services.NetworkDataSourceImpl
import kotlinx.android.synthetic.main.fragment_recently_scanned.view.*
import java.io.Serializable
import androidx.lifecycle.Observer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class RecentlyScannedFragment : Fragment() {

    private var networkDataSource: NetworkDataSourceImpl? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_recently_scanned, container, false)

        val apiService = ApiService(ConnectivityInterceptorImpl(this.context!!))
        networkDataSource = NetworkDataSourceImpl(apiService)

        networkDataSource?.product?.observe(this, Observer {
//            username.text = it.

            var response: List<Product> = mutableListOf(
                Product(111, "Choco-Pie", 1111, "adskfjkasjdfk"),
                Product(111, "Choco-Pie", 1111, "adskfjkasjdfk"),
//                Product(111, "Choco-Pie", 1111, "adskfjkasjdfk"),
//                Product(111, "Choco-Pie", 111, Array(["adskfjkasjdfk", "dfas"]), ["adsf", "sdfaf"]),
                it
            )

            val productAdapter = ProductAdapter(response, object : ProductAdapter.Callback {
                override fun onItemClicked(item: Product) {
                    val intent = Intent(this@RecentlyScannedFragment.activity, ProductActivity::class.java)
                    intent.putExtra("PRODUCT", item as Serializable)
                    startActivity(intent)
                }
            })
            view.productRecycler.adapter = productAdapter
        })

        fetchProducts(view, 11111)
        return view
    }

    private fun fetchProducts(view:View, barcode: Long) {
        GlobalScope.launch(Dispatchers.Main) {
            networkDataSource?.fetchProductByBarcode(barcode
//                , object : NetworkDataSource.Callback {
//                override fun onHttpException() {
//                    Log.e("test", "test")
//                }
//
//                override fun onNoConnectivityException() {
//                    Log.e("test", "test")
//                }
//            }
            )
        }
    }

}
