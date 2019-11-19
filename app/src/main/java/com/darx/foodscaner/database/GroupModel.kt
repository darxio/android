package com.darx.foodscaner.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "groups")
data class GroupModel(@PrimaryKey(autoGenerate = true) var id: Int,
                      @ColumnInfo(name = "name") var name: String,
                      @ColumnInfo(name = "about") var about: String,
                      @ColumnInfo(name = "image") var imagePath: String

): Serializable {
    constructor():this(0,"","", "")
}