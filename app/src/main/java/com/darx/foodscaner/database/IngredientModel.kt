package com.darx.foodscaner.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.darx.foodscaner.models.IngredientExtended
import java.io.Serializable

@Entity(tableName = "ingredients")
data class IngredientModel(
                    @PrimaryKey var id: Int,
                    @ColumnInfo(name = "name") var name: String,
                    @ColumnInfo(name = "danger") var danger: Int?,
                    @ColumnInfo(name = "description") var description: String?,
                    @ColumnInfo(name = "wiki_link") var wikiLink: String?,
                    @ColumnInfo(name = "image") var imagePath: String?


): Serializable {
    constructor():this(0,"", -1,"", "", "")
    constructor(ing: IngredientExtended):this() {
        this.name = ing.name;
        this.imagePath = "";
        this.description = ing.description;
        this.danger = ing.danger;
        this.wikiLink = ing.wiki_link;
    }
}
