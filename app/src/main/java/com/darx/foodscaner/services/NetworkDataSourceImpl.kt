package com.darx.foodscaner.services

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.darx.foodscaner.database.FruitModel
import com.darx.foodscaner.database.GroupModel
import com.darx.foodscaner.database.IngredientModel
import com.darx.foodscaner.database.ProductModel
import com.darx.foodscaner.internal.NoConnectivityException
import com.darx.foodscaner.services.NetworkDataSource.Callback
import okhttp3.RequestBody
import retrofit2.HttpException
import java.lang.Exception
import java.net.SocketTimeoutException

class NetworkDataSourceImpl(private val apiService: ApiService, private val _ctx: Context) : NetworkDataSource {

    private val _product = MutableLiveData<ProductModel>()
    private val _productSearch = MutableLiveData<List<ProductModel>>()
    private val _ingredient = MutableLiveData<IngredientModel>()
    private val _ingredients = MutableLiveData<List<IngredientModel>>()
    private val _ingredientSearch = MutableLiveData<List<IngredientModel>>()
    private val _groupIngredients = MutableLiveData<List<IngredientModel>>()
    private val _groups = MutableLiveData<List<GroupModel>>()
    private val _groupSearch = MutableLiveData<List<GroupModel>>()
    private val _fruit = MutableLiveData<FruitModel>()

    override val ctx: Context
        get() = _ctx

    override val product: LiveData<ProductModel>
        get() = _product

    override val productSearch: LiveData<List<ProductModel>>
        get() = _productSearch

    override val ingredient: LiveData<IngredientModel>
        get() = _ingredient

    override val ingredients: LiveData<List<IngredientModel>>
        get() = _ingredients

    override val ingredientSearch: LiveData<List<IngredientModel>>
        get() = _ingredientSearch

    override val groupIngredients: LiveData<List<IngredientModel>>
        get() = _groupIngredients

    override val groups: LiveData<List<GroupModel>>
        get() = _groups

    override val groupSearch: LiveData<List<GroupModel>>
        get() = _groupSearch

    override val fruit: LiveData<FruitModel>
        get() = _fruit

    override suspend fun fetchProductByBarcode(barcode: Long, callback: Callback) {
        try {
            val fetchedProductByBarcode = apiService.productByBarcode(barcode).await()
            _product.postValue(fetchedProductByBarcode)
        }
        catch (e: NoConnectivityException) {
            callback.onNoConnectivityException()
        }
        catch (e: HttpException) {
            callback.onHttpException()
        }
        catch (e: SocketTimeoutException) {
            callback.onTimeoutException()
        }
        catch (e: Exception) {
            callback.onException()
        }
    }

    override suspend fun fetchProductSearch(name: String, callback: Callback) {
        try {
            val fetchedProductSearch = apiService.productSearch(name).await()
            _productSearch.postValue(fetchedProductSearch)
        }
        catch (e: NoConnectivityException) {
            callback.onNoConnectivityException()
        }
        catch (e: HttpException) {
            callback.onHttpException()
        }
        catch (e: SocketTimeoutException) {
            callback.onTimeoutException()
        }
        catch (e: Exception) {
            callback.onException()
        }
    }

    override suspend fun getIngredientByID(id: Int, callback: Callback) {
        try {
            val ingByID = apiService.ingredientGetByID(id).await()
            _ingredient.postValue(ingByID)
        }
        catch (e: NoConnectivityException) {
            callback.onNoConnectivityException()
        }
        catch (e: HttpException) {
            callback.onHttpException()
        }
        catch (e: SocketTimeoutException) {
            callback.onTimeoutException()
        }
        catch (e: Exception) {
            callback.onException()
        }
    }

    override suspend fun fetchIngredients(count: Int, page: Int, callback: Callback) {
        try {
            val fetchedIngredients = apiService.ingredientsTop(count, page).await()
            _ingredientSearch.postValue(fetchedIngredients)
        }
        catch (e: NoConnectivityException) {
            callback.onNoConnectivityException()
        }
        catch (e: HttpException) {
            callback.onHttpException()
        }
        catch (e: SocketTimeoutException) {
            callback.onTimeoutException()
        }
        catch (e: Exception) {
            callback.onException()
        }
    }

    override suspend fun fetchIngredientSearch(name: String, callback: Callback) {
        try {
            val fetchedIngredientSearch = apiService.ingredientSearsh(name).await()
            _ingredientSearch.postValue(fetchedIngredientSearch)
        }
        catch (e: NoConnectivityException) {
            callback.onNoConnectivityException()
        }
        catch (e: HttpException) {
            callback.onHttpException()
        }
        catch (e: SocketTimeoutException) {
            callback.onTimeoutException()
        }
        catch (e: Exception) {
            callback.onException()
        }
    }

    override suspend fun fetchGroupIngredients(id: Int, count: Int, page: Int, callback: Callback) {
        try {
            val fetchedGroupIngredients = apiService.groupIngredients(id, count, page).await()
            _groupIngredients.postValue(fetchedGroupIngredients)
        }
        catch (e: NoConnectivityException) {
            callback.onNoConnectivityException()
        }
        catch (e: HttpException) {
            callback.onHttpException()
        }
        catch (e: SocketTimeoutException) {
            callback.onTimeoutException()
        }
        catch (e: Exception) {
            callback.onException()
        }
    }

    override suspend fun fetchGroups(callback: Callback) {
        try {
            val fetchedGroups = apiService.groups().await()
            _groups.postValue(fetchedGroups)
        }
        catch (e: NoConnectivityException) {
            callback.onNoConnectivityException()
        }
        catch (e: HttpException) {
            callback.onHttpException()
        }
        catch (e: SocketTimeoutException) {
            callback.onTimeoutException()
        }
        catch (e: Exception) {
            callback.onException()
        }
    }

    override suspend fun fetchGroupSearch(name: String, callback: Callback) {
        try {
            val fetchedGroupSearch = apiService.groupSearsh(name).await()
            _groupSearch.postValue(fetchedGroupSearch)
        }
        catch (e: NoConnectivityException) {
            callback.onNoConnectivityException()
        }
        catch (e: HttpException) {
            callback.onHttpException()
        }
        catch (e: SocketTimeoutException) {
            callback.onTimeoutException()
        }
        catch (e: Exception) {
            callback.onException()
        }
    }

    override suspend fun productAdd(barcode: Long, name: String, callback: Callback) {
        try {
            apiService.productAdd(barcode, name)
        }
        catch (e: NoConnectivityException) {
            callback.onNoConnectivityException()
        }
        catch (e: HttpException) {
            callback.onHttpException()
        }
        catch (e: SocketTimeoutException) {
            callback.onTimeoutException()
        }
        catch (e: Exception) {
            callback.onException()
        }
    }

    override suspend fun searchFruit(file: RequestBody, callback: Callback) {
        try {
            val fetchedFruit = apiService.searchFruit(file).await()
            _fruit.postValue(fetchedFruit)
        }
        catch (e: NoConnectivityException) {
            callback.onNoConnectivityException()
        }
        catch (e: HttpException) {
            callback.onHttpException()
        }
        catch (e: SocketTimeoutException) {
            callback.onTimeoutException()
        }
        catch (e: Exception) {
            callback.onException()
        }
    }

}