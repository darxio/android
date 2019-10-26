package com.darx.foodscaner.database


import androidx.room.TypeConverter
import org.json.JSONObject

object ProductIngredientsConverter {
    @TypeConverter
    @JvmStatic
    fun toJson(Contents: String): JSONObject {
        return JSONObject(Contents)
    }

    @TypeConverter
    @JvmStatic
    fun jsonToString(Contents: JSONObject): String {
        return Contents.toString()
    }
}