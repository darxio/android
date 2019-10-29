package com.darx.foodscaner.database

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE

@Dao
interface ProductsDAO {
    @Query("SELECT * from products ORDER BY date DESC") // ORDER BY
    fun getAll(): LiveData<List<ProductModel>>

    @Query("SELECT * from products WHERE starred=1 ORDER BY date DESC") //??
    fun getFavourites(): LiveData<List<ProductModel>>

    @Query("SELECT * from products WHERE barcode = :barcode")
    fun getOne(barcode: Long): ProductModel

    @Insert(onConflict = REPLACE)
    fun add(product: ProductModel)

    @Update
    fun updateStarred(product: ProductModel)

    @Delete
    fun deleteOne(product: ProductModel)

    @Query("DELETE from products")
    fun deleteAll()
}