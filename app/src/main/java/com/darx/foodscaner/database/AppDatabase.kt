package com.darx.foodscaner.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(version = 2, entities = [GroupModel::class, ExcludedIngredientModel::class, IngredientModel::class])
abstract class AppDatabase : RoomDatabase() {
    abstract fun groupDAO(): GroupsDAO
    abstract fun ExcludedIngredientsDAO(): ExcludedIngredientsDAO
    abstract fun IngredientsDAO(): IngredientsDAO

    companion object {
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase? {
            if (INSTANCE == null) {
                synchronized(AppDatabase::class) {
                    INSTANCE = Room.databaseBuilder(
                        context.getApplicationContext(),
                        AppDatabase::class.java, "app.db"
                    )
                        .build()
                }
            }
            return INSTANCE
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }
}