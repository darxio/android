package com.darx.foodscaner.database


import androidx.room.TypeConverter
import com.darx.foodscaner.models.IngredientExtended
import com.google.gson.Gson




object ProductIngredientsConverter {
    @TypeConverter
    @JvmStatic
    fun jsonArrayToString(Contents: ArrayList<IngredientExtended>?): String {
        if (Contents == null) {
            return "NULL"
        }

        var a = ""
        for (s in Contents) {
            a += s.toString() + "|\t|"
        }

        return a
    }

    @TypeConverter
    @JvmStatic
    fun toJsonArray(s: String): ArrayList<IngredientExtended>? {
        if (s == "NULL") {
            return null
        }

        val ingredients = ArrayList<IngredientExtended>()
        val ings = s.split("|\t|")
        for (i in ings) {
            val ing = IngredientExtended()
            ingredients.add(ing.fromString(i))
        }

        return ingredients
    }
}

//
//object ProductIngredientsConverter {
//    @TypeConverter
//    @JvmStatic
//    fun jsonArrayToString(Contents: ArrayList<SerializableJSONObject>): String {
//        val sb = StringBuilder()
//        for (s in Contents) {
//            sb.append(s.get())
//            sb.append("|\t|")
//        }
//
//        return sb.toString()
//    }
//
//    @TypeConverter
//    @JvmStatic
//    fun toJsonArray(s: String): ArrayList<SerializableJSONObject> {
////        val sja = SerializableJSONArray(JSONArray(ings))
//        val ingredients = ArrayList<SerializableJSONObject>()
//        val ings = s.split("|\t|")
//        for (json in ings) {
//            try {
//                val j = SerializableJSONObject(JSONObject(json))
//                ingredients.add(j)
//            } catch (err: JSONException) {
//                Log.d("Error", err.toString())
//            }
//        }
//
//        return ingredients
//    }
//}