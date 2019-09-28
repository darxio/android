package com.darx.foodscaner.services

import com.darx.foodscaner.services.request.RegistrationInfo
import com.darx.foodscaner.services.response.Groups
import com.darx.foodscaner.services.response.Registration
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlinx.coroutines.Deferred
import okhttp3.*
import retrofit2.http.*
import java.io.IOException
import java.security.acl.Group
import java.util.concurrent.TimeUnit


//http://api.weatherstack.com/current?access_key=82e4f09b1d58065e0a32fb06341135c2&query=New%20York&Lang=ru

interface ApiService {

//    @GET("current")
//    fun search(
//        @Query("access_key") key: String,
//        @Query("query") location: String,
//        @Query("lang") lang: String = "en"
//    ): Deferred<Wether>

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
            val okHttpClient = OkHttpClient.Builder()
                .cookieJar(ApiCookieJar())
//                .interceptors().add(object : Interceptor() {
//                    @Throws(IOException::class)
//                    fun intercept(chain: Interceptor.Chain): Response<*> {
//                        return onOnIntercept(chain)
//                    }
//                })
                .build()

            val retrofit = Retrofit.Builder()
                .client(okHttpClient)
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://api.rasseki.ru/")
                .build()

            return retrofit.create(ApiService::class.java);
        }
    }
}