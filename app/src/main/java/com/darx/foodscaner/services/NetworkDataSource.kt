package com.darx.foodscaner.services

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProviders
import com.darx.foodscaner.camerafragment.camera.WorkflowModel
import com.darx.foodscaner.database.GroupModel
import com.darx.foodscaner.database.IngredientModel
import com.darx.foodscaner.database.ProductModel

interface NetworkDataSource {
    val product: LiveData<ProductModel>
    val ingredient: LiveData<IngredientModel>
    val productSearch: LiveData<List<ProductModel>>
    val ingredients: LiveData<List<IngredientModel>>
    val ingredientSearch: LiveData<List<IngredientModel>>
    val groupIngredients: LiveData<List<IngredientModel>>
    val groups: LiveData<List<GroupModel>>
    val groupSearch: LiveData<List<GroupModel>>
    val ctx: Context

    suspend fun fetchProductByBarcode(barcode: Long, callback: Callback = DefaultCallback(ctx))
    suspend fun fetchProductSearch(name: String, count: Int, page: Int, callback: Callback = DefaultCallback(ctx))
    suspend fun fetchIngredients(count: Int, page: Int, callback: Callback = DefaultCallback(ctx))
    suspend fun fetchIngredientSearch(name: String, count: Int, page: Int, callback: Callback = DefaultCallback(ctx))
    suspend fun fetchGroupIngredients(id: Int, count: Int, page: Int, callback: Callback = DefaultCallback(ctx))
    suspend fun getIngredientByID(id: Int, callback: Callback = DefaultCallback(ctx))
    suspend fun fetchGroups(callback: Callback = DefaultCallback(ctx))
    suspend fun fetchGroupSearch(name: String, callback: Callback = DefaultCallback(ctx))
    suspend fun productAdd(barcode: Long, name: String, callback: Callback = DefaultCallback(ctx))

    interface Callback {
        fun onNoConnectivityException()
        fun onHttpException()
        fun onTimeoutException()
        fun onException()
    }

    class DefaultCallback(var ctx: Context): Callback {
        override fun onNoConnectivityException() {
            Log.e("Connectivity", "No internet connection.")
            Toast.makeText(
                this.ctx, "Нет интернета!",
                Toast.LENGTH_SHORT
            ).show()
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