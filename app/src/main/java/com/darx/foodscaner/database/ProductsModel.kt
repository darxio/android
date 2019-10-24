package com.darx.foodscaner.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import java.util.*

@Entity(tableName = "products")
data class ProductsModel(@PrimaryKey(autoGenerate = true) var id: Int,
                      @ColumnInfo(name = "name") var name: String,
                      @ColumnInfo(name = "about") var about: String,
                      @ColumnInfo(name = "image_path") var imagePath: String,
                      @ColumnInfo(name = "ingredients") var ingredients: Array<String?>?,
                      @TypeConverters(TimestampConverter::class)
                      @ColumnInfo(name = "date")
                      var date: Date?
){
    constructor():this(0,"","", "", null, null)
}