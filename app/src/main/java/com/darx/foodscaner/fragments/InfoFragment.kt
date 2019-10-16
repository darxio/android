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
import kotlinx.android.synthetic.main.fragment_info.view.*
import java.io.Serializable


class InfoFragment : Fragment() {

//    private val apiService = ApiService(/*ConnectivityInterceptorImpl(this.baseContext)*/)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_info, container, false)
        fetchProducts(view)
        return view
    }

    private fun fetchProducts(view:View) {
//        GlobalScope.launch(Dispatchers.Main) {
//            val response = apiService.products().await()
//
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
//        }
    }

}
