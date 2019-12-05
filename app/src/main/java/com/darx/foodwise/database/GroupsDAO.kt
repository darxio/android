package com.darx.foodwise.database

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
    fun getOne(id: Int): LiveData<GroupModel>

    @Query("SELECT id from groups")
    fun getAllIds(): LiveData<List<Int>>

    @Query("SELECT COUNT(*) > 0 from groups WHERE id IN (:ids)")
    fun checkAll(ids: List<Int>): LiveData<Boolean>

    @Query("SELECT COUNT(*) from groups WHERE id = :id")
    fun find(id: Int): Int

    @Insert(onConflict = REPLACE)
    fun add(group: GroupModel)

    @Delete
    fun deleteOne(group: GroupModel)

    @Query("DELETE from groups")
    fun deleteAll()
}