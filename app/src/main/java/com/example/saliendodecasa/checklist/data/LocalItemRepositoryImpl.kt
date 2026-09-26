package com.example.saliendodecasa.checklist.data

import com.example.saliendodecasa.checklist.domain.ItemModel
import com.example.saliendodecasa.checklist.domain.ItemRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LocalItemRepositoryImpl(private val dao: ItemDao) : ItemRepository {
    override fun getItemByList(listName: String): Flow<List<ItemModel>> {
        return dao.getItemByList(listName).map { list -> list.map { convertToDomain(it) } }
    }

    override suspend fun insertItem(item: ItemModel) {
        dao.insert(convertToEntity(item))
    }

    override suspend fun updateItem(item: ItemModel) {
        dao.update(convertToEntity(item))
    }

    override suspend fun deleteItem(item: ItemModel) {
        dao.delete(convertToEntity(item))
    }

    override suspend fun resetChecks(listName: String) {
        dao.resetChecks(listName)
    }
}