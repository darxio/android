package com.darx.foodscaner.services

import android.util.Log
import androidx.lifecycle.LiveData
import com.darx.foodscaner.database.GroupModel
import com.darx.foodscaner.database.IngredientModel
import com.darx.foodscaner.database.ProductModel

interface NetworkDataSource {
    val product: LiveData<ProductModel>
    val productSearch: LiveData<List<ProductModel>>
    val ingredientSearch: LiveData<List<IngredientModel>>
    val groups: LiveData<List<GroupModel>>
    val groupSearch: LiveData<List<GroupModel>>

    suspend fun fetchProductByBarcode(barcode: Long, callback: Callback = DefaultCallback())
    suspend fun fetchProductSearch(name: String, callback: Callback = DefaultCallback())
    suspend fun fetchIngredientSearch(name: String, callback: Callback = DefaultCallback())
    suspend fun fetchGroups(callback: Callback = DefaultCallback())
    suspend fun fetchGroupSearch(name: String, callback: Callback = DefaultCallback())

    interface Callback {
        fun onNoConnectivityException()
        fun onHttpException()
        fun onTimeoutException()
        fun onException()
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
        override fun onException() {
            Log.e("Exception", "default.")
        }
    }

}