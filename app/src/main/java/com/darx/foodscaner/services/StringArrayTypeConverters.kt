package com.darx.foodscaner.services

import androidx.room.TypeConverter
import java.util.Collections.emptyList
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*


class StringArrayTypeConverters {

    var gson = Gson()

    @TypeConverter
    fun stringToArray(data: String?): List<String> {
        if (data == null) {
            return Collections.emptyList()
        }

        val listType = object : TypeToken<List<String>>() {

        }.getType()

        return gson.fromJson<List<String>>(data, listType)
    }

    @TypeConverter
    fun arrayToString(arrayList: List<String>): String {
        return gson.toJson(arrayList)
    }
}