package com.example.saliendodecasa.checklist.presentation

import com.example.saliendodecasa.checklist.domain.ItemModel

data class MainUiState(
    val currentListName: String = "General",
    val items: List<ItemModel> = emptyList()
)

