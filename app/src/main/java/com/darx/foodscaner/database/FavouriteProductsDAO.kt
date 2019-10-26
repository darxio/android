package com.darx.foodscaner.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query

@Dao
interface FavouriteProductsDAO {
    @Query("SELECT * from favourite_products")
    fun getAll(): List<FavouriteProductModel>

    @Query("SELECT * from favourite_products WHERE barcode = :barcode")
    fun getOne(barcode: Long): FavouriteProductModel

    @Insert(onConflict = REPLACE)
    fun add(favouriteProduct: FavouriteProductModel)

    @Query("DELETE from favourite_products WHERE barcode = :barcode")
    fun deleteOne(barcode: Long)

    @Query("DELETE from favourite_products")
    fun deleteAll()
}