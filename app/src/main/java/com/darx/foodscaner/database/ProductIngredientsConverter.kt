package com.darx.foodscaner.database


import androidx.room.TypeConverter
import org.json.JSONObject

object ProductIngredientsConverter {
    @TypeConverter
    fun toJson(Contents: String): JSONObject {
        return JSONObject(Contents)
    }

    @TypeConverter
    fun jsonToString(Contents: JSONObject): String {
        return Contents.toString()
    }
}
