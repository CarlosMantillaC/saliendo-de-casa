package com.example.saliendodecasa.checklist.data

import com.example.saliendodecasa.checklist.domain.ItemModel

fun convertToDomain(entity: ItemEntity): ItemModel {
    return ItemModel(
        id = entity.id,
        name = entity.name,
        listName = entity.listName,
        isChecked = entity.isChecked
    )
}

fun convertToEntity(model: ItemModel): ItemEntity {
    return ItemEntity(
        id = model.id,
        name = model.name,
        listName = model.listName,
        isChecked = model.isChecked
    )
}