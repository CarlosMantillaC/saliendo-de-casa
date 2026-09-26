package com.example.saliendodecasa.checklist.domain

import kotlinx.coroutines.flow.Flow

interface ItemRepository {
    fun getItemByList(listName: String): Flow<List<ItemModel>>
    suspend fun insertItem(item: ItemModel)
    suspend fun updateItem(item: ItemModel)
    suspend fun deleteItem(item: ItemModel)
    suspend fun resetChecks(listName: String)
}