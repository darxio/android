package com.darx.foodscaner.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query

@Dao
interface IngredientsDAO {
    @Query("SELECT * from ingredients")
    fun getAll(): List<IngredientModel>

    @Query("SELECT * from ingredients WHERE id = :id")
    fun getOne(id: Int): IngredientModel

    @Query("DELETE from ingredients")
    fun deleteAll()
}