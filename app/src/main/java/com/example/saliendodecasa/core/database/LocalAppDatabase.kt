package com.example.saliendodecasa.core.database

import android.content.Context
import androidx.room3.Database
import androidx.room3.Room
import androidx.room3.RoomDatabase
import com.example.saliendodecasa.checklist.data.ItemDao
import com.example.saliendodecasa.checklist.data.ItemEntity

@Database(entities = [ItemEntity::class], version = 1, exportSchema = false)
abstract class LocalAppDatabase : RoomDatabase() {
    abstract fun itemDao(): ItemDao

    companion object {
        private var instance: LocalAppDatabase? = null

        fun getDatabase(context: Context): LocalAppDatabase {
            return instance ?: synchronized(this) {
                val newInstance = Room.databaseBuilder(
                    context.applicationContext,
                    LocalAppDatabase::class.java,
                    "saliendo_de_casa_local_db"
                ).build()

                instance = newInstance
                newInstance
            }
        }
    }
}