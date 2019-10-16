package com.darx.foodscaner.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.darx.foodscaner.data.response.Product

@Dao
interface ProductDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsert(product: Product)

    @Query("SELECT * FROM products")
    fun getProducts(): LiveData<List<Product>>
}