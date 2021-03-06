package com.darx.foodwise.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.*

@Entity(tableName = "products")
data class ProductModel(
    @PrimaryKey @ColumnInfo(name = "barcode") var barcode: Long,
    @ColumnInfo(name = "shrinked") var shrinked: Boolean,
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "description") var description: String?,
    @ColumnInfo(name = "contents") var contents: String?,
    @TypeConverters(ProductIngredientsConverter::class)
    @ColumnInfo(name = "ingredients") var ingredients: ArrayList<IngredientExtended>?,
    @SerializedName("category_url")
    @ColumnInfo(name = "category_url") var categoryURL: String?,
    @ColumnInfo(name = "mass") var mass: String?,
    @SerializedName("best_before")
    @ColumnInfo(name = "best_before") var bestBefore: String?,
    @ColumnInfo(name = "nutrition") var nutrition: String?,
    @ColumnInfo(name = "manufacturer") var manufacturer: String?,
    @ColumnInfo(name = "image") var image: String?,
    @TypeConverters(TimestampConverter::class)
    @ColumnInfo(name = "date")
        var date: Date = java.util.Calendar.getInstance().time,
    @ColumnInfo(name = "starred")
                    var starred: Boolean = false,
    @ColumnInfo(name = "scanned") var scanned: Boolean = false,
    var ok: Int = 0
): Serializable {
    constructor():this(0, false,"","", "", null, "",
        "","","","","", java.util.Calendar.getInstance().time, false, false,0)
}