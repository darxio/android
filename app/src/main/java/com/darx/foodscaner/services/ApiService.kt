package com.darx.foodscaner.services

import com.darx.foodscaner.data.request.LoginRqst
import com.darx.foodscaner.data.request.RegistrationRqst
import com.darx.foodscaner.data.response.Group
import com.darx.foodscaner.data.response.Login
import com.darx.foodscaner.data.response.Product
import com.darx.foodscaner.data.response.Registration
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlinx.coroutines.Deferred
import okhttp3.*
import retrofit2.http.*


interface ApiService {

    // === USERS ===
    @POST("users")
    fun registration(
        @Body registration: RegistrationRqst
    ): Deferred<Registration>

    @POST("session")
    fun login(
        @Body registration: LoginRqst
    ): Deferred<Login>

    @DELETE("session")
    fun logout(): Deferred<Login>


    // === PRODUCTS ===
    @GET("products")
    fun products(): Deferred<List<Product>>

    @GET("products")
    fun productByBarcode(
        @Query("barcode") barcode: Long
    ): Deferred<Product>


    // === GROUPS ===
    @GET("groups")
    fun groups(): Deferred<List<Group>>

    @GET("groups")
    fun groupById(
        @Query("id") id: Int
    ): Deferred<Group>

    @GET("groups")
    fun groupByName(
        @Query("name") name: String
    ): Deferred<Group>


    @POST("user/groups")
    fun userGroups(
        @Body username: String
    ): Deferred<List<Group>>

    @GET("user/groups")
    fun userGroupsByName(
        @Query("name") name: String
    ): Deferred<Group>

    @POST("user/groups")
    fun addGroup(): Deferred<Group>

    @DELETE("user/groups")
    fun removeGroup(): Deferred<Group>


    companion object {
        operator fun invoke(
            connectivityInterceptor: ConnectivityInterceptor
        ): ApiService {
            val httpClient = OkHttpClient.Builder()
                .cookieJar(ApiCookieJar())
                .addInterceptor(connectivityInterceptor)
                .build()

            val retrofit = Retrofit.Builder()
                .client(httpClient)
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://api.rasseki.ru/")
                .build()

            return retrofit.create(ApiService::class.java);
        }
    }
}