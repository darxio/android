package com.darx.foodscaner.services.request


import com.google.gson.annotations.SerializedName

data class RegistrationInfo(
    val username: String,
    val password: String
)