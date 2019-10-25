package com.darx.foodscaner.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query

@Dao
interface ProductsDAO {
    @Query("SELECT * from products")
    fun getAll(): List<ProductModel>

    @Query("SELECT * from products WHERE barcode = :barcode")
    fun getOne(barcode: Long): ProductModel

    @Insert(onConflict = REPLACE)
    fun add(product: ProductModel)

    @Query("DELETE from products WHERE barcode = :barcode")
    fun deleteOne(barcode: Long)

    @Query("DELETE from products")
    fun deleteAll()
}