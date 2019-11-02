package com.darx.foodscaner.services

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.darx.foodscaner.database.ProductModel
import com.darx.foodscaner.internal.NoConnectivityException
import com.darx.foodscaner.services.NetworkDataSource.Callback
import retrofit2.HttpException
import java.net.SocketTimeoutException

class NetworkDataSourceImpl(private val apiService: ApiService) : NetworkDataSource {

    private val _product = MutableLiveData<ProductModel>()
    private val _productSearch = MutableLiveData<List<ProductModel>>()

    override val product: LiveData<ProductModel>
        get() = _product

    override val productSearch: LiveData<List<ProductModel>>
        get() = _productSearch


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
    }

}