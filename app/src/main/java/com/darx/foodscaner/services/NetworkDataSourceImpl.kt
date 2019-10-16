package com.darx.foodscaner.services

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.darx.foodscaner.data.request.LoginRqst
import com.darx.foodscaner.data.request.RegistrationRqst
import com.darx.foodscaner.data.response.Login
import com.darx.foodscaner.data.response.Product
import com.darx.foodscaner.data.response.Registration
import com.darx.foodscaner.internal.NoConnectivityException

class NetworkDataSourceImpl(private val apiService: ApiService) : NetworkDataSource {

    private val _registration = MutableLiveData<Registration>()
    private val _login = MutableLiveData<Login>()
    private val _logout = MutableLiveData<Login>()
    private val _product = MutableLiveData<Product>()


    override val registration: LiveData<Registration>
        get() = _registration

    override val login: LiveData<Login>
        get() = _login

    override val logout: LiveData<Login>
        get() = _logout

    override val product: LiveData<Product>
        get() = _product


    override suspend fun fetchRegistration(registration: RegistrationRqst) {
        try {
            val fetchedRegistration = apiService.registration(registration).await()
            _registration.postValue(fetchedRegistration)
        }
        catch (e: NoConnectivityException) {
            Log.e("Connectivity", "No internet connection.", e)
        }
    }

    override suspend fun fetchLogin(login: LoginRqst) {
        try {
            val fetchedLogin = apiService.login(login).await()
            _login.postValue(fetchedLogin)
        }
        catch (e: NoConnectivityException) {
            Log.e("Connectivity", "No internet connection.", e)
        }
    }

    override suspend fun fetchLogout() {
        try {
            val fetchedLogout = apiService.logout().await()
            _logout.postValue(fetchedLogout)
        }
        catch (e: NoConnectivityException) {
            Log.e("Connectivity", "No internet connection.", e)
        }
    }

    override suspend fun fetchProductByBarcode(barcode: Long) {
        try {
            val fetchedProductByBarcode = apiService.productByBarcode(barcode).await()
            _product.postValue(fetchedProductByBarcode)
        }
        catch (e: NoConnectivityException) {
            Log.e("Connectivity", "No internet connection.", e)
        }
    }

}