package com.darx.foodscaner.services

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.darx.foodscaner.data.request.RegistrationInfo
import com.darx.foodscaner.data.response.Registration
import com.darx.foodscaner.internal.NoConnectivityException

class NetworkDataSourceImpl(
    private val apiService: ApiService
) : NetworkDataSource {
    private val _downloadedData = MutableLiveData<Registration>()

    override val downloadedData: LiveData<Registration>
        get() = _downloadedData

    override suspend fun fetchRegistration(registration: RegistrationInfo) {
        try {
            val fetchedRegistration = apiService
                .registration(registration)
                .await()
            _downloadedData.postValue(fetchedRegistration)
        }
        catch (e: NoConnectivityException) {
            Log.e("Connectivity", "No internet connection.", e)
        }
    }
}