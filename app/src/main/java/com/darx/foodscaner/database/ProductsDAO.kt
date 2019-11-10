package com.darx.foodscaner.database

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.OnConflictStrategy.IGNORE
import androidx.room.OnConflictStrategy.REPLACE
import android.provider.SyncStateContract.Helpers.update
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import androidx.room.Transaction
import java.util.*


@Dao
interface ProductsDAO {
    @Query("SELECT * from products ORDER BY date DESC") // ORDER BY
    fun getAll(): LiveData<List<ProductModel>>

    @Query("SELECT * from products WHERE starred=1 ORDER BY date DESC") //??
    fun getFavourites(): LiveData<List<ProductModel>>

    @Query("SELECT * from products WHERE barcode = :barcode")
    fun getOne(barcode: Long): ProductModel

    @Insert(onConflict = IGNORE)
    fun add(product: ProductModel): Long

    @Query("UPDATE products SET date = :date WHERE barcode = :barcode")
    fun updateDate(barcode: Long, date: Date)

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
            updateDate(product.barcode, product.date)
        }
    }
}