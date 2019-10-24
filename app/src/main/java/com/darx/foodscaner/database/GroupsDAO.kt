package com.darx.foodscaner.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query

@Dao
interface GroupsDAO {
    @Query("SELECT * from groups")
    fun getAll(): List<GroupsModel>

    @Query("SELECT * from groups WHERE id = :id")
    fun getOne(id: Int): GroupsModel

    @Insert(onConflict = REPLACE)
    fun add(group: GroupsModel)

    @Query("DELETE from groups WHERE id = :id")
    fun deleteOne(id: Int)

    @Query("DELETE from groups")
    fun deleteAll()
}