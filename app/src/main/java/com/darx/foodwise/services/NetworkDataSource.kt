package com.darx.foodwise.services

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import com.darx.foodwise.database.FruitModel
import com.darx.foodwise.database.GroupModel
import com.darx.foodwise.database.IngredientModel
import com.darx.foodwise.database.ProductModel
import okhttp3.MultipartBody

interface NetworkDataSource {
    val product: LiveData<ProductModel>
    val ingredient: LiveData<IngredientModel>
    val productSearch: LiveData<List<ProductModel>>
    val ingredients: LiveData<List<IngredientModel>>
    val ingredientSearch: LiveData<List<IngredientModel>>
    val groupIngredients: LiveData<List<IngredientModel>>
    val groupIngredientsSearch: LiveData<List<IngredientModel>>
    val groups: LiveData<List<GroupModel>>
    val groupSearch: LiveData<List<GroupModel>>
    val fruit: LiveData<ProductModel>
    val ctx: Context

    suspend fun fetchProductByBarcode(barcode: Long, callback: Callback = DefaultCallback(ctx))
    suspend fun fetchProductSearch(name: String, count: Int, page: Int, callback: Callback = DefaultCallback(ctx))
    suspend fun fetchIngredients(count: Int, page: Int, callback: Callback = DefaultCallback(ctx))
    suspend fun fetchIngredientSearch(name: String, count: Int, page: Int, callback: Callback = DefaultCallback(ctx))
    suspend fun fetchGroupIngredients(id: Int, count: Int, page: Int, callback: Callback = DefaultCallback(ctx))
    suspend fun fetchGroupIngredientsSearch(group_id: Int, query: String, count: Int, page: Int, callback: Callback = DefaultCallback(ctx))
    suspend fun getIngredientByID(id: Int, callback: Callback = DefaultCallback(ctx))
    suspend fun fetchGroups(callback: Callback = DefaultCallback(ctx))
    suspend fun fetchGroupSearch(name: String, callback: Callback = DefaultCallback(ctx))
    suspend fun productAdd(barcode: Long, name: String, callback: Callback = DefaultCallback(ctx))
    suspend fun searchFruit(file: MultipartBody.Part, callback: Callback = DefaultCallback(ctx))

    interface Callback {
        fun onNoConnectivityException()
        fun onHttpException()
        fun onTimeoutException()
        fun onException()
    }

    class DefaultCallback(var ctx: Context): Callback {
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