package com.example.saliendodecasa.checklist.data

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "items")
data class ItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val listName: String,
    val isChecked: Boolean
)
