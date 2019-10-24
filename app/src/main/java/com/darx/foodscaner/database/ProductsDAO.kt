package com.darx.foodscaner.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query

@Dao
interface ProductsDAO {
    @Query("SELECT * from products")
    fun getAll(): List<ProductsModel>

    @Query("SELECT * from products WHERE id = :id")
    fun getOne(id: Int): ProductsModel

    @Insert(onConflict = REPLACE)
    fun add(product: ProductsModel)

    @Query("DELETE from products WHERE id = :id")
    fun deleteOne(id: Int)

    @Query("DELETE from products")
    fun deleteAll()
}