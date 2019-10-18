package com.darx.foodscaner.services

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.darx.foodscaner.data.request.LoginRqst
import com.darx.foodscaner.data.request.RegistrationRqst
import com.darx.foodscaner.data.response.Login
import com.darx.foodscaner.data.response.Product
import com.darx.foodscaner.data.response.Registration

interface NetworkDataSource {

    val registration: LiveData<Registration>
    val login: LiveData<Login>
    val logout: LiveData<Login>
    val product: LiveData<Product>

    suspend fun fetchRegistration(registration: RegistrationRqst)
    suspend fun fetchLogin(login: LoginRqst)
    suspend fun fetchLogout()
    suspend fun fetchProductByBarcode(barcode: Long, callback: Callback = DefaultCallback())

    interface Callback {
        fun onNoConnectivityException()
        fun onHttpException()
    }

    class DefaultCallback: Callback {
        override fun onNoConnectivityException() {
            Log.e("Connectivity", "No internet connection.")
        }
        override fun onHttpException() {
            Log.e("HTTP", "Wrong answer.")
        }
    }

}