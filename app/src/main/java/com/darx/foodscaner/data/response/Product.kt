package com.darx.foodscaner.data.response

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import java.io.Serializable
import com.darx.foodscaner.services.StringArrayTypeConverters


@Entity(tableName = "products")

data class Product (
    @PrimaryKey(autoGenerate = true) var id: Long?,
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "barcode") var barcode: Long,
    @ColumnInfo(name = "contents") var contents: String,
    @ColumnInfo(name = "category_url") var category_url: String,
    @ColumnInfo(name = "mass") var mass: String,
    @ColumnInfo(name = "best_before") var best_before: String,
    @ColumnInfo(name = "nutrition") var nutrition: String,
    @ColumnInfo(name = "manufacturer") var manufacturer: String,
    @ColumnInfo(name = "image") var image: String
//    @TypeConverters(StringArrayTypeConverters::class)
//    @ColumnInfo(name = "ingredients") var ingredients: List<String>,
//    @TypeConverters(StringArrayTypeConverters::class)
//    @ColumnInfo(name = "ingredient_types") var ingredient_types: List<String>,
//    @ColumnInfo(name = "Ingredients") var Ingredients: String
): Serializable