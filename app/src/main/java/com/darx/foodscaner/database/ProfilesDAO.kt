package com.darx.foodscaner.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query

@Dao
interface ProfilesDAO {
    @Query("SELECT * from profiles")
    fun getAll(): LiveData<List<ProfileModel>>

    @Query("SELECT * from profiles WHERE id = :id")
    fun getOne(id: Int): LiveData<ProfileModel>

    @Insert(onConflict = REPLACE)
    fun add(profile: ProfileModel)

    @Delete
    fun deleteOne(group: ProfileModel)

    @Query("DELETE from profiles")
    fun deleteAll()
}
