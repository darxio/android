package com.darx.foodscaner.database

import android.util.Log
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class FruitModel : Serializable {
    @SerializedName("prediction") var prediction: String? = ""
    @SerializedName("accuracy") var accuracy: String? = ""

}