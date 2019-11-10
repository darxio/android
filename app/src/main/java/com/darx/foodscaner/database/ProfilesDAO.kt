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

    @Insert(onConflict = REPLACE)
    fun add(profile: ProfileModel)
}