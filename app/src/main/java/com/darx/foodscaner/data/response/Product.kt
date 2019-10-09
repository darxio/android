package com.darx.foodscaner.data.response

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class Product (
    @PrimaryKey(autoGenerate = false) var id: Long?,
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "barcode") var barcode: Int,
    @ColumnInfo(name = "info") var info: String
)