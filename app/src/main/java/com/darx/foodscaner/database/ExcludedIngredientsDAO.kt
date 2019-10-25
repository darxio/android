package com.darx.foodscaner.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query

@Dao
interface ExcludedIngredientsDAO {
    @Query("SELECT * from excluded_ingredients")
    fun getAll(): List<ExcludedIngredientsModel>

    @Query("SELECT * from excluded_ingredients WHERE id = :id")
    fun getOne(id: Int): ExcludedIngredientsModel

    @Insert(onConflict = REPLACE)
    fun add(ingredient: ExcludedIngredientsModel)

    @Query("DELETE from excluded_ingredients WHERE id = :id")
    fun deleteOne(id: Int)

    @Query("DELETE from excluded_ingredients")
    fun deleteAll()
}