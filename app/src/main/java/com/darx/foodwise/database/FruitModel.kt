package com.darx.foodwise.database

import com.google.gson.annotations.SerializedName
import java.io.Serializable


class FruitModel : Serializable {
    @SerializedName("prediction") var prediction: String? = ""
    @SerializedName("accuracy") var accuracy: String? = ""

}