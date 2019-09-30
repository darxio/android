package com.darx.foodscaner.services

import com.darx.foodscaner.data.request.RegistrationInfo
import com.darx.foodscaner.data.response.Groups
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
        @Body registration: RegistrationInfo
    ): Deferred<Registration>

    @POST("session")
    fun login(
        @Body registration: RegistrationInfo
    ): Deferred<Registration>

    @DELETE("session")
    fun logout(): Deferred<Registration>

    // === GROUPS ===
    @GET("groups")
    fun groupsById(
        @Query("id") id: Int
    ): Deferred<Groups>

    @GET("groups")
    fun groupsByName(
        @Query("name") name: String
    ): Deferred<Groups>

    @GET("user/groups")
    fun userGroupsById(
        @Query("id") id: Int
    ): Deferred<Groups>

    @GET("user/groups")
    fun userGroupsByName(
        @Query("name") name: String
    ): Deferred<Groups>

    @POST("user/groups")
    fun addGroup(): Deferred<Groups>

    @DELETE("user/groups")
    fun removeGroup(): Deferred<Groups>


    companion object Factory {
        fun create(): ApiService {
            val httpClient = OkHttpClient.Builder()
                .cookieJar(ApiCookieJar())
//                .interceptors().add(object : Interceptor {
//                    @Throws(IOException::class)
//                    override fun intercept(chain: Interceptor.Chain): Response<*> {
//                        return OnIntercept(chain)
//                    }
//                })
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