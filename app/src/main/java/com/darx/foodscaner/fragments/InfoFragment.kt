package com.darx.foodscaner.fragments


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.darx.foodscaner.ProductActivity

import com.darx.foodscaner.R
import com.darx.foodscaner.data.Product
import com.darx.foodscaner.adapters.ProductAdapter
import kotlinx.android.synthetic.main.fragment_info.view.*

/**
 * A simple [Fragment] subclass.
 */
class InfoFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_info, container, false)
        val items = listOf(
            Product("Яблоко", "Вкусно"),
            Product("Груша", "Очень вкусно"),
            Product("Яблоко", "Вкусно"),
            Product("Груша", "Очень вкусно"),
            Product("Яблоко", "Вкусно"),
            Product("Груша", "Очень вкусно")
        )

        val productAdapter = ProductAdapter(items, object : ProductAdapter.Callback {
            override fun onItemClicked(item: Product) {
                val intent = Intent(this@InfoFragment.activity, ProductActivity::class.java)
                startActivity(intent)
            }
        })
        view.productRecycler.adapter = productAdapter
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}
