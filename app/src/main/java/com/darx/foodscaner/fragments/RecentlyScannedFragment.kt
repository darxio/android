package com.darx.foodscaner.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.darx.foodscaner.ProductActivity

import com.darx.foodscaner.R
import com.darx.foodscaner.adapters.ProductsAdapter
import com.darx.foodscaner.database.AppDatabase
import com.darx.foodscaner.database.ProductModel
import com.darx.foodscaner.database.ScannedProductModel
import com.darx.foodscaner.database.ScannedProductsDAO
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_recently_scanned.view.*
import java.io.Serializable



class RecentlyScannedFragment : Fragment() {
    private var db: AppDatabase? = null
    private var scannedProductsDAO: ScannedProductsDAO? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_recently_scanned, container, false)

        db = AppDatabase.getInstance(context = context!!) // ask
            scannedProductsDAO = db?.scannedProductsDAO()
        val recentlyScannedProducts: List<ScannedProductModel>? = null

        Observable.fromCallable({

            var p1 = ScannedProductModel(barcode = 1321345364, name = "Chebupeli", description = "Ploxo", contents = "a,f,g,g,b",categoryURL = "/dsf",mass = "9324",bestBefore = "NULL",nutrition ="1",manufacturer = "43",image ="dfs",date = null)
            var p2 = ScannedProductModel(barcode = 555555555, name = "Sir", description = "Xorosho", contents = "a,f,g,g,b",categoryURL = "/dsf",mass = "9324",bestBefore = "NULL",nutrition ="1",manufacturer = "43",image ="dfs",date = null)

            scannedProductsDAO?.add(p1)
            scannedProductsDAO?.add(p2)
            db?.scannedProductsDAO()?.getAll()
        }).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({it->
                val productsAdapter =
                    ProductsAdapter(it!!, object : ProductsAdapter.Callback {
                        override fun onItemClicked(item: ScannedProductModel) {
                            val intent = Intent(
                                this@RecentlyScannedFragment.activity,
                                ProductActivity::class.java
                            )
                            intent.putExtra("PRODUCT", item as Serializable)
                            startActivity(intent)
                        }
                    })
                view.recently_scanned_products_recycler_view.adapter = productsAdapter
            })


        return view
    }
}
