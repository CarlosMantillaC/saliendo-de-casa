package com.example.saliendodecasa.checklist.data

import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Insert
import androidx.room3.Query
import androidx.room3.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDao {

    @Query("SELECT * FROM items WHERE listName = :listName")
    fun getItemByList(listName: String): Flow<List<ItemEntity>>

    @Insert
    suspend fun insert(item: ItemEntity)

    @Update
    suspend fun update(item: ItemEntity)

    @Delete
    suspend fun delete(item: ItemEntity)

    @Query("UPDATE items SET isChecked = 0 WHERE listName = :listName")
    suspend fun resetChecks(listName: String)
}