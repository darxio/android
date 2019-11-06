package com.darx.foodscaner.services


import com.darx.foodscaner.database.GroupModel
import com.darx.foodscaner.database.IngredientModel
import com.darx.foodscaner.database.ProductModel
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlinx.coroutines.Deferred
import okhttp3.*
import retrofit2.http.*


interface ApiService {
    // === PRODUCTS ===
    @GET("products")
    fun productsAll(): Deferred<List<ProductModel>>

    @GET("products/barcode/{barcode}")
    fun productByBarcode(
        @Path("barcode") barcode: Long
    ): Deferred<ProductModel>

    @GET("products/search/{name}")
    fun productSearch(
        @Path("name") name: String
    ): Deferred<List<ProductModel>>


    // === GROUPS ===
    @GET("groups")
    fun groups(): Deferred<List<GroupModel>>

    @GET("groups")
    fun groupByID(
        @Query("id") id: Int
    ): Deferred<GroupModel>

    @GET("groups")
    fun groupByName(
        @Query("name") name: String
    ): Deferred<GroupModel>

    @GET("groups/search/{name}")
    fun groupSearsh(
        @Path("name") name: String
    ): Deferred<List<GroupModel>>


    // === INGREDIENTS ===
    @GET("ingredients/search/{name}")
    fun ingredientSearsh(
        @Path("name") name: String
    ): Deferred<List<IngredientModel>>


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