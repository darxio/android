package com.darx.foodwise.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Entity(tableName = "groups")
data class GroupModel(@PrimaryKey(autoGenerate = true) var id: Int,
                      @ColumnInfo(name = "name") var name: String,
                      @ColumnInfo(name = "about") var about: String,
                      @SerializedName("image_link")
                      @ColumnInfo(name = "image") var imagePath: String,
                      var isInBase: Boolean

): Serializable {
    constructor():this(0,"","", "", false)
}