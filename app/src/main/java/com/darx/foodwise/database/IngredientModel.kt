package com.darx.foodwise.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import java.io.Serializable
import java.util.*

@Entity(tableName = "ingredients")
data class IngredientModel(
    @PrimaryKey var id: Int,
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "danger") var danger: Int?,
    @ColumnInfo(name = "description") var description: String?,
    @ColumnInfo(name = "wiki_link") var wikiLink: String?,
    @ColumnInfo(name = "image") var imagePath: String?,
    @TypeConverters(IngredientGroupsConverter::class)
    @ColumnInfo(name = "groups") var groups: ArrayList<Int> = ArrayList(),
    @ColumnInfo(name = "allowed") var allowed: Boolean? = true,
    var groupMached: Boolean = false,
    var ok: Boolean = true
)
    : Serializable {
    constructor():this(0,"", -1,"", "", "", ArrayList(), true, false, true)
    constructor(ing: IngredientExtended):this() {
        this.id = ing.id
        this.name = ing.name
        this.imagePath = ""
        this.description = ing.description
        this.danger = ing.danger
        this.wikiLink = ing.wiki_link
        this.groups = ArrayList()
        this.allowed = true
    }
}