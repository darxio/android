package com.darx.foodscaner.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query

@Dao
interface FavouriteProductsDAO {
    @Query("SELECT * from favourite_products")
    fun getAll(): List<FavouriteProductsModel>

    @Query("SELECT * from favourite_products WHERE id = :id")
    fun getOne(id: Int): FavouriteProductsModel

    @Insert(onConflict = REPLACE)
    fun add(favouriteProduct: FavouriteProductsModel)

    @Query("DELETE from favourite_products WHERE id = :id")
    fun deleteOne(id: Int)

    @Query("DELETE from favourite_products")
    fun deleteAll()
}