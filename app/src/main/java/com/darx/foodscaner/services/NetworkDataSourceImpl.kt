package com.darx.foodscaner.services

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.darx.foodscaner.database.GroupModel
import com.darx.foodscaner.database.IngredientModel
import com.darx.foodscaner.database.ProductModel
import com.darx.foodscaner.internal.NoConnectivityException
import com.darx.foodscaner.services.NetworkDataSource.Callback
import retrofit2.HttpException
import java.lang.Exception
import java.net.SocketTimeoutException

class NetworkDataSourceImpl(private val apiService: ApiService) : NetworkDataSource {

    private val _product = MutableLiveData<ProductModel>()
    private val _productSearch = MutableLiveData<List<ProductModel>>()
    private val _ingredientSearch = MutableLiveData<List<IngredientModel>>()
    private val _groups = MutableLiveData<List<GroupModel>>()
    private val _groupSearch = MutableLiveData<List<GroupModel>>()

    override val product: LiveData<ProductModel>
        get() = _product

    override val productSearch: LiveData<List<ProductModel>>
        get() = _productSearch

    override val ingredientSearch: LiveData<List<IngredientModel>>
        get() = _ingredientSearch

    override val groups: LiveData<List<GroupModel>>
        get() = _groups

    override val groupSearch: LiveData<List<GroupModel>>
        get() = _groupSearch


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

}