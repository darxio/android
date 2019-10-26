package com.darx.foodscaner.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query

@Dao
interface ScannedProductsDAO {
    @Query("SELECT * from scanned_products")
    fun getAll(): List<ScannedProductModel>

    @Query("SELECT * from scanned_products WHERE barcode = :barcode")
    fun getOne(barcode: Long): ScannedProductModel

    @Insert(onConflict = REPLACE)
    fun add(scannedProduct: ScannedProductModel)

    @Query("DELETE from scanned_products WHERE barcode = :barcode")
    fun deleteOne(barcode: Long)

    @Query("DELETE from scanned_products")
    fun deleteAll()
}