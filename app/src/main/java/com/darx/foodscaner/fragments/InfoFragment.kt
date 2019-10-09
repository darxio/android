package com.darx.foodscaner.fragments


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.darx.foodscaner.ProductActivity

import com.darx.foodscaner.R
import com.darx.foodscaner.data.response.Product
import com.darx.foodscaner.adapters.ProductAdapter
import com.darx.foodscaner.data.request.RegistrationInfo
import com.darx.foodscaner.services.ApiService
import kotlinx.android.synthetic.main.fragment_info.view.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.Serializable


/**
 * A simple [Fragment] subclass.
 */
class InfoFragment : Fragment() {

    private val apiService = ApiService(/*ConnectivityInterceptorImpl(this.baseContext)*/)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_info, container, false)
        fetchProducts(view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    private fun fetchProducts(view:View) {
        GlobalScope.launch(Dispatchers.Main) {
            val response = apiService.products().await()

            val productAdapter = ProductAdapter(response, object : ProductAdapter.Callback {
                override fun onItemClicked(item: Product) {
                    val intent = Intent(this@InfoFragment.activity, ProductActivity::class.java)
                    intent.putExtra("PRODUCT", item as Serializable)
                    startActivity(intent)
                }
            })
            view.productRecycler.adapter = productAdapter
        }
    }

}
