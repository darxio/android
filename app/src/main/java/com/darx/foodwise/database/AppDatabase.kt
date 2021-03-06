package com.darx.foodwise.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(version = 49, entities = [GroupModel::class, IngredientModel::class, ProductModel::class])
@TypeConverters(TimestampConverter::class, ProductIngredientsConverter::class, IngredientGroupsConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun groupsDAO(): GroupsDAO
    abstract fun ingredientsDAO(): IngredientsDAO
    abstract fun productsDAO(): ProductsDAO

    companion object {
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase? {
            if (INSTANCE == null) {
                synchronized(AppDatabase::class) {
                    INSTANCE = Room.databaseBuilder(
                        context.getApplicationContext(),
                        AppDatabase::class.java, "app.db"
                    )
                        .fallbackToDestructiveMigration()
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