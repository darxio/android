package com.darx.foodscaner.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(version = 4, entities = [GroupModel::class, IngredientModel::class, ExcludedIngredientModel::class
    , ProductModel::class, FavouriteProductModel::class, ScannedProductModel::class])
@TypeConverters(TimestampConverter::class, ProductIngredientsConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun groupsDAO(): GroupsDAO
    abstract fun ingredientsDAO(): IngredientsDAO
    abstract fun excludedIngredientsDAO(): ExcludedIngredientsDAO
    abstract fun productsDAO(): ProductsDAO
    abstract fun favouriteProductsDAO(): FavouriteProductsDAO
    abstract fun scannedProductsDAO(): ScannedProductsDAO


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