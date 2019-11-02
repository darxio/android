package com.darx.foodscaner.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import org.json.JSONObject
import java.io.Serializable
import java.util.*

@Entity(tableName = "favourite_products")
data class FavouriteProductModel(
    @PrimaryKey @ColumnInfo(name = "barcode") var barcode: Long,
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "description") var description: String,
//    @TypeConverters(TimestampConverter::class)
//    @ColumnInfo(name = "Ingredients") var contents: JSONObject?,
    @ColumnInfo(name = "Ingredients") var ingredients: String,
    @ColumnInfo(name = "category_url") var categoryURL: String,
    @ColumnInfo(name = "mass") var mass: String,
    @ColumnInfo(name = "bestbefore") var bestBefore: String,
    @ColumnInfo(name = "nutrition") var nutrition: String,
    @ColumnInfo(name = "manufacturer") var manufacturer: String,
    @ColumnInfo(name = "image") var image: String
): Serializable {
    constructor():this(0,"","", "", "", "","","","","")
}
