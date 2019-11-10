package com.darx.foodscaner.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "profiles")
data class ProfileModel(@PrimaryKey(autoGenerate = true) var id: Int,
                        @ColumnInfo(name = "tutorial") var tutorial: Boolean
): Serializable {
    constructor():this(0,true)
}