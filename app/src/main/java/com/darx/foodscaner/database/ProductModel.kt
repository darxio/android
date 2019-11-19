package com.darx.foodscaner.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.darx.foodscaner.models.IngredientExtended
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.*

@Entity(tableName = "products")
data class ProductModel(
    @PrimaryKey @ColumnInfo(name = "barcode") var barcode: Long,
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
    @ColumnInfo(name = "ok") var ok: Boolean = true
): Serializable {
    constructor():this(0,"","", "", ArrayList(), "",
        "","","","","", java.util.Calendar.getInstance().time, false, true)
}