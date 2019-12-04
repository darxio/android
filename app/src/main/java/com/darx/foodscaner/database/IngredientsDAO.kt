package com.darx.foodscaner.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query

@Dao
interface IngredientsDAO {
    @Query("SELECT * from ingredients")
    fun getAll(): LiveData<List<IngredientModel>>

    @Query("SELECT * from ingredients WHERE id = :id")
    fun getOne(id: Int): LiveData<IngredientModel>

    @Query("SELECT * from ingredients WHERE allowed == 0")
    fun getNotAllowed(): LiveData<List<IngredientModel>>

    @Insert(onConflict = REPLACE)
    fun add(ingredient: IngredientModel)

    @Query("DELETE from ingredients WHERE id = (SELECT id from ingredients WHERE allowed = 1 and groups like :groups)")
    fun deleteIngrOfGroup_(groups: List<Int>)

    @Delete
    fun deleteOne(ingredient: IngredientModel)

    @Query("DELETE from ingredients")
    fun deleteAll()
}