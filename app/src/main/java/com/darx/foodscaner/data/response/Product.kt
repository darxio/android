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
//    @TypeConverters(StringArrayTypeConverters::class)
//    @ColumnInfo(name = "ingredients") var ingredients: List<String>,
//    @TypeConverters(StringArrayTypeConverters::class)
//    @ColumnInfo(name = "ingredient_types") var ingredient_types: List<String>,
    @ColumnInfo(name = "info") var info: String
): Serializable