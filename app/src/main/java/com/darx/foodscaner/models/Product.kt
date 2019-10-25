package com.darx.foodscaner.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable


@Entity(tableName = "products")

data class Product (
    @PrimaryKey(autoGenerate = true) var id: Long?,
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "barcode") var barcode: Long,
//    @TypeConverters(StringArrayTypeConverters::class)
//    @ColumnInfo(name = "ingredients") var ingredients: List<String>,
//    @TypeConverters(StringArrayTypeConverters::class)
//    @ColumnInfo(name = "ingredient_types") var ingredient_types: List<String>,
    @ColumnInfo(name = "info") var info: String
): Serializable