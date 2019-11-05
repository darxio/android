package com.darx.foodscaner.database


import android.util.Log
import androidx.room.TypeConverter
import com.google.gson.JsonArray
import com.google.gson.Gson
import com.darx.foodscaner.database.ProductModel
import com.darx.foodscaner.utils.SerializableJSONArray
import com.darx.foodscaner.utils.SerializableJSONObject
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONException



object ProductIngredientsConverter {
    @TypeConverter
    @JvmStatic
    fun jsonArrayToString(Contents: ArrayList<SerializableJSONObject>): String {
        val sb = StringBuilder()
        for (s in Contents) {
            sb.append(s.get())
            sb.append("|\t|")
        }

        return sb.toString()
    }

    @TypeConverter
    @JvmStatic
    fun toJsonArray(s: String): ArrayList<SerializableJSONObject> {
//        val sja = SerializableJSONArray(JSONArray(ings))
        val ingredients = ArrayList<SerializableJSONObject>()
        val ings = s.split("|\t|")
        for (json in ings) {
            try {
                val j = SerializableJSONObject(JSONObject(json))
                ingredients.add(j)
            } catch (err: JSONException) {
                Log.d("Error", err.toString())
            }
        }

        return ingredients
    }
}