package com.darx.foodscaner.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query

@Dao
interface GroupDAO {
    @Query("SELECT * from groups")
    fun getAll(): List<GroupModel>

    @Query("SELECT * from groups WHERE id = :id")
    fun getOne(id: Int): GroupModel

    @Insert(onConflict = REPLACE)
    fun add(group: GroupModel)

    @Query("DELETE from groups WHERE id = :id")
    fun deleteOne(id: Int)

    @Query("DELETE from groups")
    fun deleteAll()
}