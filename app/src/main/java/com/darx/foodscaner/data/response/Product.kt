package com.darx.foodscaner.data.response

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable


@Entity(tableName = "products")
data class Product (
    @PrimaryKey(autoGenerate = true) var id: Long?,
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "barcode") var barcode: Long,
    @ColumnInfo(name = "ingredients") var ingredients: String,
    @ColumnInfo(name = "ingredient_types") var ingredient_types: String,
    @ColumnInfo(name = "info") var info: String
): Serializable