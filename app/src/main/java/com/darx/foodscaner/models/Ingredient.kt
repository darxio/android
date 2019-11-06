package com.darx.foodscaner.models

import android.util.Log
import com.google.gson.annotations.SerializedName
//
//class Ingredient {
////    data class Ingredient (
//        val name: String,
//        val danger: Int = -1,
//        val ingredients: ArrayList<Ingredient> = ArrayList<Ingredient>()
//
////    )
////
////    data class IngredientExtended (
////        val name: String,
////        val danger: Int?,
////        val description: String?,
////        val wiki_link: String?,
////        val ingredients: Ingredient?
////    )
//
//}

class IngredientExtended (
) {
    @SerializedName("name") var name: String = ""
    @SerializedName("danger") var danger: Int? = -1
    @SerializedName("description") var description: String? = ""
    @SerializedName("wiki_link") var wiki_link: String? = ""
    @SerializedName("ingredients") var ingredients: ArrayList<IngredientExtended>? = null

    fun fromString(s: String): IngredientExtended {
        val vals = s.split("|\$|")
        try {
            this.name = vals[0].split(" = ")[1]
            this.danger = vals[1].split(" = ")[1].toInt()
            this.description = vals[2].split(" = ")[1]
            this.wiki_link = vals[3].split(" = ")[1]
        } catch (err: IndexOutOfBoundsException) {
            Log.d("ERROR", err.toString())
//            Log.d("ERROR_INFO", s)
        }

//        val ing = vals[4].split(" = ")[1]
//        val ing_str = ing.substring(1, ing.length-1)
//        if (ing_str != "") {
//            val i = this.fromString(ing_str)
//            this.ingredients?.add(i)
//        } else {
            this.ingredients = null
//        }
        return this
    }

    override fun toString(): String {
        var ings = ""
//        if (ingredients != null) {
//            ings = ingredients.toString()
//        }

        return "name = "+name+"|\$|danger = "+danger+"|\$|wiki_link = "+wiki_link+
                "|\$|description = "+description+"|\$|ingredients = "+ings+""
    }
}