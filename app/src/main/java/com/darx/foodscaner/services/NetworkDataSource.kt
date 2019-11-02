package com.darx.foodscaner.services

import android.util.Log
import androidx.lifecycle.LiveData
import com.darx.foodscaner.database.ProductModel

interface NetworkDataSource {
    val product: LiveData<ProductModel>
    val productSearch: LiveData<List<ProductModel>>

    suspend fun fetchProductByBarcode(barcode: Long, callback: Callback = DefaultCallback())
    suspend fun fetchProductSearch(name: String, callback: Callback = DefaultCallback())

    interface Callback {
        fun onNoConnectivityException()
        fun onHttpException()
        fun onTimeoutException()
    }

    class DefaultCallback: Callback {
        override fun onNoConnectivityException() {
            Log.e("Connectivity", "No internet connection.")
        }
        override fun onHttpException() {
            Log.e("HTTP", "Wrong answer.")
        }
        override fun onTimeoutException() {
            Log.e("Timeout", "Long time.")
        }
    }

}