package com.darx.foodwise.database

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.OnConflictStrategy.IGNORE
import androidx.room.Transaction
import java.util.*


@Dao
interface ProductsDAO {
    @Query("SELECT * from products WHERE scanned=1 ORDER BY date DESC") // ORDER BY
    fun getScanned(): LiveData<List<ProductModel>>

    @Query("SELECT * from products WHERE starred=1 ORDER BY date DESC") //??
    fun getFavourites(): LiveData<List<ProductModel>>

    @Query("SELECT * from products WHERE barcode = :barcode")
    fun getOne(barcode: Long): LiveData<ProductModel>

    @Insert(onConflict = IGNORE)
    fun add(product: ProductModel): Long

    @Query("UPDATE products SET date = :date AND scanned = :scanned AND starred = :starred WHERE barcode = :barcode")
    fun updateDateAndScanned(barcode: Long, date: Date, scanned: Boolean, starred: Boolean)

    @Update
    fun updateStarred(product: ProductModel)

    @Delete
    fun deleteOne(product: ProductModel)

    @Query("DELETE from products")
    fun deleteAll()

    @Transaction
    fun upsert(product: ProductModel) {
        val id = add(product)
        if (id.compareTo(-1) == 0) {
            updateStarred(product)
        }
    }
}