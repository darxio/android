package com.darx.foodscaner.services

import androidx.lifecycle.LiveData
import com.darx.foodscaner.data.request.RegistrationInfo
import com.darx.foodscaner.data.response.Registration

interface NetworkDataSource {
    val downloadedData: LiveData<Registration>

    suspend fun fetchRegistration(
        registration: RegistrationInfo
    )
}