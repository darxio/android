package com.darx.foodscaner.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlin.collections.ArrayList

object IngredientGroupsConverter {
    @TypeConverter
    @JvmStatic
    fun toString(groupsId: ArrayList<Int>?): String {
        val gson = Gson()
        return gson.toJson(groupsId)
    }

    @TypeConverter
    @JvmStatic
    fun toArrayList(s: String): ArrayList<Int>? {
        val listType = object : TypeToken<ArrayList<Int>>() {}.getType()
        return Gson().fromJson(s, listType)
    }

}