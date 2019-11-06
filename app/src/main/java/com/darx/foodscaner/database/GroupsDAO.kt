package com.darx.foodscaner.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query

@Dao
interface GroupsDAO {
    @Query("SELECT * from groups")
    fun getAll(): LiveData<List<GroupModel>>

    @Query("SELECT * from groups WHERE id = :id")
    fun getOne(id: Int): GroupModel

    @Insert(onConflict = REPLACE)
    fun add(group: GroupModel)

    @Delete
    fun deleteOne(group: GroupModel)

    @Query("DELETE from groups")
    fun deleteAll()
}