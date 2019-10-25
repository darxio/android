package com.darx.foodscaner.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "excluded_ingredients")
data class ExcludedIngredientModel(
                    @PrimaryKey var id: Int,
                    @ColumnInfo(name = "name") var name: String,
                    @ColumnInfo(name = "about") var about: String,
                    @ColumnInfo(name = "image_path") var imagePath: String

){
    constructor():this(0,"","", "")
}