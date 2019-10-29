package com.darx.foodscaner.services

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.darx.foodscaner.database.ProductModel
import com.darx.foodscaner.internal.NoConnectivityException
import com.darx.foodscaner.services.NetworkDataSource.Callback
import retrofit2.HttpException

class NetworkDataSourceImpl(private val apiService: ApiService) : NetworkDataSource {

    private val _product = MutableLiveData<ProductModel>()

    override val product: LiveData<ProductModel>
        get() = _product


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
    }

}