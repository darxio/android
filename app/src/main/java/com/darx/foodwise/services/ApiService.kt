package com.darx.foodwise.services


import com.darx.foodwise.database.FruitModel
import com.darx.foodwise.database.GroupModel
import com.darx.foodwise.database.IngredientModel
import com.darx.foodwise.database.ProductModel
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlinx.coroutines.Deferred
import okhttp3.*
import retrofit2.http.*
import retrofit2.http.POST
import retrofit2.Call


interface ApiService {
    // === PRODUCTS ===
    @GET("products")
    fun productsAll(): Deferred<List<ProductModel>>

    @GET("products/barcode/{barcode}")
    fun productByBarcode(
        @Path("barcode") barcode: Long
    ): Deferred<ProductModel>

    @GET("products/search/{name}/{count}/{page}")
    fun productSearch(
        @Path("name") name: String,
        @Path("count") count: Int,
        @Path("page") page: Int
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

    @GET("groups/search_ing/{group_id}/{query}/{count}/{page}")
    fun groupIngredientsSearsh(
        @Path("group_id") group_id: Int,
        @Path("query") query: String,
        @Path("count") count: Int,
        @Path("page") page: Int
    ): Deferred<List<IngredientModel>>


    // === INGREDIENTS ===
    @GET("/groups/ingredients/{group_id}/{count}/{page}")
    fun groupIngredients(
        @Path("group_id") group_id: Int,
        @Path("count") count: Int,
        @Path("page") page: Int
    ): Deferred<List<IngredientModel>>

    @GET("/ingredients/top/{count}/{page}")
    fun ingredientsTop(
        @Path("count") count: Int,
        @Path("page") page: Int
    ): Deferred<List<IngredientModel>>

    @GET("ingredients/search/{name}/{count}/{page}")
    fun ingredientSearsh(
        @Path("name") name: String,
        @Path("count") count: Int,
        @Path("page") page: Int
    ): Deferred<List<IngredientModel>>

    // === INGREDIENTS ===
    @GET("ingredients/name/{id}")
    fun ingredientGetByID(
        @Path("id") id: Int
    ): Deferred<IngredientModel>

    // === PRODUCT ADDITION ===
//    @GET("/products/add/{barcode}/{name}")
//    fun productAdd(
//        @Path("barcode") barcode: Long,
//        @Path("name") name: String
//    )

    @POST("/products/add")
    fun productAdd(
        @Body data: ProductData
    ): Deferred<Unit>

    data class ProductData(
        val barcode: Long,
        val name: String
    )

    @Multipart
    @POST("/fruits/search")
    fun searchFruit(
        @Part file: MultipartBody.Part
    ): Call<ProductModel>


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