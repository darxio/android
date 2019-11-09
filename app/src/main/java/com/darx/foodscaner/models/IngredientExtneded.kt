package com.darx.foodscaner.models

import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class IngredientExtended :Serializable {
    @SerializedName("id") var id: Int = 0
    @SerializedName("name") var name: String = ""
    @SerializedName("danger") var danger: Int? = -1
    @SerializedName("description") var description: String? = ""
    @SerializedName("wiki_link") var wiki_link: String? = ""
    @SerializedName("ingredients") var ingredients: ArrayList<IngredientExtended>? = null

    fun fromString(s: String): IngredientExtended {
        if (s  == "") return IngredientExtended()
        val gson = Gson()
        var g = IngredientExtended()
        try {
            g = gson.fromJson(s, this.javaClass)
        } catch (err: IllegalStateException) {
            Log.d("ERROR", err.toString())
        }
        return g
    }

    override fun toString(): String {
        val gson = Gson()
        return gson.toJson(this)
    }
}