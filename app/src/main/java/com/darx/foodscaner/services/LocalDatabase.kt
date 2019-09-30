package com.darx.foodscaner.services

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.darx.foodscaner.data.Product
import com.darx.foodscaner.data.db.ProductDao


@Database(
    entities = [Product::class],
    version = 1
)
abstract class LocalDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao

    companion object {
        @Volatile private var instace: LocalDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instace ?: synchronized(LOCK) {
            instace ?: buildDatabase(context).also { instace = it }
        }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context.applicationContext,
                LocalDatabase::class.java, "local.db")
                .build()
    }
}