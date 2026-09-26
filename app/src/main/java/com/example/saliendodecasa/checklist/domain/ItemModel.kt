package com.example.saliendodecasa.checklist.domain

data class ItemModel(
    val id: Int = 0,
    val name: String,
    val listName: String,
    val isChecked: Boolean = false
)