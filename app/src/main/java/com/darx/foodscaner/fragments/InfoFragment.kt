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
import com.darx.foodscaner.data.response.Product
import com.darx.foodscaner.services.ApiService
import com.darx.foodscaner.services.ConnectivityInterceptorImpl
import com.darx.foodscaner.services.NetworkDataSourceImpl
import kotlinx.android.synthetic.main.fragment_info.view.*
import kotlinx.android.synthetic.main.fragment_profile.*
import java.io.Serializable
import androidx.lifecycle.Observer
import com.darx.foodscaner.data.request.RegistrationRqst
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class InfoFragment : Fragment() {

    private var networkDataSource: NetworkDataSourceImpl? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_info, container, false)

        val apiService = ApiService(ConnectivityInterceptorImpl(this.context!!))
        networkDataSource = NetworkDataSourceImpl(apiService)

        networkDataSource?.product?.observe(this, Observer {
//            username.text = it.

            var response: List<Product> = mutableListOf(
                Product(111, "Choco-Pie", 1111, "adskfjkasjdfk"),
                Product(111, "Choco-Pie", 1111, "adskfjkasjdfk"),
                Product(111, "Choco-Pie", 1111, "adskfjkasjdfk"),
                Product(111, "Choco-Pie", 1111, "adskfjkasjdfk")
            )

            val productAdapter = ProductAdapter(response, object : ProductAdapter.Callback {
                override fun onItemClicked(item: Product) {
                    val intent = Intent(this@InfoFragment.activity, ProductActivity::class.java)
                    intent.putExtra("PRODUCT", item as Serializable)
                    startActivity(intent)
                }
            })
            view.productRecycler.adapter = productAdapter
        })

        fetchProducts(view)
        return view
    }

    private fun fetchProducts(view:View) {

        GlobalScope.launch(Dispatchers.Main) {
            networkDataSource?.fetchProductByBarcode (registrationInfo)
        }

        GlobalScope.launch(Dispatchers.Main) {
            val response = apiService.products().await()
        }
    }

}
